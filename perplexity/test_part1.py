# Copyright 2025 Perplexity AI, Inc. All rights reserved.


import pytest

from temporal_kv_store import TemporalKVStore


def test_part1_simple() -> None:
  """Basic test for TemporalKVStore.

  This test is intended to illustrate expected behavior rather
  than to comprehensively test your code. It uses a single
  configuration key ("agent_model") for ease of reading.
  Obviously, your implementation should work for multiple keys.
  """
  store = TemporalKVStore()

  def check_values(expectations):
    """Check that the store returns expected values for given timestamp ranges.

    Args:
      expectations: A list of (timestamp_range, expected_value) pairs.
    """
    for timestamp_range, expected_value in expectations:
      for timestamp in timestamp_range:
        actual = store.get("agent_model", timestamp=timestamp)
        assert actual == expected_value, \
          f"At timestamp {timestamp}: expected {expected_value!r}, got {actual!r}"

  # Initially, there is no value set.
  check_values([
    (range(0, 10), None),
  ])

  # Perform a set operation at timestamp 8.
  store.set("agent_model", value="A", timestamp=8)

  # - Before timestamp 8, there is no value set.
  # - Starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 8), None),
    (range(8, 10), "A"),
  ])

  # Perform a set operation at timestamp 4.
  store.set("agent_model", value="B", timestamp=4)

  # - Before timestamp 4, there is no value set.
  # - Starting at timestamp 4, the value is "B".
  # - Then, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 4), None),
    (range(4, 8), "B"),
    (range(8, 10), "A"),
  ])

  # Perform a delete operation at timestamp 5.
  # This delete is valid because the set operation at
  # timestamp 4 means that the key exists at timestamp 5.
  assert store.delete("agent_model", timestamp=5) == True

  # - Before timestamp 4, there is no value set.
  # - Starting at timestamp 4, the value is "B".
  # - Then, starting at timestamp 5, there is no value set
  #   due to the deletion.
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 4), None),
    (range(4, 5), "B"),
    (range(5, 8), None),
    (range(8, 10), "A"),
  ])

  # Attempt to perform a delete operation at timestamp 6,
  # which should fail and no-op since there is no value set
  # at timestamp 6 due to the previous deletion at timestamp 5.
  assert store.delete("agent_model", timestamp=6) == False

  # Perform another set operation at timestamp 4.
  store.set("agent_model", value="C", timestamp=4)

  # - Before timestamp 4, there is no value set.
  # - Starting at timestamp 4, the value is "C"
  #   (due to our convention that operations with the same
  #   timestamp are resolved in order of invocation).
  # - Then, starting at timestamp 5, there is no value set
  #   due to the deletion (observe that the deletion still
  #   applies even though we performed a new set operation
  #   at a previous timestamp!).
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 4), None),
    (range(4, 5), "C"),
    (range(5, 8), None),
    (range(8, 10), "A"),
  ])

  # Perform another set operation at timestamp 5.
  store.set("agent_model", value="D", timestamp=5)

  # - Before timestamp 4, there is no value set.
  # - Starting at timestamp 4, the value is "C".
  # - Then, starting at timestamp 5, the value is "D"
  #   (the deletion is overridden by this new set operation).
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 4), None),
    (range(4, 5), "C"),
    (range(5, 8), "D"),
    (range(8, 10), "A"),
  ])

  # Attempt to perform a delete operation at timestamp 2,
  # which should fail and no-op since there is no value set
  # at timestamp 2.
  assert store.delete("agent_model", timestamp=2) == False

  # Perform another set operation at timestamp 1.
  store.set("agent_model", value="E", timestamp=1)

  # - Before timestamp 1, there is no value set.
  # - Starting at timestamp 1, the value is "E".
  # - Then, starting at timestamp 4, the value is "C".
  # - Then, starting at timestamp 5, the value is "D".
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 1), None),
    (range(1, 4), "E"),
    (range(4, 5), "C"),
    (range(5, 8), "D"),
    (range(8, 10), "A"),
  ])

  # Perform another delete operation at timestamp 5.
  assert store.delete("agent_model", timestamp=5) == True

  # - Before timestamp 1, there is no value set.
  # - Starting at timestamp 1, the value is "E".
  # - Then, starting at timestamp 4, the value is "C".
  # - Then, starting at timestamp 5, there is no value set.
  #   (delete wins over the earlier set "D" at timestamp 5).
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 1), None),
    (range(1, 4), "E"),
    (range(4, 5), "C"),
    (range(5, 8), None),
    (range(8, 10), "A"),
  ])

  # Perform another set operation at timestamp 5.
  store.set("agent_model", value="F", timestamp=5)

  # - Before timestamp 1, there is no value set.
  # - Starting at timestamp 1, the value is "E".
  # - Then, starting at timestamp 4, the value is "C".
  # - Then, starting at timestamp 5, the value is "F".
  #   (the subsequent set at timestamp 5 wins over the delete).
  # - Finally, starting at timestamp 8, the value is "A".
  check_values([
    (range(0, 1), None),
    (range(1, 4), "E"),
    (range(4, 5), "C"),
    (range(5, 8), "F"),
    (range(8, 10), "A"),
  ])


def main() -> None:
  pytest.main([__file__, "-v", "-rP", "-x", "--timeout=10"])


if __name__ == "__main__":
  main()
