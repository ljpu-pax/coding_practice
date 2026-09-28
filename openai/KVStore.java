package openai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Key-Value Store with Persistence
 *
 * Problem:
 * You are given a class that provides save_blob() and get_blob() functionality to directly
 * save byte arrays to files and restore them back. It also provides utility functions to
 * serialize/deserialize between strings or integers and bytes.
 *
 * Implement a KV store with the following functions:
 * - put(key: str, value: str): Store a key-value pair
 * - get(key: str): Retrieve the value for a given key
 * - shutdown(): Save the KV store to a file
 * - restore(): Restore the KV store from a file
 *
 * Constraints:
 * - You CANNOT directly convert the internal Dict/List to string and serialize to file
 * - You CANNOT use JSON
 * - You MUST use the provided utility functions to implement custom Dict/List serialization
 *
 * Solution Approach:
 * The key insight is to manually serialize the HashMap by:
 * 1. Store number of entries (as 4 bytes integer)
 * 2. For each entry:
 *    - Store key length (4 bytes)
 *    - Store key bytes
 *    - Store value length (4 bytes)
 *    - Store value bytes
 * 3. On restore, read in the same order and reconstruct the HashMap
 *
 * Format:
 * [num_entries (4 bytes)]
 * [key1_length (4 bytes)][key1_bytes][value1_length (4 bytes)][value1_bytes]
 * [key2_length (4 bytes)][key2_bytes][value2_length (4 bytes)][value2_bytes]
 * ...
 *
 * Time Complexity:
 * - put: O(1)
 * - get: O(1)
 * - shutdown: O(n) where n is total size of all keys and values
 * - restore: O(n)
 *
 * Space Complexity: O(n) for storing the key-value pairs
 */
public class KVStore {

    /**
     * Simulates the provided blob storage class with utility functions
     */
    static class BlobStorage {
        private byte[] data;

        // Save byte array to storage
        public void saveBlob(byte[] blob) {
            this.data = blob;
        }

        // Get byte array from storage
        public byte[] getBlob() {
            return this.data;
        }

        // Utility: Convert string to bytes
        public byte[] stringToBytes(String str) {
            return str.getBytes(StandardCharsets.UTF_8);
        }

        // Utility: Convert bytes to string
        public String bytesToString(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        // Utility: Convert integer to bytes (4 bytes, big-endian)
        public byte[] intToBytes(int value) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (value >>> 24);
            bytes[1] = (byte) (value >>> 16);
            bytes[2] = (byte) (value >>> 8);
            bytes[3] = (byte) value;
            return bytes;
        }

        // Utility: Convert bytes to integer (4 bytes, big-endian)
        public int bytesToInt(byte[] bytes) {
            return ((bytes[0] & 0xFF) << 24) |
                   ((bytes[1] & 0xFF) << 16) |
                   ((bytes[2] & 0xFF) << 8) |
                   (bytes[3] & 0xFF);
        }
    }

    private Map<String, String> store;
    private BlobStorage blobStorage;

    public KVStore() {
        this.store = new HashMap<>();
        this.blobStorage = new BlobStorage();
    }

    /**
     * Store a key-value pair
     */
    public void put(String key, String value) {
        store.put(key, value);
    }

    /**
     * Retrieve value for a given key
     */
    public String get(String key) {
        return store.get(key);
    }

    /**
     * Serialize the KV store to bytes and save to blob storage
     *
     * Format:
     * [num_entries (4 bytes)]
     * For each entry:
     *   [key_length (4 bytes)][key_bytes][value_length (4 bytes)][value_bytes]
     */
    public void shutdown() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Write number of entries
            byte[] numEntriesBytes = blobStorage.intToBytes(store.size());
            baos.write(numEntriesBytes);

            // Write each key-value pair
            for (Map.Entry<String, String> entry : store.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Serialize key
                byte[] keyBytes = blobStorage.stringToBytes(key);
                byte[] keyLengthBytes = blobStorage.intToBytes(keyBytes.length);
                baos.write(keyLengthBytes);
                baos.write(keyBytes);

                // Serialize value
                byte[] valueBytes = blobStorage.stringToBytes(value);
                byte[] valueLengthBytes = blobStorage.intToBytes(valueBytes.length);
                baos.write(valueLengthBytes);
                baos.write(valueBytes);
            }

