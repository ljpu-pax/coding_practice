
# 🚀 GPU-Aware Job Scheduling and Orchestration (NVIDIA DGX Cloud Interview Prep)

---

## 📌 0. Quick Reference (60s Pitch + Checklist)

**60s Pitch**
- GPUs are special resources in K8s bridged by the NVIDIA Device Plugin and GPU Operator. I design schedulers that are MIG- and NVLink-aware to maximize utilization while preserving performance. I use DCGM telemetry for health-aware placement, checkpoint for fault tolerance, and Temporal to orchestrate the full lifecycle. I balance fairness with efficiency using quotas, priorities, and preemption.

**Day-of Interview Checklist**
- Clarify workload: batch vs service, single- vs multi-GPU, model size, dataset locality.
- Resource granularity: full GPU vs MIG profiles; need for NVLink locality.
- SLOs: time-to-start, throughput/latency, failure recovery time, cost/utilization targets.
- Health policy: temperature/ECC thresholds, drain/reschedule rules.
- Fault tolerance: checkpoint cadence, object store, resume semantics.
- Fairness: tenant quotas, priorities, preemption/aging.

**Top Talking Points**
- MIG fragmentation avoidance: bin packing, predictive slicing, defragment windows.
- NVLink/NVSwitch-aware placement for multi-GPU jobs; topology hints/labels.
- Telemetry-driven decisions: DCGM → Prometheus → Alertmanager → workflow signal.
- Temporal workflows for retries, rollbacks, and auditability.
- Tradeoffs: utilization vs performance isolation; checkpoint frequency vs I/O cost.

## 📑 Table of Contents

- 0. Quick Reference
- 1. Core Concepts
- 2. Key Challenges & Optimizations
- 3. Architecture Summary
- 4. Minimal Examples
- 5. Top Q&A (brief)
- 6. Interview Takeaways
- Appendices A–H (details, diagrams, commands, metrics, scenarios, behavioral)

## 📌 1. Core Concepts

- **Kubernetes (K8s) Cluster**
  - Orchestrates resources (CPU, memory, GPU) across nodes.
  - Pods are the smallest scheduling unit, containing one or more containers.

- **GPU in K8s**
  - GPUs are not a native K8s resource.
  - **NVIDIA Device Plugin** exposes GPUs/MIG slices as allocatable resources (`nvidia.com/gpu`, `nvidia.com/mig-1g.10gb`).
  - **NVIDIA GPU Operator** automates drivers, CUDA, DCGM (telemetry).

- **MIG (Multi-Instance GPU)**
  - Splits a large GPU (e.g., A100/H100) into smaller slices (e.g., 1g.10gb, 2g.20gb).
  - Increases utilization but can cause **fragmentation**.
  - Optimization: **Bin Packing** & **Predictive slicing**.

- **NVLink / NVSwitch**
  - High-bandwidth interconnect between GPUs.
  - Scheduler should place multi-GPU jobs within the same NVLink domain.

- **Telemetry & Health**
  - Metrics (temperature, ECC errors, utilization) from **DCGM / nvidia-smi**.
  - Health-aware scheduling avoids unhealthy GPUs.

- **Checkpoint & Failover**
  - Long-running jobs must checkpoint to S3/GCS.
  - On failure, scheduler restarts job from last checkpoint.

- **Temporal Orchestration**
  - Durable workflow engine to manage **job lifecycle**: submission → scheduling → monitoring → checkpointing → teardown.
  - Handles retries, failover, and cross-system orchestration (K8s, storage, telemetry).

---

## 📌 2. Key Challenges & Optimizations

| Challenge            | Optimization Strategy |
|----------------------|------------------------|
| **MIG Fragmentation** | Bin Packing, Predictive slicing |
| **Multi-GPU Topology** | NVLink/NVSwitch-aware placement |
| **Unhealthy GPUs**   | Health-aware scheduling (temperature/ECC filtering) |
| **Long-running jobs** | Checkpoint + Resume |
| **Fairness**         | Quota per tenant, Priority + Preemption |
| **Observability**    | Telemetry pipeline (DCGM → Prometheus/Thanos → Alertmanager) |

---

## 📌 3. Architecture Summary

- Control plane: Temporal workflows drive lifecycle; K8s scheduler with GPU plugin; GPU Operator handles drivers/CUDA/DCGM.
- Data plane: Pods on worker nodes consume GPUs (full or MIG), ideally co-located on NVLink/NVSwitch for multi-GPU jobs.
- Telemetry: DCGM → Prometheus/Thanos → Alertmanager → signals back to workflows for reschedule/drain.
- Resilience: periodic checkpoints to object store; restart from last good state.
- Fairness: quotas, PriorityClasses, preemption, and aging for multi-tenant balance.

