# 🚀 NVIDIA DGX Cloud Interview One-Pager

---

## 📌 0. Quick Reference (60s Pitch + Checklist)

**60s Pitch**
- GPUs are special resources in K8s bridged by the NVIDIA Device Plugin and GPU Operator. I design schedulers that are MIG- and NVLink-aware to maximize utilization while preserving performance. I use DCGM telemetry for health-aware placement, checkpoint for fault tolerance, and Temporal to orchestrate the full lifecycle. I balance fairness with efficiency using quotas, priorities, and preemption.

**Interview Checklist**
- Workload: batch vs service, single- vs multi-GPU, model size, data locality
- Resource granularity: full GPU vs MIG; need for NVLink locality
- SLOs: startup time, throughput/latency, recovery time, utilization/cost
- Health policy: temp/ECC thresholds, drain/reschedule rules
- Fault tolerance: checkpoint cadence, object store, resume semantics
- Fairness: tenant quotas, priorities, preemption/aging

---

## 📑 Table of Contents
- 0. Quick Reference
- 1. Core Concepts
- 2. Key Challenges & Optimizations
- 3. Architecture Summary
- 4. Minimal Examples
- 5. Top Q&A (brief)
- 6. Interview Takeaways

---

## 📌 1. Core Concepts
- GPUs in K8s via NVIDIA Device Plugin; GPU Operator manages drivers, CUDA, DCGM
- MIG: partition large GPUs; watch fragmentation; common profiles (1g.10gb, 2g.20gb, …)
- NVLink/NVSwitch: co-locate multi-GPU jobs within same domain
- Telemetry & Health: DCGM + Prometheus/Thanos + Alertmanager
- Checkpoint & Failover: periodic saves to S3/GCS; restart from last checkpoint
- Temporal: durable workflow engine for lifecycle and retries

---

## 📌 2. Key Challenges & Optimizations
| Challenge | Optimization |
|---|---|
| MIG fragmentation | Bin packing, predictive slicing, defrag windows |
| Multi-GPU topology | NVLink/NVSwitch-aware placement |
| Unhealthy GPUs | Health-aware scheduling (temp/ECC filtering) |
| Long-running jobs | Checkpoint + resume |
| Fairness | Quotas, priority + preemption |
| Observability | DCGM → Prometheus/Thanos → Alertmanager |

---

## 📌 3. Architecture Summary
- Control plane: Temporal workflows + K8s scheduler (GPU plugin) + GPU Operator
- Data plane: Pods consume full GPUs or MIG; prefer NVLink colocation for all-reduce
- Telemetry loop: DCGM → Prometheus/Thanos → Alertmanager → workflow signals
- Resilience: checkpoints to object store; workflow restarts on failure
- Fairness: quotas, PriorityClasses, preemption, aging

---

## 📌 4. Minimal Examples
```yaml
# Pod requesting 1 full GPU
resources:
  limits:
    nvidia.com/gpu: 1
```
```yaml
# Pod requesting MIG slice
resources:
  limits:
    nvidia.com/mig-1g.10gb: 1
```
Tip: Prefer DCGM-informed placement over client-side heuristics.

---

## 📌 5. Top Q&A (brief)
- GPU assignment: Device Plugin registers; scheduler binds; runtime mounts /dev/nvidia*
- MIG: partitions to boost utilization; avoid fragmentation via bin packing/profiles
- NVLink: place multi-GPU jobs within the same NVLink/NVSwitch domain
- ECC policy: drain on uncorrected; thresholds for corrected; health-aware placement
- Fault tolerance: periodic checkpoints; workflow-driven restarts
- Temporal: durable orchestration across K8s, storage, telemetry
- Fairness: quotas, priorities/preemption, aging anti-starvation

---

## 📌 6. Interview Takeaways
- Lead with system-level view → zoom into GPU specifics → tie back to SLOs
- Articulate tradeoffs: utilization vs isolation; checkpoint frequency vs I/O
- Highlight telemetry-driven, health-aware placement and NVLink-aware scheduling
- Emphasize reproducible workflows and operational playbooks
