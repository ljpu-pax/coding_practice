"""
Temporal Key-Value Store Implementation
Supports get, set, delete operations at arbitrary timestamps
"""

from typing import Any, Optional
from sortedcontainers import SortedList


class TemporalKVStore:
    """
    A key-value store that supports operations at arbitrary timestamps.

    - set(key, value, timestamp): Set key to value at given timestamp
    - get(key, timestamp): Get value of key at given timestamp
    - delete(key, timestamp): Delete key at given timestamp (returns True if successful)

    Key properties:
    1. Operations can be performed at any timestamp (past or future)
    2. Later operations at the same timestamp override earlier ones
    3. Delete only succeeds if there's a value set at that exact timestamp

    Performance:
    - Uses SortedList (B-tree) for O(log n) insert and search
    """

    def __init__(self):
        # For each key, store a SortedList of (timestamp, operation_order, value/None)
        # SortedList maintains sorted order automatically with O(log n) inserts
        self.store = {}
        self.operation_counter = 0

    def set(self, key: str, value: Any, timestamp: int) -> None:
        """Set key to value at given timestamp - O(log n)"""
        self.operation_counter += 1

        # Store as (timestamp, operation_order, value)
        entry = (timestamp, self.operation_counter, value)

        # Get or create SortedList for this key
        if key not in self.store:
            self.store[key] = SortedList([entry])
        else:
            # SortedList.add is O(log n)
            self.store[key].add(entry)

    def get(self, key: str, timestamp: int) -> Optional[Any]:
        """Get value of key at given timestamp - O(log n)"""
        if key not in self.store:
            return None

        history = self.store[key]

        # Binary search for the rightmost entry with timestamp <= query timestamp
        # bisect_right returns insertion point (index after all matching elements)
        idx = history.bisect_right((timestamp, float('inf'), None))

        # idx-1 is the last entry with timestamp <= query timestamp
        if idx == 0:
            return None

        _, _, value = history[idx - 1]
        return value

    def delete(self, key: str, timestamp: int) -> bool:
        """Delete key at given timestamp - O(log n)"""
        # Check if there's a value at this timestamp
        current_value = self.get(key, timestamp)

        if current_value is None:
            return False

        # Perform the deletion by setting value to None
        self.operation_counter += 1
        entry = (timestamp, self.operation_counter, None)

        if key not in self.store:
            self.store[key] = SortedList([entry])
        else:
            self.store[key].add(entry)

        return True

    def restore(self, timestamp: int) -> None:
        """
        Restore the store to its state at the given timestamp.
        Removes all operations that occurred after the given timestamp.
        """
        keys_to_remove = []

        for key, history in self.store.items():
            # Find index of first entry after timestamp
            idx = history.bisect_right((timestamp, float('inf'), None))

            # Remove all entries after idx
            if idx < len(history):
                # Delete from idx to end
                del history[idx:]

            # If no entries remain, mark key for removal
            if len(history) == 0:
                keys_to_remove.append(key)

        # Remove empty keys
        for key in keys_to_remove:
            del self.store[key]


def test_basic():
    """Test basic functionality"""
    store = TemporalKVStore()

    # Test set and get
    store.set("key1", "value1", timestamp=10)
    assert store.get("key1", timestamp=10) == "value1"
    assert store.get("key1", timestamp=9) is None
    assert store.get("key1", timestamp=11) == "value1"

    # Test update
    store.set("key1", "value2", timestamp=20)
    assert store.get("key1", timestamp=10) == "value1"
    assert store.get("key1", timestamp=20) == "value2"

    # Test delete
    assert store.delete("key1", timestamp=20) == True
    assert store.get("key1", timestamp=20) is None

    # Test delete on non-existent key
    assert store.delete("key2", timestamp=10) == False

    print("Basic tests passed!")


def test_out_of_order():
    """Test operations performed out of chronological order"""
    store = TemporalKVStore()

    # Set at timestamp 20
    store.set("key1", "v20", timestamp=20)

    # Set at earlier timestamp 10
    store.set("key1", "v10", timestamp=10)

    # Check values
    assert store.get("key1", timestamp=9) is None
    assert store.get("key1", timestamp=10) == "v10"
    assert store.get("key1", timestamp=15) == "v10"
    assert store.get("key1", timestamp=20) == "v20"

    print("Out-of-order tests passed!")


def test_same_timestamp():
    """Test operations at the same timestamp"""
    store = TemporalKVStore()

    # Multiple sets at same timestamp
    store.set("key1", "v1", timestamp=10)
    store.set("key1", "v2", timestamp=10)

    # Later operation should win
    assert store.get("key1", timestamp=10) == "v2"

    # Delete after set at same timestamp
    store.set("key1", "v3", timestamp=10)
    assert store.delete("key1", timestamp=10) == True
    assert store.get("key1", timestamp=10) is None

    # Set after delete at same timestamp
    store.set("key1", "v4", timestamp=10)
    assert store.get("key1", timestamp=10) == "v4"

    print("Same-timestamp tests passed!")


if __name__ == "__main__":
    test_basic()
    test_out_of_order()
    test_same_timestamp()
    print("\n" + "="*50)
    print("All custom tests passed!")
