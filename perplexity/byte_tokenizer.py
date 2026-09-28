"""
Byte Tokenizer Implementation
Part 0, 1, and 2 solution
"""

from typing import List
import random


class ByteTokenizer:
    def __init__(self, token_alphabet: List[bytes]):
        """
        Initialize the tokenizer with a token alphabet.

        Args:
            token_alphabet: List of byte sequences representing valid tokens
        """
        self.token_alphabet = token_alphabet

        # Build token_to_id mapping for O(1) lookup
        self.token_to_id = {token: idx for idx, token in enumerate(token_alphabet)}

        # Part 1 optimization: Precompute merge table
        # For each token, precompute all valid splits and their corresponding token IDs
        self.merge_table = {}

        for cur_token_id, cur_token_bytes in enumerate(token_alphabet):
            valid_merges = set()

            # Try all split points
            for split_point in range(1, len(cur_token_bytes)):
                left_bytes = cur_token_bytes[:split_point]
                right_bytes = cur_token_bytes[split_point:]

                # Check if both parts exist as tokens
                left_id = self.token_to_id.get(left_bytes)
                right_id = self.token_to_id.get(right_bytes)

                if left_id is not None and right_id is not None:
                    valid_merges.add((left_id, right_id))

            self.merge_table[cur_token_id] = valid_merges

    def slow_tokenize(self, text: bytes) -> List[int]:
        """
        Tokenize some text slowly using a naive algorithm.

        Returns a list of ints. Each int represents the zero-based
        index of a token within self.token_alphabet.
        """
        # STEP 1: Convert bytes to individual token IDs
        token_ids: List[int] = list(text)

        # STEP 2: Merge multi-byte tokens
        for cur_token_id in range(256, len(self.token_alphabet)):
            cur_token_bytes = self.token_alphabet[cur_token_id]

            # Find which pairs can be merged into current token
            pairs_to_merge: set[tuple[int, int]] = set()
            for split_point in range(1, len(cur_token_bytes)):
                left_bytes = cur_token_bytes[:split_point]
                right_bytes = cur_token_bytes[split_point:]

                left_id = self.token_to_id.get(left_bytes)
                right_id = self.token_to_id.get(right_bytes)

                if left_id is not None and right_id is not None:
                    pairs_to_merge.add((left_id, right_id))

            # Perform all valid merges for the current token
            new_token_ids: List[int] = []
            i = 0
            while i < len(token_ids):
                # Check if tokens at positions (i, i + 1) can be merged
                if (i < len(token_ids) - 1 and
                    (token_ids[i], token_ids[i + 1]) in pairs_to_merge):
                    new_token_ids.append(cur_token_id)
                    i += 2
                else:  # No merge is possible, copy the original token
                    new_token_ids.append(token_ids[i])
                    i += 1

            token_ids = new_token_ids

        return token_ids

    def tokenize(self, text: bytes) -> List[int]:
        """
        Fast tokenization using precomputed merge table.

        Returns same result as slow_tokenize but much faster.
        """
        # STEP 1: Initialize with single-byte tokens
        token_ids: List[int] = list(text)

        # STEP 2: Merge using precomputed table
        for cur_token_id in range(256, len(self.token_alphabet)):
            pairs_to_merge = self.merge_table[cur_token_id]

            if not pairs_to_merge:
                continue

            # Perform all valid merges for the current token
            new_token_ids: List[int] = []
            i = 0
            while i < len(token_ids):
                # Check if tokens at positions (i, i + 1) can be merged
                if (i < len(token_ids) - 1 and
                    (token_ids[i], token_ids[i + 1]) in pairs_to_merge):
                    new_token_ids.append(cur_token_id)
                    i += 2
                else:  # No merge is possible, copy the original token
                    new_token_ids.append(token_ids[i])
                    i += 1

            token_ids = new_token_ids

        return token_ids

    def estimate_token_count(self, text: bytes, sample_size: int, rng: random.Random) -> int:
        """
        Estimate the number of tokens in text by sampling.

        Args:
            text: The byte sequence to estimate token count for
            sample_size: Maximum number of bytes we can tokenize
            rng: Random number generator for sampling

        Returns:
            Estimated token count
        """
        text_len = len(text)

        # If we can tokenize the entire text, return exact count
        if sample_size >= text_len:
            return len(self.tokenize(text))

        # Otherwise, sample and extrapolate
        # Strategy: Take a random contiguous chunk of sample_size bytes
        # and extrapolate based on the ratio

        # Random starting position
        start_pos = rng.randint(0, text_len - sample_size)
        sample = text[start_pos:start_pos + sample_size]

        # Tokenize the sample
        sample_token_count = len(self.tokenize(sample))

        # Extrapolate: (total_bytes / sample_bytes) * sample_token_count
        estimated_count = (text_len / sample_size) * sample_token_count

        return int(estimated_count)


def test_tokenizer():
    """Test the tokenizer with a simple example"""
    # Create a simple token alphabet
    # First 256 are single bytes (0-255)
    token_alphabet = [bytes([i]) for i in range(256)]

    # Add some multi-byte tokens
    token_alphabet.append(b"hello")  # token 256
    token_alphabet.append(b"world")  # token 257
    token_alphabet.append(b"he")     # token 258
    token_alphabet.append(b"ll")     # token 259
    token_alphabet.append(b"o")      # token 111 (already exists as single byte)

    tokenizer = ByteTokenizer(token_alphabet)

    # Test slow vs fast tokenization
    text = b"hello world"
    slow_result = tokenizer.slow_tokenize(text)
    fast_result = tokenizer.tokenize(text)

    print(f"Slow tokenize: {slow_result}")
    print(f"Fast tokenize: {fast_result}")
    print(f"Results match: {slow_result == fast_result}")

    # Test estimation
    rng = random.Random(42)
    long_text = b"hello world " * 1000
    exact_count = len(tokenizer.tokenize(long_text))
    estimated_count = tokenizer.estimate_token_count(long_text, 1000, rng)

    print(f"\nEstimation test:")
    print(f"Exact count: {exact_count}")
    print(f"Estimated count: {estimated_count}")
    print(f"Error: {abs(exact_count - estimated_count) / exact_count * 100:.2f}%")


if __name__ == "__main__":
    test_tokenizer()
