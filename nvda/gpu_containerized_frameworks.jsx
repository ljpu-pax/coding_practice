export default function GPUContainerFrameworks() {
  return (
    <div className="p-6 bg-gray-50 rounded-2xl shadow-md space-y-6">
      <h1 className="text-2xl font-bold text-center mb-4">GPU Containerized Frameworks — Spark, Ray, and Dask on Kubernetes</h1>

      <p className="text-gray-700 text-center">
        This diagram shows how <strong>Spark</strong>, <strong>Ray</strong>, and <strong>Dask</strong> coexist in a unified GPU-enabled Kubernetes cluster.
        Each framework runs in its own pods but shares the same GPU pool, scheduler, and observability stack.
      </p>

      <div className="bg-white rounded-xl p-6 shadow grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">

        {/* Spark Section */}
        <div className="bg-yellow-50 border border-yellow-400 rounded-xl p-4">
          <h2 className="font-semibold text-lg text-center">🚛 Spark Cluster (Batch ETL)</h2>
          <ul className="list-disc ml-5 mt-2">
            <li>1x <strong>Driver Pod</strong> (CPU)</li>
            <li>Multiple <strong>Executor Pods</strong> (GPU)</li>
            <li>RAPIDS Plugin + cuDF/cuML</li>
            <li>Submitted via SparkOperator or Airflow</li>
          </ul>
          <div className="text-center mt-3 text-gray-600">
            <p>📦 Spark Driver → submits job</p>
            <p>⬇️</p>
            <p>🎯 Executors process data on GPUs</p>
          </div>
        </div>

        {/* Ray Section */}
        <div className="bg-blue-50 border border-blue-400 rounded-xl p-4">
          <h2 className="font-semibold text-lg text-center">🏍️ Ray Cluster (ML / Inference)</h2>
          <ul className="list-disc ml-5 mt-2">
            <li>1x <strong>Head Pod</strong> (scheduler + API)</li>
            <li>Many <strong>Worker Pods</strong> (GPU tasks)</li>
            <li>Fine-grained task/actor scheduling</li>
            <li>Ray Serve for model inference APIs</li>
          </ul>
          <div className="text-center mt-3 text-gray-600">
            <p>🧠 Ray Head → dispatches tasks</p>
            <p>⬇️</p>
            <p>⚙️ Workers perform GPU inference</p>
          </div>
        </div>

        {/* Dask Section */}
        <div className="bg-green-50 border border-green-400 rounded-xl p-4">
          <h2 className="font-semibold text-lg text-center">🚙 Dask Cluster (DataFrame / Exploration)</h2>
          <ul className="list-disc ml-5 mt-2">
            <li>1x <strong>Scheduler Pod</strong> (CPU)</li>
            <li>Multiple <strong>GPU Worker Pods</strong></li>
            <li>dask-cuda + dask-cudf runtime</li>
            <li>Used for feature engineering and analysis</li>
          </ul>
          <div className="text-center mt-3 text-gray-600">
            <p>📊 Scheduler orchestrates array/DataFrame ops</p>
            <p>⬇️</p>
            <p>🟩 GPU Workers execute parallel chunks</p>
          </div>
        </div>
      </div>

      {/* Shared Infrastructure */}
      <div className="bg-gray-100 border border-gray-400 rounded-xl p-4 text-sm">
        <h2 className="font-semibold text-center">🔗 Shared Infrastructure & GPU Runtime</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-center mt-2">
          <div>
            <p className="font-semibold">Kubernetes</p>
            <p className="text-gray-600">Pod scheduling & autoscaling</p>
          </div>
          <div>
            <p className="font-semibold">NVIDIA Device Plugin</p>
            <p className="text-gray-600">GPU discovery & allocation</p>
          </div>
          <div>
            <p className="font-semibold">nvidia-container-runtime</p>
            <p className="text-gray-600">GPU passthrough to containers</p>
          </div>
          <div>
            <p className="font-semibold">DCGM + Prometheus + Grafana</p>
            <p className="text-gray-600">Observability stack</p>
          </div>
        </div>
      </div>

      {/* Data Lake and Control */}
      <div className="bg-purple-50 border border-purple-400 rounded-xl p-4 text-sm text-center">
        <h2 className="font-semibold text-lg">🧱 Shared Data & Control Plane</h2>
        <p>All frameworks interact with the same Data Lake (S3 / Delta / Iceberg) and Control Plane (FastAPI / Airflow / Argo) for unified job orchestration.</p>
      </div>

      {/* Summary */}
      <div className="bg-gray-200 border border-gray-400 rounded-xl p-4 text-center text-sm">
        <h2 className="font-semibold">📘 Summary</h2>
        <p>
          Spark runs batch ETL jobs in GPU-accelerated containers using RAPIDS.<br/>
          Ray runs fine-grained ML and inference tasks as GPU-enabled actors.<br/>
          Dask provides Pythonic GPU DataFrame parallelism for analysis.<br/>
          All coexist inside the same Kubernetes cluster with shared GPU resources and monitoring.
        </p>
      </div>
    </div>
  );
}
