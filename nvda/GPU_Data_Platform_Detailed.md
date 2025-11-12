# GPU Data Platform — Detailed Data Flow & Full Sample Code

This upgraded document adds:
- **A detailed data-flow graph** (Mermaid + ASCII fallback)
- **End-to-end sample code** for all important components (ingestion, control plane, execution, GPU configs, storage/analytics, observability, scaling, security, TTFQ, fault tolerance, runbooks).

---

## 1) Detailed Data-Flow Graph

### 1.1 Mermaid Diagram (if your viewer supports Mermaid)
```mermaid
flowchart TD
  subgraph User[User Layer]
    U1[Submit Job<br/>UI/CLI/API]
  end

  subgraph API[API & Auth]
    A1[API Gateway (REST/gRPC)]
    A2[Auth (OAuth2/IAM)]
  end

  subgraph CP[Control Plane (CPU)]
    C1[Job Orchestrator<br/>(enqueue, track state)]
    C2[Resource Manager<br/>(quota, labels, MIG policy)]
    C3[Admission Controller<br/>(warm pool, pre-pull, TTFQ)]
    C4[Metadata & Checkpoints]
  end

  subgraph EXE[Execution Plane (GPU+CPU)]
    D1[Driver Pod (Spark/Ray Head)]
    E1[Executor Pods (GPU)<br/>RAPIDS/cuDF]
    E2[Local Cache (NVMe/RMM)]
    G1[GPU Metrics Agent (DCGM)]
  end

  subgraph PIPE[Data Pipeline]
    K1[Kafka/Flink Ingestion]
    S3[(S3/GCS/HDFS\nParquet/Delta/Iceberg)]
    AQL[Analytics Engines\nPresto/Trino/BigQuery\nBlazingSQL]
  end

  subgraph OBS[Observability]
    P1[Prometheus]
    G[Grafana]
    L[Loki + FluentBit]
    AL[Alertmanager/PagerDuty]
  end

  %% Control flow
  U1 --> A1 --> A2 --> C1 --> C2 --> C3 --> C4 --> D1
  %% Resource allocation
  C2 -. GPU alloc .-> E1
  %% Data flow
  D1 --> E1 --> E2 --> S3
  K1 --> D1
  K1 --> E1
  S3 --> AQL
  %% Monitoring feedback
  E1 --> G1 --> P1
  D1 --> L
  E1 --> L
  P1 --> G
  P1 --> AL
  L --> G
```

### 1.2 ASCII Fallback (labels show flows)
```
[User] -> [API Gateway + Auth] -> [Control Plane: Orchestrator -> ResourceMgr -> Admission -> Metadata]
                 |                              |(alloc GPU)                   |
                 v                              v                              v
            [Driver Pod]  ------------------> [Executor Pods (GPU)] --> [Local Cache] --> [S3/Delta/Iceberg]
               ^   |                              ^    |                                        |
               |   v                              |    v                                        v
           [Logs -> Loki]                  [DCGM -> Prometheus] ---------------------------> [Grafana/Alerts]
  [Kafka/Flink] ---> feeds ---> [Driver/Executors] --------------> [Results -> Analytics: Presto/Trino/BlazingSQL]
```

---

## 2) Control Plane

### 2.1 FastAPI Job Submission (with Prometheus counter)
```python
# api/main.py
from fastapi import FastAPI
from pydantic import BaseModel
import time, uuid
from prometheus_client import Counter, CollectorRegistry, push_to_gateway

app = FastAPI()
registry = CollectorRegistry()
JOB_SUBMITTED = Counter("jobs_submitted_total", "Jobs submitted", registry=registry)

class JobReq(BaseModel):
    framework: str  # spark or ray
    image: str
    gpus: float = 1.0
    entrypoint: str
    args: list[str] = []
    priority: int = 0

@app.post("/submit")
def submit(job: JobReq):
    job_id = str(uuid.uuid4())
    # enqueue to Kafka/Redis omitted
    JOB_SUBMITTED.inc()
    try:
        push_to_gateway("pushgateway:9091", job="api", registry=registry)
    except Exception:
        pass
    return {"job_id": job_id, "queued_at": int(time.time())}
```

