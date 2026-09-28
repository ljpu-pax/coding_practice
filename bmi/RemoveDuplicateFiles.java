import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Remove duplicate files in a directory based on their content
 *
 * Approach:
 * 1. Traverse all files in the directory
 * 2. Compute hash/checksum for each file's content
 * 3. Group files by their content hash
 * 4. Keep one file from each group, delete duplicates
 *
 * Follow-up: How to optimize to make it faster?
 * - Use file size as pre-filter (files with different sizes can't be duplicates)
 * - Use partial hash (hash first N bytes) before computing full hash
 * - Use multi-threading for parallel hash computation
 * - Use memory-mapped files for large files
 * - Use faster hash algorithms (MD5 vs SHA-256)
 */
class RemoveDuplicateFiles {

    /**
     * Approach 1: Basic solution - Hash all file contents
     *
     * Time: O(N * M) where N = number of files, M = average file size
     * Space: O(N * K) where K = hash size
     */
    public List<List<String>> findDuplicates(String rootPath) {
        Map<String, List<String>> hashToFiles = new HashMap<>();

        try {
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        try {
                            String hash = computeFileHash(file.toFile());
                            hashToFiles.putIfAbsent(hash, new ArrayList<>());
                            hashToFiles.get(hash).add(file.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return only groups with duplicates
        List<List<String>> result = new ArrayList<>();
        for (List<String> files : hashToFiles.values()) {
            if (files.size() > 1) {
                result.add(files);
            }
        }

        return result;
    }

    /**
     * Delete duplicate files, keeping the first one in each group
     */
    public int deleteDuplicates(String rootPath) {
        List<List<String>> duplicateGroups = findDuplicates(rootPath);
        int deletedCount = 0;

        for (List<String> group : duplicateGroups) {
            // Keep the first file, delete the rest
            for (int i = 1; i < group.size(); i++) {
                try {
                    Files.delete(Paths.get(group.get(i)));
                    deletedCount++;
                    System.out.println("Deleted: " + group.get(i));
                } catch (IOException e) {
                    System.err.println("Failed to delete: " + group.get(i));
                }
            }
        }

        return deletedCount;
    }

    /**
     * Compute MD5 hash of file content
     */
    private String computeFileHash(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     * Approach 2: Optimized with file size pre-filtering
     *
     * Optimization: Group files by size first, only hash files with same size
     * Time: O(N + M * K) where M = files with duplicates, K = average file size
     */
    public List<List<String>> findDuplicatesOptimized(String rootPath) {
        // Step 1: Group files by size (fast operation)
        Map<Long, List<String>> sizeToFiles = new HashMap<>();

        try {
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        long size = attrs.size();
                        sizeToFiles.putIfAbsent(size, new ArrayList<>());
                        sizeToFiles.get(size).add(file.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Only hash files with same size
        Map<String, List<String>> hashToFiles = new HashMap<>();

        for (Map.Entry<Long, List<String>> entry : sizeToFiles.entrySet()) {
            List<String> filesWithSameSize = entry.getValue();

            // Skip if only one file has this size
            if (filesWithSameSize.size() == 1) {
                continue;
            }

            // Hash all files with same size
            for (String filePath : filesWithSameSize) {
                try {
                    String hash = computeFileHash(new File(filePath));
                    hashToFiles.putIfAbsent(hash, new ArrayList<>());
                    hashToFiles.get(hash).add(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Return only groups with duplicates
        List<List<String>> result = new ArrayList<>();
        for (List<String> files : hashToFiles.values()) {
            if (files.size() > 1) {
                result.add(files);
            }
        }

        return result;
    }

    /**
     * Approach 3: Advanced optimization with partial hash
     *
     * Optimization: Compute partial hash first (first 1KB), then full hash only if needed
     * This is useful for large files where most differences are in the beginning
     */
    public List<List<String>> findDuplicatesPartialHash(String rootPath) {
        Map<Long, List<String>> sizeToFiles = new HashMap<>();

        // Step 1: Group by size
        try {
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        long size = attrs.size();
                        sizeToFiles.putIfAbsent(size, new ArrayList<>());
                        sizeToFiles.get(size).add(file.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Compute partial hash for files with same size
        Map<String, List<String>> partialHashToFiles = new HashMap<>();

        for (List<String> filesWithSameSize : sizeToFiles.values()) {
            if (filesWithSameSize.size() == 1) {
                continue;
            }

            for (String filePath : filesWithSameSize) {
                try {
                    String partialHash = computePartialHash(new File(filePath), 1024); // First 1KB
                    partialHashToFiles.putIfAbsent(partialHash, new ArrayList<>());
                    partialHashToFiles.get(partialHash).add(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Step 3: Compute full hash only for files with same partial hash
        Map<String, List<String>> fullHashToFiles = new HashMap<>();

        for (List<String> filesWithSamePartialHash : partialHashToFiles.values()) {
            if (filesWithSamePartialHash.size() == 1) {
                continue;
            }

            for (String filePath : filesWithSamePartialHash) {
                try {
                    String fullHash = computeFileHash(new File(filePath));
                    fullHashToFiles.putIfAbsent(fullHash, new ArrayList<>());
                    fullHashToFiles.get(fullHash).add(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Return only groups with duplicates
        List<List<String>> result = new ArrayList<>();
        for (List<String> files : fullHashToFiles.values()) {
            if (files.size() > 1) {
                result.add(files);
            }
        }

        return result;
    }

    /**
     * Compute hash of first N bytes of file
     */
    private String computePartialHash(File file, int bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[Math.min(bytes, 8192)];
            int bytesRead = fis.read(buffer, 0, Math.min(bytes, (int)file.length()));

            if (bytesRead > 0) {
                md.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     * Approach 4: Multi-threaded hash computation for large directories
     *
     * Optimization: Use parallel streams to compute hashes concurrently
     */
    public List<List<String>> findDuplicatesParallel(String rootPath) {
        Map<Long, List<String>> sizeToFiles = new HashMap<>();

        // Collect all files first
        List<Path> allFiles = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(rootPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        allFiles.add(file);
                        long size = attrs.size();
                        synchronized (sizeToFiles) {
                            sizeToFiles.putIfAbsent(size, new ArrayList<>());
                            sizeToFiles.get(size).add(file.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Filter files that need hashing
        List<String> filesToHash = new ArrayList<>();
        for (List<String> files : sizeToFiles.values()) {
            if (files.size() > 1) {
                filesToHash.addAll(files);
            }
        }

        // Parallel hash computation
        Map<String, List<String>> hashToFiles = new HashMap<>();

        filesToHash.parallelStream().forEach(filePath -> {
            try {
                String hash = computeFileHash(new File(filePath));
                synchronized (hashToFiles) {
                    hashToFiles.putIfAbsent(hash, new ArrayList<>());
                    hashToFiles.get(hash).add(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Return duplicates
        List<List<String>> result = new ArrayList<>();
        for (List<String> files : hashToFiles.values()) {
            if (files.size() > 1) {
                result.add(files);
            }
        }

        return result;
    }
}

/**
 * LeetCode 609: Find Duplicate File in System
 *
 * Given a list of directory info including directory path, and all the files with contents in this directory,
 * you need to find out all the groups of duplicate files in the file system in terms of their paths.
 */
class FindDuplicateFileInSystem {
    /**
     * LeetCode version: Input is list of strings like "root/a 1.txt(abcd) 2.txt(efgh)"
     */
    public List<List<String>> findDuplicate(String[] paths) {
        Map<String, List<String>> contentToFiles = new HashMap<>();

        for (String path : paths) {
            String[] parts = path.split(" ");
            String directory = parts[0];

            for (int i = 1; i < parts.length; i++) {
                int openParen = parts[i].indexOf('(');
                String fileName = parts[i].substring(0, openParen);
                String content = parts[i].substring(openParen + 1, parts[i].length() - 1);

                String fullPath = directory + "/" + fileName;
                contentToFiles.putIfAbsent(content, new ArrayList<>());
                contentToFiles.get(content).add(fullPath);
            }
        }

        List<List<String>> result = new ArrayList<>();
        for (List<String> files : contentToFiles.values()) {
            if (files.size() > 1) {
                result.add(files);
            }
        }

        return result;
    }
}

/**
 * Test cases and examples
 */
class RemoveDuplicateFilesTest {
    public static void main(String[] args) {
        testLeetCodeVersion();
        testOptimizationComparison();
    }

    private static void testLeetCodeVersion() {
        System.out.println("=== LeetCode 609: Find Duplicate File in System ===\n");
        FindDuplicateFileInSystem solution = new FindDuplicateFileInSystem();

        String[] paths1 = {
            "root/a 1.txt(abcd) 2.txt(efgh)",
            "root/c 3.txt(abcd)",
            "root/c/d 4.txt(efgh)",
            "root 4.txt(efgh)"
        };

        List<List<String>> result1 = solution.findDuplicate(paths1);
        System.out.println("Test 1: Multiple duplicate groups");
        System.out.println("  Output:");
        for (List<String> group : result1) {
            System.out.println("    " + group);
        }
        System.out.println();

        String[] paths2 = {
            "root/a 1.txt(abcd) 2.txt(efgh)",
            "root/c 3.txt(abcd)",
            "root/c/d 4.txt(efgh)"
        };

        List<List<String>> result2 = solution.findDuplicate(paths2);
        System.out.println("Test 2: Two duplicate groups");
        System.out.println("  Output:");
        for (List<String> group : result2) {
            System.out.println("    " + group);
        }
        System.out.println();
    }

    private static void testOptimizationComparison() {
        System.out.println("=== Optimization Strategies Comparison ===\n");

        System.out.println("1. Basic Approach:");
        System.out.println("   - Hash all files regardless of size");
        System.out.println("   - Time: O(N * M) where N=files, M=avg size");
        System.out.println("   - Use case: Small directories with small files");
        System.out.println();

        System.out.println("2. Size Pre-filtering:");
        System.out.println("   - Group by size first, only hash files with same size");
        System.out.println("   - Time: O(N + K * M) where K=files with duplicates");
        System.out.println("   - Speedup: 10-100x for directories with many unique sizes");
        System.out.println();

        System.out.println("3. Partial Hash:");
        System.out.println("   - Hash first 1KB, then full hash only if needed");
        System.out.println("   - Time: O(N + K1 + K2 * M) where K1=partial collisions");
        System.out.println("   - Speedup: 5-50x for large files with early differences");
        System.out.println();

        System.out.println("4. Multi-threading:");
        System.out.println("   - Parallel hash computation using thread pool");
        System.out.println("   - Time: O((N * M) / T) where T=number of threads");
        System.out.println("   - Speedup: 2-8x on multi-core systems");
        System.out.println();

        System.out.println("5. Additional Optimizations:");
        System.out.println("   - Use faster hash (xxHash, CRC32 vs MD5/SHA)");
        System.out.println("   - Memory-mapped files for large files");
        System.out.println("   - Skip system/hidden files");
        System.out.println("   - Use file system metadata (inode) to detect hard links");
        System.out.println("   - Incremental updates (only check new/modified files)");
        System.out.println();
    }
}

/**
 * Follow-up Questions and Answers
 */
class FollowUpQuestions {
    /*
     * Q1: How would you handle very large files (GB+)?
     * A1:
     *   - Use memory-mapped files (MappedByteBuffer)
     *   - Process files in chunks
     *   - Use streaming hash computation
     *   - Consider partial comparison (sample random blocks)
     *
     * Q2: How to handle symbolic links?
     * A2:
     *   - Use Files.isSymbolicLink() to detect
     *   - Optionally follow or skip symbolic links
     *   - Track visited files by inode to avoid cycles
     *
     * Q3: What if files are being modified during scan?
     * A3:
     *   - Lock files during hash computation
     *   - Use file modification time to detect changes
     *   - Re-verify duplicates before deletion
     *   - Use atomic operations for deletion
     *
     * Q4: How to distribute this across multiple machines?
     * A4:
     *   - Partition by directory or file size range
     *   - Use distributed hash table (DHT)
     *   - Central coordinator collects results
     *   - MapReduce pattern: Map=hash files, Reduce=group duplicates
     *
     * Q5: How to handle network file systems (NFS)?
     * A5:
     *   - Minimize round trips (batch operations)
     *   - Cache file metadata
     *   - Use rsync-style algorithms
     *   - Consider comparing by chunks over network
     */
}
