package openai;

import java.util.*;

/**
 * Key-Value Store with Persistence - SIMPLIFIED for Interview
 *
 * Problem:
 * You are given a class that provides save_blob() and get_blob() functionality to save/restore
 * byte arrays. It also provides utility functions to convert between strings/integers and bytes.
 *
 * Implement a KV store with:
 * - put(key, value): Store a key-value pair
 * - get(key): Retrieve the value for a given key
 * - shutdown(): Save the KV store to blob storage
 * - restore(): Restore the KV store from blob storage
 *
 * Constraints:
 * - CANNOT use JSON or toString() directly
 * - MUST use provided utility functions to manually serialize
 *
 * SIMPLIFIED Approach for Interview:
 * Instead of ByteArrayOutputStream (too complex), use a simple List<Byte> approach:
 * 1. Store count of entries
 * 2. For each entry: [key_length][key_bytes][value_length][value_bytes]
 * 3. Flatten to byte array for storage
 *
 * Time: O(1) for put/get, O(n) for shutdown/restore
 * Space: O(n)
 */
public class KVStoreSimple {

    /**
     * Provided blob storage interface (given in interview)
     */
    static class BlobStorage {
        private byte[] data;

        void saveBlob(byte[] blob) {
            this.data = blob;
        }

        byte[] getBlob() {
            return this.data;
        }

        // Utility: string to bytes
        byte[] stringToBytes(String str) {
            return str.getBytes();
        }

        // Utility: bytes to string
        String bytesToString(byte[] bytes) {
            return new String(bytes);
        }

        // Utility: int to bytes (4 bytes)
        byte[] intToBytes(int value) {
            return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value
            };
        }

