# Copyright 2025 Perplexity AI, Inc. All rights reserved.


import argparse
import random
import time
from dataclasses import dataclass
from enum import IntEnum

from temporal_kv_store import TemporalKVStore


class OpType(IntEnum):
  """Operation types as integer enum for efficiency."""
  GET = 0
  SET = 1
  DELETE = 2
  RESTORE = 3


@dataclass(frozen=True)
class Operation:
  """Represents a single operation to be performed."""
  op_type: OpType
  key: str
  value: str | None
  timestamp: int


def parse_args() -> argparse.Namespace:
  parser = argparse.ArgumentParser(
    description="Performance benchmark for TemporalKVStore.")
  parser.add_argument(
    "--num_gets",
    type=int,
    default=200000,
    help="Number of get operations to perform in the benchmark.",
  )
  parser.add_argument(
    "--num_sets",
    type=int,
    default=200000,
    help="Number of set operations to perform in the benchmark.",
  )
  parser.add_argument(
    "--num_deletes",
    type=int,
    default=100000,
    help="Number of delete operations to perform in the benchmark.",
  )
  parser.add_argument(
    "--num_unique_keys",
    type=int,
    default=10000,
    help="Number of unique keys to use in the benchmark.",
  )
  parser.add_argument(
    "--max_timestamp",
    type=int,
    default=(10 ** 15),
    help="Maximum timestamp to use in the benchmark.",
  )
  parser.add_argument(
    "--num_restores",
    type=int,
    default=0,
    help="Number of restores to perform during the benchmark.",
  )
  parser.add_argument(
    "--seed",
    type=int,
    default=0,
    help="Random seed to use in the benchmark.",
  )
  parser.add_argument(
    "--batch_size",
    type=int,
    default=100000,
    help="Size of operation batches for progress reporting.",
  )
  parser.add_argument(
    "--hot_key_proportion",
    type=float,
    default=0.9,
    help="Proportion of operations that hit the hot key.",
  )

  return parser.parse_args()


def generate_keys(num_keys: int) -> list[str]:
  """Generate a list of unique key names."""
  return [f"key_{i}" for i in range(num_keys)]


def generate_operations(
    args: argparse.Namespace,
    rng: random.Random,
    keys: list[str]
) -> list[Operation]:
  """
  Generate all operations to be performed.
  A configurable percentage of operations hit a single "hot" key,
  the rest are distributed uniformly across other keys.
  Returns list of benchmark operations.
  """
  all_ops = []

  # Designate the first key as the "hot" key
  hot_key = keys[0] if keys else "hot_key"
  cold_keys = keys[1:] if len(keys) > 1 else keys

  def select_key() -> str:
    """Select a key based on hot key proportion."""
    if rng.random() < args.hot_key_proportion:
      return hot_key
    else:
      return rng.choice(cold_keys) if cold_keys else hot_key

  # Generate set operations
  for _ in range(args.num_sets):
    key = select_key()
    value = f"v_{rng.randint(0, 1000000)}"
    timestamp = rng.randint(0, args.max_timestamp)
    all_ops.append(Operation(OpType.SET, key, value, timestamp))

  # Generate get operations
  for _ in range(args.num_gets):
    key = select_key()
    timestamp = rng.randint(0, args.max_timestamp)
    all_ops.append(Operation(OpType.GET, key, None, timestamp))

  # Generate delete operations
  for _ in range(args.num_deletes):
    key = select_key()
    timestamp = rng.randint(0, args.max_timestamp)
    all_ops.append(Operation(OpType.DELETE, key, None, timestamp))

  # Shuffle all operations to mix them
  rng.shuffle(all_ops)

  # Insert restore operations at regular intervals
  if args.num_restores > 0:
    restore_interval = len(all_ops) // (args.num_restores + 1)
    for i in range(args.num_restores):
      insert_pos = (i + 1) * restore_interval
      restore_timestamp = rng.randint(0, args.max_timestamp)
      all_ops.insert(insert_pos, Operation(OpType.RESTORE, '', None, restore_timestamp))

  return all_ops


