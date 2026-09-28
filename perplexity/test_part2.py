# Copyright 2025 Perplexity AI, Inc. All rights reserved.


import pytest

from temporal_kv_store import TemporalKVStore


# Basic Restore Tests


def test_part2_restore_to_empty_state():
  """Test restore to timestamp before any operations."""
  store = TemporalKVStore()

  store.set("key1", value="v1", timestamp=10)
  store.set("key2", value="v2", timestamp=20)
  store.set("key3", value="v3", timestamp=30)

  # Restore to timestamp 5 (before any operations)
  store.restore(5)

  # All operations should be removed
  assert store.get("key1", timestamp=10) is None
  assert store.get("key2", timestamp=20) is None
  assert store.get("key3", timestamp=30) is None

  # Verify store is effectively empty at various timestamps
  assert store.get("key1", timestamp=100) is None
  assert store.get("key2", timestamp=100) is None
  assert store.get("key3", timestamp=100) is None


def test_part2_restore_single_key():
  """Test basic restore functionality with a single key."""
  store = TemporalKVStore()

  # Build timeline
  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)
  store.set("key", value="v3", timestamp=30)
  store.set("key", value="v4", timestamp=40)

  # Restore to timestamp 25
  store.restore(25)

  # Operations at/before 25 should remain
  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) == "v2"
  assert store.get("key", timestamp=25) == "v2"

  # Operations after 25 should be gone
  assert store.get("key", timestamp=30) == "v2"  # Now sees v2, not v3
  assert store.get("key", timestamp=40) == "v2"  # Now sees v2, not v4


def test_part2_restore_multiple_keys():
  """Test restore with multiple keys."""
  store = TemporalKVStore()

  # Build timelines for multiple keys
  store.set("key1", value="a1", timestamp=10)
  store.set("key1", value="a2", timestamp=30)
  store.set("key2", value="b1", timestamp=20)
  store.set("key2", value="b2", timestamp=40)
  store.set("key3", value="c1", timestamp=15)
  store.set("key3", value="c2", timestamp=35)

  # Restore to timestamp 25
  store.restore(25)

  # Check key1: a1(10) remains, a2(30) removed
  assert store.get("key1", timestamp=10) == "a1"
  assert store.get("key1", timestamp=30) == "a1"

  # Check key2: b1(20) remains, b2(40) removed
  assert store.get("key2", timestamp=20) == "b1"
  assert store.get("key2", timestamp=40) == "b1"

  # Check key3: c1(15) remains, c2(35) removed
  assert store.get("key3", timestamp=15) == "c1"
  assert store.get("key3", timestamp=35) == "c1"


def test_part2_restore_to_future():
  """Test restore to timestamp beyond all operations (should be no-op)."""
  store = TemporalKVStore()

  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)

  # Restore to future timestamp
  store.restore(1000)

  # All operations should remain
  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) == "v2"
  assert store.get("key", timestamp=30) == "v2"


# Complex Timeline Restore Tests


def test_part2_restore_between_operations():
  """Test restore to timestamp between two operations."""
  store = TemporalKVStore()

  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)
  store.set("key", value="v3", timestamp=30)

  # Restore to timestamp 15 (between v1 and v2)
  store.restore(15)

  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=15) == "v1"
  assert store.get("key", timestamp=20) == "v1"  # v2 removed
  assert store.get("key", timestamp=30) == "v1"  # v3 removed


def test_part2_restore_at_exact_operation_timestamp():
  """Test restore at exact timestamp of an operation (operation should remain)."""
  store = TemporalKVStore()

  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)
  store.set("key", value="v3", timestamp=30)

  # Restore to timestamp 20 (exact timestamp of v2)
  store.restore(20)

  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) == "v2"  # v2 should remain
  assert store.get("key", timestamp=30) == "v2"  # v3 removed


def test_part2_restore_with_same_timestamp_operations():
  """Test restore when multiple operations share timestamps."""
  store = TemporalKVStore()

  # Multiple operations at timestamp 20
  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2a", timestamp=20)
  store.set("key", value="v2b", timestamp=20)  # Overwrites v2a
  store.set("key", value="v3", timestamp=30)

  # Restore to timestamp 20
  store.restore(20)

  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) == "v2b"  # Last operation at 20 wins
  assert store.get("key", timestamp=30) == "v2b"  # v3 removed


# Restore with Different Operation Types


def test_part2_restore_preserves_deletes():
  """Test that delete operations before restore point are preserved."""
  store = TemporalKVStore()

  store.set("key", value="v1", timestamp=10)
  assert store.delete("key", timestamp=20) == True
  store.set("key", value="v2", timestamp=30)

  # Restore to timestamp 25 (after delete but before v2)
  store.restore(25)

  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) is None  # Delete preserved
  assert store.get("key", timestamp=25) is None  # Still deleted
  assert store.get("key", timestamp=30) is None  # v2 removed