See Appendix C for the full ASCII end-to-end diagram.

## 📌 4. Minimal Examples

### Pod YAML requesting GPU
```yaml
resources:
  limits:
    nvidia.com/gpu: 1
```

### Pod YAML requesting MIG slice
```yaml
resources:
  limits:
    nvidia.com/mig-1g.10gb: 1
```

Tip: prefer DCGM + scheduler predicates instead of ad-hoc client-side selection.

---

## 📌 5. Top Q&A (brief)

Q1: GPU assignment? Device Plugin registers; scheduler binds; runtime mounts `/dev/nvidia*`.

Q2: MIG value? Partition large GPUs; watch fragmentation; predefine common profiles.

Q3: Avoid fragmentation? Bin pack; predictive slicing; defrag windows.

Q4: NVLink? Co-locate multi-GPU jobs within same NVLink/NVSwitch domain.

Q5: ECC policy? Drain on uncorrected; threshold on corrected; health-aware placement.

Q6: Fault tolerance? Periodic checkpoints; workflow-driven restart on failure.

Q7: Temporal’s role? Durable orchestration across K8s, storage, telemetry.

Q8: Fairness? Quotas, priorities/preemption, aging anti-starvation.

---

## 📌 6. Advanced YAML and Scheduling Strategies

### A. Node/GPU Topology-Aware Placement
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: trainer-topology-aware
spec:
  nodeSelector:
    nvidia.com/gpu.present: "true"
  topologySpreadConstraints:
    - maxSkew: 1
      topologyKey: kubernetes.io/hostname
      whenUnsatisfiable: ScheduleAnyway
      labelSelector:
        matchLabels:
          app: trainer
  containers:
  - name: trainer
    image: nvcr.io/nvidia/pytorch:24.06-py3
    resources:
      limits:
        nvidia.com/gpu: 2
```

### B. MIG Profile Requests + Affinity
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: trainer-mig
spec:
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: nvidia.com/mig.strategy
            operator: In
            values: [single]
  containers:
  - name: trainer
    image: nvcr.io/nvidia/pytorch:24.06-py3
    resources:
      limits:
        nvidia.com/mig-1g.10gb: 2
```

### C. Priority, Preemption, and Quotas
```yaml
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: critical-ml
value: 100000
globalDefault: false
preemptionPolicy: PreemptLowerPriority
description: "Critical ML jobs"
```

### D. Job with Checkpoint Sidecar
```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: trainer-checkpointing
spec:
  template:
    spec:
      restartPolicy: OnFailure
      containers:
      - name: trainer
        image: nvcr.io/nvidia/pytorch:24.06-py3
        resources:
          limits:
            nvidia.com/gpu: 1
        volumeMounts:
        - name: ckpt
          mountPath: /ckpt
      - name: uploader
        image: amazon/aws-cli
        args: ["s3", "sync", "/ckpt", "s3://bucket/checkpoints/"]
        volumeMounts:
        - name: ckpt
          mountPath: /ckpt
      volumes:
      - name: ckpt
        emptyDir: {}
```

### Scheduling Notes
- Prefer even-numbered GPUs on the same NVLink domain for all-reduce.
- For MIG, group same-profile slices to reduce internal fragmentation.
- Keep a defragmentation window to collapse MIG layouts during low traffic.
- Encode topology with node labels/extended resources where available.

---

## 📌 6. Interview Takeaways

- Emphasize **system-level view**: User → Workflow (Temporal) → K8s → Node → Pod → Container → GPU/MIG.
- Know the **pain points**: fragmentation, topology, health, fairness.
- Use **analogies**:
  - Scheduler = airport control tower
  - MIG = slicing pizza into portions
  - NVLink = seating people at the same table for fast communication
  - Checkpoint = saving progress in a video game

---

✅ Use this document as a **Notion interview prep page**. It contains concepts, diagrams, Q&A, and sample code for quick review.


---

## 📎 Appendix A. Extended Discussions

### GPU Scheduling vs CPU Scheduling
- **CPU**: handled by Linux kernel + cgroups, relatively uniform cores.  
- **GPU**: heterogeneous resources (MIG slices, NVLink domains, different GPU models).  
- GPU scheduling must consider:  
  - Memory per slice  
  - Topology (intra-node vs inter-node)  
  - Health metrics (temperature, ECC errors)  
  - Long job fault tolerance (checkpointing)