        // Utility: bytes to int
        int bytesToInt(byte[] bytes) {
            return ((bytes[0] & 0xFF) << 24) |
                   ((bytes[1] & 0xFF) << 16) |
                   ((bytes[2] & 0xFF) << 8) |
                   (bytes[3] & 0xFF);
        }
    }

    private Map<String, String> store;
    private BlobStorage blobStorage;

    public KVStoreSimple() {
        this.store = new HashMap<>();
        this.blobStorage = new BlobStorage();
    }

    /**
     * Put key-value pair
     */
    public void put(String key, String value) {
        store.put(key, value);
    }

    /**
     * Get value by key
     */
    public String get(String key) {
        return store.get(key);
    }

    /**
     * SIMPLIFIED shutdown - easier to write in interview
     *
     * Format: [count][key1_len][key1][val1_len][val1][key2_len][key2]...
     */
    public void shutdown() {
        List<Byte> buffer = new ArrayList<>();

        // 1. Write count
        addBytes(buffer, blobStorage.intToBytes(store.size()));

        // 2. Write each key-value pair
        for (Map.Entry<String, String> entry : store.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Write key
            byte[] keyBytes = blobStorage.stringToBytes(key);
            addBytes(buffer, blobStorage.intToBytes(keyBytes.length));
            addBytes(buffer, keyBytes);

            // Write value
            byte[] valueBytes = blobStorage.stringToBytes(value);
            addBytes(buffer, blobStorage.intToBytes(valueBytes.length));
            addBytes(buffer, valueBytes);
        }

        // 3. Convert List<Byte> to byte[]
        byte[] blob = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            blob[i] = buffer.get(i);
        }

        blobStorage.saveBlob(blob);
    }

    /**
     * SIMPLIFIED restore
     */
    public void restore() {
        byte[] blob = blobStorage.getBlob();
        if (blob == null || blob.length == 0) {
            return;
        }

        store.clear();
        int offset = 0;

        // 1. Read count
        int count = blobStorage.bytesToInt(readBytes(blob, offset, 4));
        offset += 4;

        // 2. Read each key-value pair
        for (int i = 0; i < count; i++) {
            // Read key
            int keyLen = blobStorage.bytesToInt(readBytes(blob, offset, 4));
            offset += 4;
            String key = blobStorage.bytesToString(readBytes(blob, offset, keyLen));
            offset += keyLen;

            // Read value
            int valueLen = blobStorage.bytesToInt(readBytes(blob, offset, 4));
            offset += 4;
            String value = blobStorage.bytesToString(readBytes(blob, offset, valueLen));
            offset += valueLen;

            store.put(key, value);
        }
    }

    /**
     * Helper: Add byte array to list
     */
    private void addBytes(List<Byte> buffer, byte[] bytes) {
        for (byte b : bytes) {
            buffer.add(b);
        }
    }

    /**
     * Helper: Read bytes from array
     */
    private byte[] readBytes(byte[] source, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, offset, result, 0, length);
        return result;
    }

    /**
     * Simple test
     */
    public static void main(String[] args) {
        System.out.println("=== Test 1: Basic put/get ===");
        KVStoreSimple kv = new KVStoreSimple();
        kv.put("name", "Alice");
        kv.put("age", "30");
        System.out.println("name: " + kv.get("name")); // Alice
        System.out.println("age: " + kv.get("age"));   // 30
        System.out.println();

        System.out.println("=== Test 2: Shutdown and restore ===");
        KVStoreSimple kv2 = new KVStoreSimple();
        kv2.put("key1", "value1");
        kv2.put("key2", "value2");
        kv2.put("key3", "value3");

        System.out.println("Before shutdown:");
        System.out.println("key1: " + kv2.get("key1")); // value1
        System.out.println("key2: " + kv2.get("key2")); // value2

        kv2.shutdown();

        // Simulate restart
        KVStoreSimple kv2Restored = new KVStoreSimple();
        kv2Restored.blobStorage = kv2.blobStorage; // Share blob storage
        kv2Restored.restore();

        System.out.println("\nAfter restore:");
        System.out.println("key1: " + kv2Restored.get("key1")); // value1
        System.out.println("key2: " + kv2Restored.get("key2")); // value2
        System.out.println("key3: " + kv2Restored.get("key3")); // value3
        System.out.println();

        System.out.println("=== Test 3: Empty store ===");
        KVStoreSimple kv3 = new KVStoreSimple();
        kv3.shutdown();
        KVStoreSimple kv3Restored = new KVStoreSimple();
        kv3Restored.blobStorage = kv3.blobStorage;
        kv3Restored.restore();
        System.out.println("Empty store restored successfully");
        System.out.println();

        System.out.println("=== Test 4: Update existing key ===");
        KVStoreSimple kv4 = new KVStoreSimple();
        kv4.put("status", "active");
        System.out.println("Before: " + kv4.get("status")); // active
        kv4.put("status", "inactive");
        System.out.println("After: " + kv4.get("status"));  // inactive

        kv4.shutdown();
        KVStoreSimple kv4Restored = new KVStoreSimple();
        kv4Restored.blobStorage = kv4.blobStorage;
        kv4Restored.restore();
        System.out.println("Restored: " + kv4Restored.get("status")); // inactive
    }
}

/**
 * INTERVIEW TIPS:
 * ================
 *
 * 1. Start Simple:
 *    - First implement put/get (easy HashMap)
 *    - Then tackle shutdown/restore
 *
 * 2. Explain the Format:
 *    - Draw on whiteboard: [count][len][key][len][val]...
 *    - Show example: "a"="b" → [1][1]['a'][1]['b']
 *
 * 3. Key Insight:
 *    - "Why can't use JSON?" → Need to show manual serialization skills
 *    - "Why store length?" → Needed to know where each string ends
 *
 * 4. Simplifications for Interview:
 *    - Use List<Byte> instead of ByteArrayOutputStream (easier)
 *    - Use helper methods (addBytes, readBytes) for clarity
 *    - Don't worry about efficiency - correctness first!
 *
 * 5. Common Mistakes to Avoid:
 *    - Forgetting to store length before strings
 *    - Off-by-one errors in offset tracking
 *    - Not handling empty store case
 *
 * 6. Follow-ups:
 *    Q: "What if keys/values are very large?"
 *    A: Could compress, or use file-based storage instead of in-memory
 *
 *    Q: "What about concurrent access?"
 *    A: Add locks around put/get/shutdown/restore
 *
 *    Q: "How to handle corruption?"
 *    A: Add checksum/magic number at start of blob
 *
 * Time to Code in Interview: ~20-25 minutes
 * Lines of Code: ~80 lines (this version)
 */