### 2.2 Admission Controller (pseudo: warm-pool & pre-pull check)
```python
def admit(job):
    if not image_in_node_cache(job.image):
        schedule_prepull(job.image)
        return {"admit": False, "reason": "prepulling"}
    if not warm_pool_available(job.framework):
        spin_up_warm_pods(job.framework)
        return {"admit": False, "reason": "warming"}
    return {"admit": True}
```

---

## 3) Ingestion

### 3.1 Kafka Connect Debezium (MySQL CDC) — `connector.json`
```json
{
  "name": "mysql-cdc",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "*****",
    "database.server.id": "5400",
    "database.server.name": "mysqlsrv",
    "database.whitelist": "appdb",
    "table.whitelist": "appdb.events",
    "include.schema.changes": "true",
    "database.history.kafka.bootstrap.servers": "kafka:9092",
    "database.history.kafka.topic": "schema-changes"
  }
}
```

### 3.2 Spark Structured Streaming — Kafka → Parquet with checkpoint
```python
from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("ingest").getOrCreate()
raw = (spark.readStream.format("kafka")
       .option("kafka.bootstrap.servers", "kafka:9092")
       .option("subscribe", "events").load())

# parse your schema (omitted), select fields → df
df = raw.selectExpr("CAST(value AS STRING) as json")

query = (df.writeStream
  .format("parquet")
  .option("path","s3://lake/events/")
  .option("checkpointLocation","s3://lake/_chk/events/")
  .partitionBy("date","hour")
  .outputMode("append")
  .start())
query.awaitTermination()
```

---

## 4) Execution on Kubernetes (GPU)

### 4.1 SparkApplication (RAPIDS & fractional GPU)
```yaml
apiVersion: sparkoperator.k8s.io/v1beta2
kind: SparkApplication
metadata:
  name: rapids-etl
  namespace: dp-jobs
spec:
  type: Scala
  mode: cluster
  image: nvcr.io/spark-rapids/spark:24.08
  mainClass: com.company.ETL
  sparkConf:
    "spark.plugins": com.nvidia.spark.SQLPlugin
    "spark.rapids.sql.enabled": "true"
    "spark.executor.resource.gpu.amount": "1"
    "spark.task.resource.gpu.amount": "0.125"
    "spark.dynamicAllocation.enabled": "true"
  driver:
    cores: 2
    memory: 4g
    serviceAccount: spark-sa
  executor:
    instances: 8
    cores: 4
    memory: 16g
    nodeSelector:
      accelerator: "nvidia-gpu"
    tolerations:
      - key: nvidia.com/gpu
        operator: Exists
        effect: NoSchedule
    gpu:
      name: nvidia.com/gpu
      quantity: 1
```

### 4.2 Ray fine-grained tasks with placement groups
```python
import ray
from ray.util.placement_group import placement_group

ray.init()

pg = placement_group([{"GPU": 1}] * 4, strategy="PACK")

@ray.remote(num_gpus=0.25, scheduling_strategy="PLACEMENT_GROUP")
def infer(x):
    import cudf
    return int(x) * int(x)

futs = [infer.options(placement_group=pg).remote(i) for i in range(32)]
print(sum(ray.get(futs)))
```

### 4.3 K8s Pod with GPU (MIG/taints/nodeSelector)
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: gpu-worker
spec:
  tolerations:
    - key: "nvidia.com/gpu"
      operator: "Exists"
      effect: "NoSchedule"
  nodeSelector:
    accelerator: "nvidia-gpu"
  containers:
    - name: worker
      image: nvcr.io/nvidia/rapidsai/rapidsai:24.08-cuda12-runtime
      resources:
        limits:
          nvidia.com/gpu: 1
      env:
        - name: NVIDIA_VISIBLE_DEVICES
          value: "all"