### GPU Telemetry (DCGM)
- DCGM (Data Center GPU Manager) provides metrics: utilization, memory usage, ECC error counts, temperatures, clocks.  
- These metrics are scraped by Prometheus and influence scheduling decisions.  
- Example: draining a node when ECC error count crosses threshold.

### Health-aware Scheduling Example
- If 4 GPUs are available, but 1 reports high temperature or frequent ECC errors → scheduler must **exclude** it.  
- Temporal workflow signals K8s to reschedule job on another healthy node.

### Checkpointing Strategies
- **Time-based**: save every X minutes.  
- **Iteration-based**: save every N training steps.  
- **Adaptive**: save more frequently during early convergence.  
- Tradeoff: frequency vs I/O overhead.

---

## 📎 Appendix B. Scenario Q&A and Design Template

### Scenarios
- **Hot GPU with ECC spikes:** Exclude device, drain node if recurring, reschedule via workflow signal, trigger RMA if thresholds persist.
- **MIG fragmentation rising:** Consolidate by preferring full-GPU jobs to fragmentation-resistant nodes; schedule defrag window; temporarily adjust MIG strategy labels.
- **Multi-GPU training latency:** Enforce NVLink domain placement; reduce cross-socket hops; verify NCCL topology env; pin pods via affinity.
- **Backlog under multi-tenant load:** Apply quota and priority; enable preemption for critical workloads; use aging to avoid starvation.
- **Checkpoint I/O bottleneck:** Increase chunk size, parallel uploads, compress selectively; reduce cadence during steady-state; consider local SSD staging.

### Design Template (use in whiteboard answers)
- **Workload Profile:** batch vs service, data size, model params, GPU count.
- **Resource Model:** full GPU vs MIG profiles; topology awareness; storage.
- **Scheduling Policy:** bin packing, NVLink locality, fairness policy.
- **Health & Telemetry:** DCGM metrics, thresholds, actions (drain/reschedule).
- **Fault Tolerance:** checkpoint frequency, format, resume semantics.
- **Observability:** metrics, logs, traces, alerts, dashboards.
- **SLOs & Tradeoffs:** startup time, throughput, cost/utilization targets.

---

## 📎 Appendix C. Block Diagrams (Extended)

### GPU vs CPU Scheduling in K8s

```markdown
             ┌─────────────────────────────┐
             │  Kubernetes Scheduler       │
             └───────┬─────────────────────┘
                     │
     ┌───────────────┼─────────────────────────┐
     ▼                                       ▼
CPU Scheduling                           GPU Scheduling
(cgroups/OS native)                      (via Device Plugin/Operator)
- Count cores                             - Check GPU inventory
- Allocate time slices                    - MIG slices / NVLink topology
- Uniform resources                       - Heterogeneous resources
```

### Telemetry Flow with DCGM

```markdown
GPU Node ──► DCGM Exporter ──► Prometheus ──► Alertmanager ──► Temporal signal
        (metrics)         (scraping)      (alerts)          (trigger reschedule)
```

### Checkpointing Flow

```markdown
Job Training ──► Save Checkpoint ──► Object Storage (S3/GCS)
             │
             └─► On Failure ──► Temporal detects ──► Restart job ──► Load Checkpoint
```

---

### End-to-End Orchestration + Data Plane (ASCII)