            // Save to blob storage
            blobStorage.saveBlob(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error during shutdown", e);
        }
    }

    /**
     * Restore the KV store from blob storage
     *
     * Reads the serialized format and reconstructs the HashMap
     */
    public void restore() {
        byte[] blob = blobStorage.getBlob();
        if (blob == null || blob.length == 0) {
            store = new HashMap<>();
            return;
        }

        store = new HashMap<>();
        int offset = 0;

        // Read number of entries
        byte[] numEntriesBytes = Arrays.copyOfRange(blob, offset, offset + 4);
        int numEntries = blobStorage.bytesToInt(numEntriesBytes);
        offset += 4;

        // Read each key-value pair
        for (int i = 0; i < numEntries; i++) {
            // Read key
            byte[] keyLengthBytes = Arrays.copyOfRange(blob, offset, offset + 4);
            int keyLength = blobStorage.bytesToInt(keyLengthBytes);
            offset += 4;

            byte[] keyBytes = Arrays.copyOfRange(blob, offset, offset + keyLength);
            String key = blobStorage.bytesToString(keyBytes);
            offset += keyLength;

            // Read value
            byte[] valueLengthBytes = Arrays.copyOfRange(blob, offset, offset + 4);
            int valueLength = blobStorage.bytesToInt(valueLengthBytes);
            offset += 4;

            byte[] valueBytes = Arrays.copyOfRange(blob, offset, offset + valueLength);
            String value = blobStorage.bytesToString(valueBytes);
            offset += valueLength;

            // Store in map
            store.put(key, value);
        }
    }

    /**
     * Test the KV store implementation
     */
    public static void main(String[] args) {
        System.out.println("=== Test Case 1: Basic put/get ===");
        KVStore kv1 = new KVStore();
        kv1.put("name", "Alice");
        kv1.put("age", "30");
        kv1.put("city", "New York");
        System.out.println("name: " + kv1.get("name")); // Expected: Alice
        System.out.println("age: " + kv1.get("age"));   // Expected: 30
        System.out.println("city: " + kv1.get("city")); // Expected: New York
        System.out.println();

        System.out.println("=== Test Case 2: Shutdown and restore ===");
        KVStore kv2 = new KVStore();
        kv2.put("key1", "value1");
        kv2.put("key2", "value2");
        kv2.put("key3", "value3");

        System.out.println("Before shutdown:");
        System.out.println("key1: " + kv2.get("key1")); // Expected: value1
        System.out.println("key2: " + kv2.get("key2")); // Expected: value2
        System.out.println("key3: " + kv2.get("key3")); // Expected: value3

        kv2.shutdown();

        // Simulate restart - create new instance with same blob storage
        KVStore kv2Restored = new KVStore();
        kv2Restored.blobStorage = kv2.blobStorage; // Share blob storage
        kv2Restored.restore();

        System.out.println("\nAfter restore:");
        System.out.println("key1: " + kv2Restored.get("key1")); // Expected: value1
        System.out.println("key2: " + kv2Restored.get("key2")); // Expected: value2
        System.out.println("key3: " + kv2Restored.get("key3")); // Expected: value3
        System.out.println();

        System.out.println("=== Test Case 3: Update existing key ===");
        KVStore kv3 = new KVStore();
        kv3.put("status", "active");
        System.out.println("Before update: " + kv3.get("status")); // Expected: active
        kv3.put("status", "inactive");
        System.out.println("After update: " + kv3.get("status"));  // Expected: inactive

        kv3.shutdown();
        KVStore kv3Restored = new KVStore();
        kv3Restored.blobStorage = kv3.blobStorage;
        kv3Restored.restore();
        System.out.println("After restore: " + kv3Restored.get("status")); // Expected: inactive
        System.out.println();

        System.out.println("=== Test Case 4: Empty store ===");
        KVStore kv4 = new KVStore();
        kv4.shutdown();
        KVStore kv4Restored = new KVStore();
        kv4Restored.blobStorage = kv4.blobStorage;
        kv4Restored.restore();
        System.out.println("Empty store restored, size: " + kv4Restored.store.size()); // Expected: 0
        System.out.println();

        System.out.println("=== Test Case 5: Special characters ===");
        KVStore kv5 = new KVStore();
        kv5.put("email", "user@example.com");
        kv5.put("description", "Hello, World! 你好世界");
        kv5.put("special", "!@#$%^&*()");

        kv5.shutdown();
        KVStore kv5Restored = new KVStore();
        kv5Restored.blobStorage = kv5.blobStorage;
        kv5Restored.restore();

        System.out.println("email: " + kv5Restored.get("email"));           // Expected: user@example.com
        System.out.println("description: " + kv5Restored.get("description")); // Expected: Hello, World! 你好世界
        System.out.println("special: " + kv5Restored.get("special"));       // Expected: !@#$%^&*()
        System.out.println();

        System.out.println("=== Test Case 6: Large dataset ===");
        KVStore kv6 = new KVStore();
        for (int i = 0; i < 100; i++) {
            kv6.put("key" + i, "value" + i);
        }

        kv6.shutdown();
        KVStore kv6Restored = new KVStore();
        kv6Restored.blobStorage = kv6.blobStorage;
        kv6Restored.restore();

        boolean allCorrect = true;
        for (int i = 0; i < 100; i++) {
            String expected = "value" + i;
            String actual = kv6Restored.get("key" + i);
            if (!expected.equals(actual)) {
                allCorrect = false;
                System.out.println("Mismatch at key" + i + ": expected=" + expected + ", actual=" + actual);
            }
        }
        System.out.println("Large dataset test: " + (allCorrect ? "PASSED" : "FAILED"));
        System.out.println("Restored " + kv6Restored.store.size() + " entries");
    }
}