def test_part2_restore_removes_deletes():
  """Test that delete operations after restore point are removed."""
  store = TemporalKVStore()

  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)
  assert store.delete("key", timestamp=30) == True

  # Restore to timestamp 25 (before delete)
  store.restore(25)

  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=20) == "v2"
  assert store.get("key", timestamp=30) == "v2"  # Delete removed, v2 visible


def test_part2_restore_alternating_pattern():
  """Test restore in middle of set/delete pattern."""
  store = TemporalKVStore()

  # Create alternating set/delete pattern
  for i in range(0, 10):
    if i % 2 == 0:
      store.set("key", value=f"v{i}", timestamp=i * 10)
    else:
      store.delete("key", timestamp=i * 10)

  # Restore to timestamp 45 (middle of pattern)
  store.restore(45)

  # Check remaining pattern
  assert store.get("key", timestamp=0) == "v0"
  assert store.get("key", timestamp=10) is None  # Delete at 10
  assert store.get("key", timestamp=20) == "v2"
  assert store.get("key", timestamp=30) is None  # Delete at 30
  assert store.get("key", timestamp=40) == "v4"
  assert store.get("key", timestamp=50) == "v4"  # Operations after 45 removed


# Multiple Restore Operations


def test_part2_multiple_restores_backward():
  """Test multiple restore operations going backward in time."""
  store = TemporalKVStore()

  # Build timeline
  for i in range(0, 100, 10):
    store.set("key", value=f"v{i}", timestamp=i)

  # First restore to 55
  store.restore(55)
  assert store.get("key", timestamp=50) == "v50"
  assert store.get("key", timestamp=60) == "v50"  # v60 removed

  # Second restore to 25
  store.restore(25)
  assert store.get("key", timestamp=20) == "v20"
  assert store.get("key", timestamp=30) == "v20"  # v30 removed
  assert store.get("key", timestamp=50) == "v20"  # v50 also gone

  # Third restore to 5
  store.restore(5)
  assert store.get("key", timestamp=0) == "v0"
  assert store.get("key", timestamp=10) == "v0"  # Only v0 remains


def test_part2_multiple_restores_forward():
  """Test multiple restore operations going forward in time (should be no-ops)."""
  store = TemporalKVStore()

  # Build timeline
  for i in range(0, 50, 10):
    store.set("key", value=f"v{i}", timestamp=i)

  # First restore to 20
  store.restore(20)
  assert store.get("key", timestamp=30) == "v20"  # v30 removed

  # Second restore to 30 (forward, should be no-op)
  store.restore(30)
  assert store.get("key", timestamp=30) == "v20"  # Still v20, not v30

  # Third restore to 100 (way forward, should be no-op)
  store.restore(100)
  assert store.get("key", timestamp=30) == "v20"  # Still v20


def test_part2_restore_then_modify():
  """Test adding new operations after restore."""
  store = TemporalKVStore()

  # Initial timeline
  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=20)
  store.set("key", value="v3", timestamp=30)

  # Restore to 15
  store.restore(15)

  # Add new operations
  store.set("key", value="v_new", timestamp=25)
  assert store.delete("key", timestamp=35) == True

  # Verify new timeline: v1(10) -> v_new(25) -> delete(35)
  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=15) == "v1"
  assert store.get("key", timestamp=20) == "v1"  # v2 was removed
  assert store.get("key", timestamp=25) == "v_new"
  assert store.get("key", timestamp=30) == "v_new"  # v3 was removed
  assert store.get("key", timestamp=35) is None  # Deleted


# Edge Cases


def test_part2_restore_to_negative_timestamp():
  """Test restore to negative timestamp removes everything."""
  store = TemporalKVStore()

  store.set("key1", value="v1", timestamp=0)
  store.set("key1", value="v2", timestamp=10)
  store.set("key2", value="x", timestamp=5)

  # Restore to -1 should remove everything
  store.restore(-1)

  assert store.get("key1", timestamp=0) is None
  assert store.get("key1", timestamp=10) is None
  assert store.get("key2", timestamp=5) is None

  # Verify store is effectively empty
  assert store.get("key1", timestamp=100) is None
  assert store.get("key2", timestamp=100) is None


def test_part2_restore_empty_store():
  """Test restore on store with no operations."""
  store = TemporalKVStore()

  # Restore on empty store should not crash
  store.restore(100)

  # Store should still be empty
  assert store.get("key", timestamp=0) is None
  assert store.get("key", timestamp=100) is None
  assert store.get("any_key", timestamp=50) is None