```markdown
                      ┌──────────────────────────────────────────────────────┐
                      │                     User / Client                    │
                      │     (submit AI job: image, dataset, GPUs, priority) │
                      └───────────────┬─────────────────────────────────────┘
                                      │ 1. Job request (REST/gRPC)
                           ┌──────────▼───────────┐
                           │  Temporal Orchestration                                (control plane – lifecycle) │
                           │  ───────────────────────────────────────────────────────────────────────────────── │
                           │  • Workflow (state machine: prepare→schedule→monitor→checkpoint→teardown)        │
                           │  • Activities (call K8s, storage, alerts)                                        │
                           │  • Task/Signal/Retry (durable, resumable)                                        │
                           └──────────┬───────────┬────────────────────────────────────────────────────────────┘
                                      │2. Create K8s Job/CRD      │3. Subscribe to telemetry/alerts
                    ┌─────────────────▼─────────────────┐         │
                    │        Kubernetes Control Plane    │         │
                    │  ───────────────────────────────── │         │
                    │  • API Server + etcd               │         │
                    │  • Scheduler (+ GPU-aware plugin)  │◄────────┘
                    │  • Controller Manager / Operators  │
                    └───────────┬────────────┬──────────┘
                                │4. Bind Pod │
                                │            │
                 ┌──────────────▼───────┐    │
                 │    Cluster Network   │    │ (watch/assign)
                 └──────────────┬───────┘    │
                                │             │
         ┌──────────────────────▼────────────────────────┐
         │                  Worker Node (N)              │
         │  ───────────────────────────────────────────  │
         │  • kubelet + containerd                       │
         │  • CPU (cgroups/cpuset)                       │
         │  • RAM                                        │
         │  • NVIDIA GPU(s) (NVLink/NVSwitch topology)   │
         │  • NVIDIA Device Plugin (DaemonSet)           │
         │  • NVIDIA GPU Operator (drivers, CUDA, DCGM)  │
         │         ┌───────────────┬────────────────┐
         │         │               │                │
         │   ┌─────▼─────┐   ┌────▼─────┐    ┌─────▼─────┐
         │   │   Pod A   │   │  Pod B    │    │   Pod C   │   (data plane – execution)
         │   │(1..n cont.)│ │(1..n cont.)│    │(1..n cont.)│
         │   └─────┬─────┘   └────┬─────┘    └─────┬─────┘
         │         │ CPU/mem via cgroups │          │
         │   ┌─────▼─────┐      ┌────▼─────┐   ┌───▼──────┐
         │   │ Container │      │ Container│   │ Container│
         │   │  (PyTorch │      │  (TF)    │   │  (CUDA)  │
         │   └─────┬─────┘      └────┬─────┘   └────┬─────┘
         │         │                 │               │
         │ 5. Device Plugin injects assigned GPU devices into containers
         │         │                 │               │   (e.g. /dev/nvidia0, MIG UUIDs, env)
         │   ┌─────▼─────────────────▼───────────────▼────┐
         │   │        Physical GPU & MIG Partitioning      │
         │   │  [GPU0] ─ MIG slices (e.g., 1g.10gb, 2g.20gb, 3g.40gb)     │
         │   │  [GPU1] ─ MIG slices …                                     │
         │   │  NVLink/NVSwitch fabric for intra-node multi-GPU training  │
         │   └─────────────────────────────────────────────────────────────┘
         └──────────────────────────────────────────────────────────────────┘

            ┌─────────────────────────────────────────────────────────────────────┐
            │  Telemetry & Storage (shared services)                              │
            │  • DCGM Exporter/Node logs → Prometheus/Thanos → Alertmanager       │
            │  • Object Store (S3/GCS/MinIO) for datasets, artifacts, checkpoints │
            │  • CSI volumes for persistent data                                  │
            └────────────┬────────────────────────────────────────────────────────┘
                         │6. Metrics/alerts    │7. Save/Load checkpoints (periodic)
                         └──────────► Temporal Workflow (signals: retry/failover/scale)
```

---

## 📎 Appendix D. Spark, Ray, Dask in GPU Infra

### Spark
- Batch-oriented, JVM-based.  
- Great for ETL, preprocessing of large datasets before training.  
- Limited fine-grained GPU scheduling support, though RAPIDS Accelerator can integrate GPUs.

### Dask
- Python-native parallel computing.  
- Scales NumPy/Pandas workloads.  
- With RAPIDS + Dask-CUDA, supports distributed GPU DataFrames.  
- Useful for feature engineering, large-scale preprocessing.

### Ray
- Actor-based, Python-first.  
- Strong for dynamic workloads, ML orchestration (Ray Tune, Ray Serve).  
- Integrates well with GPUs via `ray.get_gpu_ids()`.  
- More flexible than Spark for heterogeneous AI workloads.

### Where they fit in DGX Cloud:
- **Spark**: preprocessing big data → outputs training dataset.  
- **Dask**: GPU-accelerated data engineering on DGX clusters.  
- **Ray**: orchestrating ML workflows, hyperparameter tuning, serving.  
- **K8s + Temporal**: production-grade lifecycle orchestration, scheduling at scale.

---

## 📎 Appendix E. GPU/K8s/DCGM Command Cheat Sheet