```

---

## 5) GPU ETL / SQL

### 5.1 PySpark + RAPIDS
```python
from pyspark.sql import SparkSession

spark = (SparkSession.builder
  .appName("rapids-etl")
  .config("spark.plugins", "com.nvidia.spark.SQLPlugin")
  .config("spark.rapids.sql.enabled", "true")
  .getOrCreate())

df = spark.read.parquet("s3://lake/events/date=2025-10-07/")
res = (df.filter("event_type='click'")
         .groupBy("campaign_id")
         .count()
         .withColumnRenamed("count", "clicks"))
res.write.mode("overwrite").parquet("s3://lake/agg/clicks/date=2025-10-07/")
```

### 5.2 BlazingSQL (GPU SQL)
```python
from blazingsql import BlazingContext
bc = BlazingContext()
bc.create_table('events', 's3://lake/events/date=2025-10-07/*.parquet')
q = bc.sql(\"\"\"\nSELECT campaign_id, COUNT(*) AS clicks\nFROM events\nWHERE event_type='click'\nGROUP BY campaign_id\n\"\"\")\nprint(q.head())\n```

---

## 6) Storage & Table Formats

### 6.1 Delta Lake table create (Spark SQL)
```sql
CREATE TABLE delta.`s3://lake/delta/clicks` (
  campaign_id STRING,
  clicks BIGINT,
  date STRING
) USING delta
PARTITIONED BY (date);
```

### 6.2 Iceberg table evolution (Spark SQL)
```sql
ALTER TABLE prod.db.clicks ADD COLUMN country STRING;
ALTER TABLE prod.db.clicks SET LOCATION 's3://lake/iceberg/clicks/';
```

---

## 7) Observability

### 7.1 DCGM Exporter DaemonSet
```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata: { name: dcgm-exporter }
spec:
  selector: { matchLabels: { app: dcgm } }
  template:
    metadata: { labels: { app: dcgm } }
    spec:
      hostPID: true
      containers:
        - name: exporter
          image: nvcr.io/nvidia/k8s/dcgm-exporter:3.3.6
          ports: [{ containerPort: 9400, name: metrics }]
```

### 7.2 Prometheus scrape config
```yaml
scrape_configs:
  - job_name: "nvidia-dcgm"
    static_configs:
      - targets: ["dcgm-exporter:9400"]
```

### 7.3 Prometheus alert rule
```yaml
groups:
- name: gpu.rules
  rules:
  - alert: GPUIdleTooHigh
    expr: avg_over_time(nvidia_gpu_utilization[10m]) < 10
    for: 15m
    labels: { severity: warning }
    annotations:
      summary: "GPU utilization low"
      description: "GPUs underutilized for 15m"