def run_operations(store: TemporalKVStore, operations: list[Operation]) -> None:
  """Execute a list of operations on the store."""
  for op in operations:
    if op.op_type == OpType.SET:
      store.set(op.key, op.value, op.timestamp)
    elif op.op_type == OpType.GET:
      store.get(op.key, op.timestamp)
    elif op.op_type == OpType.DELETE:
      store.delete(op.key, op.timestamp)
    elif op.op_type == OpType.RESTORE:
      store.restore(op.timestamp)


def benchmark_operations(
    store: TemporalKVStore,
    operations: list[Operation],
    batch_size: int
) -> tuple[float, dict]:
  """
  Run operations and measure time, reporting progress.
  Returns (total_time, operation_counts).
  """
  total_time = 0.0
  op_counts = {OpType.SET: 0, OpType.GET: 0, OpType.DELETE: 0, OpType.RESTORE: 0}

  print(f"\nRunning {len(operations)} operations...")

  for i in range(0, len(operations), batch_size):
    batch = operations[i:i + batch_size]

    # Time this batch
    start_time = time.perf_counter()
    for op in batch:
      if op.op_type == OpType.SET:
        store.set(op.key, op.value, op.timestamp)
        op_counts[OpType.SET] += 1
      elif op.op_type == OpType.GET:
        store.get(op.key, op.timestamp)
        op_counts[OpType.GET] += 1
      elif op.op_type == OpType.DELETE:
        store.delete(op.key, op.timestamp)
        op_counts[OpType.DELETE] += 1
      elif op.op_type == OpType.RESTORE:
        store.restore(op.timestamp)
        op_counts[OpType.RESTORE] += 1
    end_time = time.perf_counter()

    batch_time = end_time - start_time
    total_time += batch_time

    # Progress report
    if (i + batch_size) % (batch_size * 10) == 0:
      progress = min((i + batch_size) / len(operations) * 100, 100)
      ops_per_sec = batch_size / batch_time if batch_time > 0 else 0
      print(f"  Progress: {progress:.1f}% - {ops_per_sec:.0f} ops/sec")

  return total_time, op_counts


def main() -> None:
  args = parse_args()
  rng = random.Random(args.seed)

  print("\n" + "=" * 60)
  print("TEMPORAL KV STORE PERFORMANCE BENCHMARK")
  print("=" * 60)
  print(f"\nConfiguration:")
  print(f"  Get operations:     {args.num_gets:,}")
  print(f"  Set operations:     {args.num_sets:,}")
  print(f"  Delete operations:  {args.num_deletes:,}")
  print(f"  Restore operations: {args.num_restores:,}")
  print(f"  Unique keys:        {args.num_unique_keys:,}")
  print(f"  Hot key proportion: {args.hot_key_proportion:.2f}")
  print(f"  Random seed:        {args.seed}")

  # Generate keys
  keys = generate_keys(args.num_unique_keys)

  # Generate all operations upfront
  print("\nGenerating operations...")
  benchmark_ops = generate_operations(args, rng, keys)
  total_ops = len(benchmark_ops)
  print(f"  Generated {total_ops:,} benchmark operations")

  # Create store
  store = TemporalKVStore()

  # Run main benchmark
  print("\n" + "=" * 60)
  print("MIXED OPERATIONS BENCHMARK")
  print("=" * 60)

  total_time, op_counts = benchmark_operations(store, benchmark_ops, args.batch_size)

  # Report results
  print("\n" + "=" * 60)
  print("RESULTS")
  print("=" * 60)
  print(f"\nTotal operations:  {total_ops:,}")
  print(f"Overall rate:      {total_ops / total_time:,.0f} ops/sec")
  print(f"Avg latency:       {total_time / total_ops * 1e6:.2f} μs/op")
  print(f"Total time:        {total_time:.3f} seconds")
  print("\n" + "=" * 60)
  print("BENCHMARK COMPLETE")
  print("=" * 60)


if __name__ == "__main__":
  main()
