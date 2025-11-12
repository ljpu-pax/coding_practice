package anthropic;

import java.util.*;
import java.security.*;

/**
 * LeetCode 609 - Find Duplicate File in System
 *
 * Problem:
 *   Given a list of directory info strings, group file paths that have the same content.
 *
 * Example input:
 *   ["root/a 1.txt(abcd) 2.txt(efgh)",
 *    "root/c 3.txt(abcd)",
 *    "root/c/d 4.txt(efgh)",
 *    "root 4.txt(efgh)"]
 *
 * Expected output (order may vary):
 *   [["root/a/1.txt","root/c/3.txt"],
 *    ["root/a/2.txt","root/c/d/4.txt","root/4.txt"]]
 *
 * -------------------------------------------------------------
 * Follow-up Questions & Answers:
 *
 * Q1: What is the time complexity of the basic solution?
 * A1: O(total_size), where total_size is the sum of all file contents. We must read all bytes.
 *
 * Q2: Can we improve efficiency?
 * A2: Yes, by layered filtering:
 *     1. Group by file size (cheap metadata).
 *     2. For same-size files, compute a cheap hash of first N bytes (e.g., first 1KB).
 *     3. Only then compute full strong hash (e.g., SHA-256).
 *     This avoids reading entire files unnecessarily in most cases.
 *
 * Q3: What about worst case?
 * A3: Worst case is still O(total_size) (e.g., all files identical in size and prefix).
 *
 * Q4: What if files are huge or we need to scale?
 * A4: Use block-level hashing (Merkle tree) or distributed MapReduce/Spark:
 *     - Map = compute hash for each file
 *     - Reduce = group by hash and output duplicates
 *
 * Q5: How to build a continuous monitoring system?
 * A5: Maintain two mappings in a database:
 *     - hash -> list of files (to detect duplicates & notify owners)
 *     - file -> hash (to handle deletions)
 *     Incremental hashing at file creation avoids rescanning the whole disk.
 *
 * -------------------------------------------------------------
 * Two Solutions Below:
 *   1. BasicSolution  - solves LeetCode directly
 *   2. AdvancedSolution - adds layered filtering (size → prefix hash → full hash)
 */

public class DuplicateFiles {

    // -------------------------------------------------------------
    // 1. Basic Solution
    // -------------------------------------------------------------
    static class BasicSolution {
        public List<List<String>> findDuplicate(String[] paths) {
            Map<String, List<String>> contentMap = new HashMap<>();

            for (String path : paths) {
                String[] parts = path.split(" ");
                String dir = parts[0];

                for (int i = 1; i < parts.length; i++) {
                    String file = parts[i];
                    int l = file.indexOf('('), r = file.indexOf(')');
                    String fileName = file.substring(0, l);
                    String content = file.substring(l + 1, r);

                    contentMap.computeIfAbsent(content, k -> new ArrayList<>())
                              .add(dir + "/" + fileName);
                }
            }

            List<List<String>> res = new ArrayList<>();
            for (List<String> group : contentMap.values()) {
                if (group.size() > 1) res.add(group);
            }
            return res;
        }
    }

    // -------------------------------------------------------------
    // 2. Advanced Solution with Layered Filtering
    // -------------------------------------------------------------
    static class AdvancedSolution {
        static class FileData {
            String path;
            long size;
            byte[] content;
            FileData(String p, long s, byte[] c) {
                path = p; size = s; content = c;
            }
        }

        private String hashBytes(byte[] data) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(data);
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) sb.append(String.format("%02x", b));
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String prefixHash(byte[] data, int n) {
            int len = Math.min(data.length, n);
            return hashBytes(Arrays.copyOfRange(data, 0, len));
        }

        public List<List<String>> findDuplicate(List<FileData> files) {
            // Step 1: group by file size
            Map<Long, List<FileData>> sizeGroups = new HashMap<>();
            for (FileData f : files) {
                sizeGroups.computeIfAbsent(f.size, k -> new ArrayList<>()).add(f);
            }

            List<List<String>> result = new ArrayList<>();

            // Step 2: refine within each size group
            for (List<FileData> sameSize : sizeGroups.values()) {
                if (sameSize.size() == 1) continue;

                Map<String, List<FileData>> prefixGroups = new HashMap<>();
                for (FileData f : sameSize) {
                    String prefix = prefixHash(f.content, 1024);
                    prefixGroups.computeIfAbsent(prefix, k -> new ArrayList<>()).add(f);
                }

                // Step 3: full SHA-256 for candidates
                for (List<FileData> samePrefix : prefixGroups.values()) {
                    if (samePrefix.size() == 1) continue;

                    Map<String, List<String>> hashGroups = new HashMap<>();
                    for (FileData f : samePrefix) {
                        String hash = hashBytes(f.content);
                        hashGroups.computeIfAbsent(hash, k -> new ArrayList<>()).add(f.path);
                    }

                    for (List<String> group : hashGroups.values()) {
                        if (group.size() > 1) result.add(group);
                    }
                }
            }

            return result;
        }
    }

    // -------------------------------------------------------------
    // Main method with tests
    // -------------------------------------------------------------
    public static void main(String[] args) {
        // Test BasicSolution
        BasicSolution basic = new BasicSolution();
        String[] input1 = {
            "root/a 1.txt(abcd) 2.txt(efgh)",
            "root/c 3.txt(abcd)",
            "root/c/d 4.txt(efgh)",
            "root 4.txt(efgh)"
        };
        System.out.println("BasicSolution:");
        System.out.println(basic.findDuplicate(input1));
        // Expected: [[root/a/1.txt, root/c/3.txt], [root/a/2.txt, root/c/d/4.txt, root/4.txt]]

        // Test AdvancedSolution
        AdvancedSolution adv = new AdvancedSolution();
        List<AdvancedSolution.FileData> files = new ArrayList<>();
        files.add(new AdvancedSolution.FileData("dir1/1.txt", 4, "abcd".getBytes()));
        files.add(new AdvancedSolution.FileData("dir2/2.txt", 4, "abcd".getBytes()));
        files.add(new AdvancedSolution.FileData("dir3/3.txt", 4, "efgh".getBytes()));
        files.add(new AdvancedSolution.FileData("dir4/4.txt", 4, "abcd".getBytes()));

        System.out.println("AdvancedSolution:");
        System.out.println(adv.findDuplicate(files));
        // Expected: [[dir1/1.txt, dir2/2.txt, dir4/4.txt]]
    }
}