```

### 7.4 FluentBit → Loki (logs)
```yaml
[INPUT]
  Name tail
  Path /var/log/containers/*.log
[OUTPUT]
  Name loki
  Match *
  Url http://loki:3100/loki/api/v1/push
  Labels job=fluentbit
```

---

## 8) Autoscaling & TTFQ

### 8.1 KEDA ScaledObject (scale by Kafka lag)
```yaml
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: spark-ingest-scaled
spec:
  scaleTargetRef:
    name: spark-ingest-deployment
  triggers:
    - type: kafka
      metadata:
        bootstrapServers: kafka:9092
        topic: events
        lagThreshold: "10000"
```

### 8.2 Image pre-pull & warm pool
```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata: { name: image-prepull }
spec:
  selector: { matchLabels: { app: prepull } }
  template:
    metadata: { labels: { app: prepull } }
    spec:
      containers:
        - name: prepull
          image: nvcr.io/nvidia/rapidsai/rapidsai:24.08-cuda12-runtime
          command: ["bash","-c","sleep 3600"]
---
apiVersion: apps/v1
kind: Deployment
metadata: { name: warm-pool }
spec:
  replicas: 5
  selector: { matchLabels: { app: warm } }
  template:
    metadata: { labels: { app: warm } }
    spec:
      containers:
        - name: worker
          image: nvcr.io/nvidia/rapidsai/rapidsai:24.08-cuda12-runtime
          resources: { limits: { nvidia.com/gpu: 1 } }
          command: ["bash","-c","sleep infinity"]
```

---

## 9) Security

### 9.1 RBAC Role
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: dp-jobs
  name: spark-executor-role
rules:
- apiGroups: [""]
  resources: ["pods","pods/log"]
  verbs: ["get","list"]
- apiGroups: ["batch"]
  resources: ["jobs"]
  verbs: ["create","delete","get"]
```

### 9.2 NetworkPolicy (deny-all + allow metrics)
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata: { name: deny-all, namespace: dp-jobs }
spec:
  podSelector: {}
  policyTypes: ["Ingress","Egress"]
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata: { name: allow-metrics, namespace: dp-jobs }
spec:
  podSelector:
    matchLabels: { app: dcgm }
  ingress:
    - from: [{ podSelector: { matchLabels: { app: prometheus } } }]
      ports: [{ port: 9400, protocol: TCP }]
```

---

## 10) Fault Tolerance

### 10.1 Exactly-once + idempotent writes (Spark)
```python
def sink(batch_df, batch_id):
    # dedupe by primary key to avoid double write on retry
    from pyspark.sql.functions import col
    dedup = batch_df.dropDuplicates(["pk"])
    (dedup.write
      .mode("append")
      .parquet("s3://out/data/"))

stream = (df.writeStream
  .option("checkpointLocation","s3://chk/jobA/")
  .foreachBatch(sink)
  .outputMode("append")
  .start())
```

### 10.2 Retry decorator
```python
import time, functools
def retry(max_tries=5, backoff=0.5):
    def deco(fn):
        @functools.wraps(fn)
        def wrapper(*a, **kw):
            t, d = 0, backoff
            while True:
                try: return fn(*a, **kw)
                except Exception:
                    t += 1
                    if t >= max_tries: raise
                    time.sleep(d); d *= 2
        return wrapper
    return deco
```

---

## 11) On-call Runbook (CLI Cheatsheet)
```bash
# 1) Check pending pods
kubectl get pods -A | grep Pending
kubectl describe node <gpu-node>

# 2) Restart stuck driver
kubectl -n dp-jobs rollout restart deployment/rapids-etl-driver

# 3) Cordon + drain a bad node
kubectl cordon <node> && kubectl drain <node> --ignore-daemonsets --delete-emptydir-data

# 4) Quick GPU metrics
kubectl -n monitoring port-forward svc/prometheus 9090:9090
open http://localhost:9090

# 5) Check DCGM
kubectl -n monitoring get pods | grep dcgm
```

---

## 12) Dask-CUDA (optional) — start a GPU cluster
```python
from dask_cuda import LocalCUDACluster
from dask.distributed import Client
cluster = LocalCUDACluster()  # auto one worker per GPU
client = Client(cluster)

import dask_cudf as dc
gdf = dc.read_parquet("s3://lake/events/*.parquet")
res = gdf[gdf.event_type=="click"].groupby("campaign_id").size().compute()
print(res.head())
```

---

## 13) Interview Talking Points (30-sec bullets)
- **Spark = Batch DAG** for massive ETL; **RAPIDS** shifts SQL ops to GPU.
- **Ray = Fine-grained** task/actor; flexible for ML inference & pipelines.
- **Dask = Pythonic parallel** on arrays/dataframes; quick wins with RAPIDS.
- **K8s + DCGM** turn GPUs into a **shared, observable, auto-scaled platform**.
- **TTFQ** ↓ via **pre-pull & warm pool**; **reliability** via checkpoint+retry.
- **Hybrid pipeline**: **Kafka→GPU compute→Delta/Iceberg→Presto** for analytics.

---
