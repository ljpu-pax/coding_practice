# Part 0: Tokenization Algorithm Description

## Overview
The `slow_tokenize` method implements a byte-pair encoding (BPE) style tokenization algorithm that converts a byte sequence into a list of token IDs.

## Algorithm Steps

### Step 1: Initialization
- Convert the input byte sequence `text` into a list of integers `token_ids`
- Each byte is treated as its own single-byte token with ID equal to its ASCII/byte value (0-255)
- Example: `b"hello"` becomes `[104, 101, 108, 108, 111]`

### Step 2: Iterative Merging
- Process each multi-byte token in the alphabet in ascending order (starting from token ID 256)
- For each multi-byte token:

  **2a. Find Valid Merge Pairs:**
  - Try all possible ways to split the current token into two parts
  - For each split point from 1 to `len(token)-1`:
    - Split into `left_bytes` and `right_bytes`
    - Check if both parts exist in the token alphabet
    - If both exist, record their token IDs as a valid merge pair

  **2b. Perform Merges:**
  - Scan through the current `token_ids` list from left to right
  - When consecutive tokens at positions `(i, i+1)` form a valid merge pair:
    - Replace them with the current multi-byte token ID
    - Skip ahead by 2 positions (since we consumed 2 tokens)
  - Otherwise, keep the original token and advance by 1 position
  - This creates a new `token_ids` list with merges applied

  **2c. Update State:**
  - Replace the old `token_ids` with the new merged list
  - Continue to the next multi-byte token

### Step 3: Return Result
- After processing all multi-byte tokens in order, return the final `token_ids` list

## Key Properties

1. **Greedy Left-to-Right**: Merges are performed greedily from left to right in a single pass per token
2. **Ordered Processing**: Multi-byte tokens are processed in order of their token ID (ascending)
3. **No Backtracking**: Once a merge is performed, it's never undone
4. **Deterministic**: Given the same input and token alphabet, output is always the same

## Example Walkthrough

Given token alphabet:
- 0-255: Single bytes
- 256: `b"he"`
- 257: `b"ll"`
- 258: `b"hello"`

Input: `b"hello"` → Initial: `[104, 101, 108, 108, 111]` (h, e, l, l, o)

1. Process token 256 (`b"he"`):
   - Can merge: `(104, 101)` → `256`
   - Result: `[256, 108, 108, 111]`

2. Process token 257 (`b"ll"`):
   - Can merge: `(108, 108)` → `257`
   - Result: `[256, 257, 111]`

3. Process token 258 (`b"hello"`):
   - No valid merges found (requires `b"he"` + `b"llo"`, but `b"llo"` doesn't exist as a token)
   - Result: `[256, 257, 111]`

Final output: `[256, 257, 111]` representing "he" + "ll" + "o"