```bash
# GPU inventory and health
nvidia-smi -L                     # List GPUs/MIG instances
nvidia-smi                         # Utilization, memory, ECC, temperature
nvidia-smi --query-gpu=index,uuid,temperature.gpu,utilization.gpu,ecc.errors.uncorrected.total --format=csv

# DCGM exporter (k8s) quick check
kubectl -n gpu-telemetry get pods
kubectl -n gpu-telemetry logs deploy/dcgm-exporter | head

# K8s GPU resources
kubectl get node -o custom-columns=NAME:.metadata.name,GPU:.status.allocatable.'nvidia\.com/gpu'
kubectl get pods -A -o custom-columns=NS:.metadata.namespace,NAME:.metadata.name,GPUs:.spec.containers[*].resources.limits.'nvidia\.com/gpu'

# MIG-specific
kubectl get node -L nvidia.com/mig.strategy
kubectl describe node <node> | grep -i mig -n

# Topology hints (labels vary)
kubectl get node --show-labels | grep -E 'topology|nvlink|pcie'

# Temporal (if using tctl)
tctl workflow list
tctl workflow describe --workflow_id <id>
```

---

## 📎 Appendix F. Metrics, SLOs, and Failure Playbooks

**Key Metrics**
- **Utilization**: GPU %, memory %, SM occupancy.
- **Health**: temperature, power draw, ECC corrected/uncorrected counts.
- **Throughput/Latency**: samples/sec, step-time, P99 latency for inference.
- **Queueing**: pending jobs, time-to-start, head-of-line blocking.

**SLO Examples**
- Training job startup ≤ 2 minutes; recovery from node failure ≤ 5 minutes.
- Inference P99 ≤ 150 ms while GPU utilization ≥ 60%.

**Playbooks**
- **Thermal breach**: throttle -> drain node -> reschedule -> open ticket.
- **ECC uncorrected**: immediate drain -> quarantine GPU -> notify ops.
- **Utilization < 30%**: enable bin packing, increase MIG slicing, right-size requests.
- **Queue backlog**: enable preemption for high-priority; burst autoscaling; review quotas.

---

## 📎 Appendix G. Extended Q&A

### Q9. How would you integrate Spark with GPU infra?
- Use **RAPIDS Accelerator for Spark** to offload SQL/DataFrame ops to GPUs.  
- Cluster managed by K8s, Spark pods can request GPU resources via Device Plugin.  
- Data preprocessed in Spark can be fed into DL training jobs.

### Q10. When would you prefer Ray over Spark?
- Spark = static, batch ETL, strong SQL.  
- Ray = dynamic, ML-native, better for online learning, RL, or hyperparameter search.  
- In NVIDIA infra: Ray complements K8s operators for model training orchestration.

### Q11. How does Dask compare to Ray?
- Dask: task graph, dataframe + array parallelism.  
- Ray: actor model, more flexible but less dataframe-native.  
- For GPU: Dask-CUDA integrates naturally with RAPIDS; Ray requires manual GPU placement but excels in workflow composition.

### Q12. How do these systems coexist with K8s?
- K8s manages cluster lifecycle + GPU allocation.  
- Spark, Dask, Ray run **on top of K8s** as applications.  
- Temporal can orchestrate workflows across them (ETL in Spark → training in Ray → analysis in Dask).

---

## 📎 Appendix H. Behavioral Prep + Closing Questions

**STAR Stories (summarize in 60–90s each)**
- **Resilience:** GPU node failures during training → implemented checkpoint/resume + DCGM-based health gates → MTTR cut by 60%.
- **Utilization:** Chronic underutilization due to MIG mismatch → introduced predictive slicing and bin packing → +25% utilization.
- **Latency:** Multi-GPU training slowed by cross-socket hops → enforced NVLink locality and NCCL tuning → 1.8× speedup.
- **Fairness:** Tenant contention → set quotas, priorities, and preemption → cleared backlog without starvation.

**Closing Questions for NVDA**
- How does the team model NVLink/NVSwitch topology in scheduling decisions today?
- What is your policy for MIG defragmentation in production clusters?
- Which DCGM metrics most strongly influence placement or draining?
- How do you evaluate tradeoffs between utilization and performance isolation?
- What success metrics do you track for GPU scheduling and orchestration?

---

## 📌 Final Takeaways

- Know the **big picture**: GPUs are special resources in K8s → Device Plugin + Operator bridge the gap.  
- DGX Cloud pain points: **MIG fragmentation, NVLink-aware placement, health-aware scheduling, checkpointing, fairness**.  
- Spark, Ray, Dask = complementary layers for data + ML workloads, but **K8s + Temporal** are the backbone of orchestration.  
- In interviews: start with **system-level view**, then zoom into **GPU-specific challenges**, and finally mention **ecosystem tools** (Spark/Ray/Dask).