def test_part2_restore_cleans_up_empty_keys():
  """Test that restore removes keys with no remaining operations."""
  store = TemporalKVStore()

  store.set("key1", value="v1", timestamp=10)
  store.set("key2", value="v2", timestamp=20)
  store.set("key3", value="v3", timestamp=30)

  # Restore to timestamp 15
  store.restore(15)

  # key1 should exist, key2 and key3 should be completely gone
  assert store.get("key1", timestamp=10) == "v1"
  assert store.get("key2", timestamp=20) is None
  assert store.get("key3", timestamp=30) is None

  # Verify key1 still has value at later timestamps
  assert store.get("key1", timestamp=100) == "v1"

  # Verify key2 and key3 have no values at any timestamp
  assert store.get("key2", timestamp=0) is None
  assert store.get("key2", timestamp=100) is None
  assert store.get("key3", timestamp=0) is None
  assert store.get("key3", timestamp=100) is None


# Retroactive Operations After Restore


def test_part2_restore_then_retroactive_operations():
  """Test adding operations before restore point after restoring."""
  store = TemporalKVStore()

  # Initial timeline
  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v2", timestamp=30)

  # Restore to 20
  store.restore(20)

  # Add retroactive operation before restore point
  store.set("key", value="v_retro", timestamp=15)

  # Timeline should be: v1(10) -> v_retro(15)
  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=15) == "v_retro"
  assert store.get("key", timestamp=20) == "v_retro"
  assert store.get("key", timestamp=30) == "v_retro"  # v2 still gone


def test_part2_restore_interaction_with_out_of_order():
  """Complex scenario with restore and out-of-order operations."""
  store = TemporalKVStore()

  # Build timeline out of order
  store.set("key", value="v3", timestamp=30)
  store.set("key", value="v1", timestamp=10)
  store.set("key", value="v5", timestamp=50)
  store.set("key", value="v2", timestamp=20)
  store.set("key", value="v4", timestamp=40)

  # Verify initial timeline works
  assert store.get("key", timestamp=25) == "v2"
  assert store.get("key", timestamp=45) == "v4"

  # Restore to 35
  store.restore(35)

  # v4(40) and v5(50) should be gone
  assert store.get("key", timestamp=35) == "v3"
  assert store.get("key", timestamp=40) == "v3"
  assert store.get("key", timestamp=50) == "v3"

  # Add more out-of-order operations
  store.set("key", value="v_new_45", timestamp=45)
  store.set("key", value="v_new_25", timestamp=25)

  # Final timeline: v1(10) -> v2(20) -> v_new_25(25) -> v3(30) -> v_new_45(45)
  assert store.get("key", timestamp=10) == "v1"
  assert store.get("key", timestamp=25) == "v_new_25"
  assert store.get("key", timestamp=30) == "v3"
  assert store.get("key", timestamp=45) == "v_new_45"


# Performance/Stress Tests


def test_part2_restore_with_many_operations():
  """Test restore with thousands of operations."""
  store = TemporalKVStore()

  # Add many operations
  num_ops = 1000
  for i in range(num_ops):
    store.set(f"key_{i % 10}", value=f"v{i}", timestamp=i)

  # Restore to middle
  restore_point = num_ops // 2
  store.restore(restore_point)

  # Verify operations before restore point remain
  for i in range(0, restore_point + 1):
    key = f"key_{i % 10}"
    # Find the last operation for this key at or before timestamp i
    expected = None
    for j in range(i, -1, -1):
      if j % 10 == i % 10 and j <= restore_point:
        expected = f"v{j}"
        break
    if expected:
      assert store.get(key, timestamp=i) == expected

  # Verify operations after restore point are gone
  assert store.get("key_0", timestamp=num_ops - 10) is not None
  assert store.get("key_0", timestamp=num_ops) is not None  # Should see earlier value


def test_part2_repeated_restores():
  """Test many restore operations in sequence."""
  store = TemporalKVStore()

  # Build initial timeline
  for i in range(100):
    store.set("key", value=f"v{i}", timestamp=i)

  # Perform many restores
  for restore_point in [90, 80, 70, 60, 50, 40, 30, 20, 10, 5, 1, 0]:
    store.restore(restore_point)

    # Verify state after each restore
    if restore_point > 0:
      # Should see last value at or before restore_point
      for j in range(restore_point, -1, -1):
        if j <= restore_point:
          assert store.get("key", timestamp=restore_point) == f"v{j}"
          break
    else:
      # Restore to 0 keeps only operation at timestamp 0
      assert store.get("key", timestamp=0) == "v0"
      assert store.get("key", timestamp=1) == "v0"

def test_part2_restore_after_duplicate_timestamp_sets():
    """
    Restore should work when the same key has been set multiple
    times at an identical timestamp that falls after the restore point.
    """
    store = TemporalKVStore()

    store.set("key", value="v1", timestamp=10)
    store.set("key", value="v2", timestamp=20)
    store.set("key", value="v3", timestamp=20)  # second write at t=20

    # Restore to before the duplicated timestamp
    store.restore(15)

    assert store.get("key", timestamp=10) == "v1"
    assert store.get("key", timestamp=20) == "v1"  # v2/v3 should be gone


def main() -> None:
  pytest.main([__file__, "-v", "-rP", "-x", "--timeout=10"])


if __name__ == "__main__":
  main()
