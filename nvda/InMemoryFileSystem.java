package nvda;

import java.util.*;

/**
 * LeetCode 588: Design In-Memory File System
 * 
 * Problem: Design a data structure that simulates an in-memory file system.
 * 
 * Implement the FileSystem class:
 * - FileSystem() Initializes the object of the system.
 * - List<String> ls(String path) If path is a file path, returns a list that only contains this file's name. 
 *   If path is a directory path, returns the list of file and directory names in this directory. 
 *   The answer should in lexicographic order.
 * - void mkdir(String path) Makes a new directory according to the given path. The given directory path 
 *   does not exist. If the middle directories in the path do not exist, you should create them as well.
 * - void addContentToFile(String filePath, String content) If filePath does not exist, creates that file 
 *   containing given content. If filePath already exists, appends the given content to original content.
 * - String readContentFromFile(String filePath) Returns the content in the file at filePath.
 * - long getSize(String path) Returns the total size (in characters) of all files in the given directory 
 *   (including subdirectories). If path is a file, returns the size of that file.
 * 
 * Example:
 * FileSystem fileSystem = new FileSystem();
 * fileSystem.ls("/");                         // return []
 * fileSystem.mkdir("/a/b/c");
 * fileSystem.addContentToFile("/a/b/c/d", "hello");
 * fileSystem.ls("/");                         // return ["a"]
 * fileSystem.readContentFromFile("/a/b/c/d"); // return "hello"
 * 
 * Constraints:
 * - 1 <= path.length, filePath.length <= 100
 * - path and filePath are absolute paths which begin with '/' and do not end with '/' except that the path is just "/".
 * - You can assume that all directory names and file names only contain lowercase letters, and the same names won't exist in the same directory.
 * - The value of content is composed of only lowercase letters, and its length is between 1 and 50.
 * - At most 300 calls will be made to ls, mkdir, addContentToFile, and readContentFromFile.
 */
public class InMemoryFileSystem {
    
    /**
     * Approach 1: Trie-based File System (Most Intuitive)
     * 
     * Uses a Trie data structure where each node represents a directory or file.
     * Each node contains children (subdirectories/files) and content (if it's a file).
     * 
     * Time Complexity:
     * - ls(): O(m + n log n) where m is path length, n is number of items in directory
     * - mkdir(): O(m) where m is path length
     * - addContentToFile(): O(m + k) where m is path length, k is content length
     * - readContentFromFile(): O(m) where m is path length
     * 
     * Space Complexity: O(TOTAL) where TOTAL is total number of characters in all paths and contents
     */
    static class FileSystem {
        private TrieNode root;
        
        private static class TrieNode {
            Map<String, TrieNode> children;
            boolean isFile;
            StringBuilder content;
            
            public TrieNode() {
                children = new HashMap<>();
                isFile = false;
                content = new StringBuilder();
            }
        }
        
        public FileSystem() {
            root = new TrieNode();
        }
        
        public List<String> ls(String path) {
            TrieNode node = navigateToNode(path);
            List<String> result = new ArrayList<>();
            
            if (node.isFile) {
                // If it's a file, return just the file name
                String[] parts = path.split("/");
                result.add(parts[parts.length - 1]);
            } else {
                // If it's a directory, return all children sorted
                result.addAll(node.children.keySet());
                Collections.sort(result);
            }
            
            return result;
        }
        
        public void mkdir(String path) {
            navigateToNode(path);
        }
        
        public void addContentToFile(String filePath, String content) {
            TrieNode node = navigateToNode(filePath);
            node.isFile = true;
            node.content.append(content);
        }
        
        public String readContentFromFile(String filePath) {
            TrieNode node = navigateToNode(filePath);
            return node.content.toString();
        }
        
        public long getSize(String path) {
            TrieNode node = navigateToNode(path);
            return calculateSize(node);
        }
        
        private long calculateSize(TrieNode node) {
            long totalSize = 0;
            
            if (node.isFile) {
                // If it's a file, return its content size
                totalSize += node.content.length();
            }
            
            // Recursively calculate size of all children (subdirectories and files)
            for (TrieNode child : node.children.values()) {
                totalSize += calculateSize(child);
            }
            
            return totalSize;
        }
        
        private TrieNode navigateToNode(String path) {
            TrieNode current = root;
            
            if (path.equals("/")) {
                return current;
            }
            
            String[] parts = path.split("/");
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                if (!current.children.containsKey(part)) {
                    current.children.put(part, new TrieNode());
                }
                current = current.children.get(part);
            }
            
            return current;
        }
    }
    
    /**
     * Approach 2: HashMap-based File System (Alternative Implementation)
     * 
     * Uses nested HashMaps to represent the directory structure.
     * Separates directories and files into different data structures.
     * 
     * Time Complexity: Same as Trie approach
     * Space Complexity: Same as Trie approach
     */
    static class FileSystemHashMap {
        private Map<String, Set<String>> directories;
        private Map<String, StringBuilder> files;
        
        public FileSystemHashMap() {
            directories = new HashMap<>();
            files = new HashMap<>();
            directories.put("/", new HashSet<>());
        }
        
        public List<String> ls(String path) {
            List<String> result = new ArrayList<>();
            
            if (files.containsKey(path)) {
                // It's a file
                String[] parts = path.split("/");
                result.add(parts[parts.length - 1]);
            } else {
                // It's a directory
                Set<String> items = directories.getOrDefault(path, new HashSet<>());
                result.addAll(items);
                Collections.sort(result);
            }
            
            return result;
        }
        
        public void mkdir(String path) {
            String[] parts = path.split("/");
            StringBuilder currentPath = new StringBuilder();
            
            for (int i = 1; i < parts.length; i++) {
                String parent = currentPath.length() == 0 ? "/" : currentPath.toString();
                currentPath.append("/").append(parts[i]);
                String current = currentPath.toString();
                
                directories.putIfAbsent(parent, new HashSet<>());
                directories.putIfAbsent(current, new HashSet<>());
                directories.get(parent).add(parts[i]);
            }
        }
        
        public void addContentToFile(String filePath, String content) {
            // Create parent directories if they don't exist
            String[] parts = filePath.split("/");
            StringBuilder parentPath = new StringBuilder();
            
            for (int i = 1; i < parts.length - 1; i++) {
                String parent = parentPath.length() == 0 ? "/" : parentPath.toString();
                parentPath.append("/").append(parts[i]);
                String current = parentPath.toString();
                
                directories.putIfAbsent(parent, new HashSet<>());
                directories.putIfAbsent(current, new HashSet<>());
                directories.get(parent).add(parts[i]);
            }
            
            // Add file to parent directory
            String parent = parentPath.length() == 0 ? "/" : parentPath.toString();
            String fileName = parts[parts.length - 1];
            
            directories.putIfAbsent(parent, new HashSet<>());
            directories.get(parent).add(fileName);
            
            // Add/append content to file
            files.putIfAbsent(filePath, new StringBuilder());
            files.get(filePath).append(content);
        }
        
        public String readContentFromFile(String filePath) {
            return files.get(filePath).toString();
        }
        
        public long getSize(String path) {
            long totalSize = 0;
            
            // If it's a file, return its size
            if (files.containsKey(path)) {
                return files.get(path).length();
            }
            
            // If it's a directory, calculate total size of all files in this directory and subdirectories
            String prefix = path.equals("/") ? "/" : path + "/";
            
            for (Map.Entry<String, StringBuilder> entry : files.entrySet()) {
                String filePath = entry.getKey();
                if (filePath.startsWith(prefix)) {
                    totalSize += entry.getValue().length();
                }
            }
            
            return totalSize;
        }
    }
    
    /**
     * Approach 3: Path-based File System (String Processing Heavy)
     * 
     * Uses string processing to manage paths and stores everything in maps.
     * Less efficient but demonstrates different approach.
     * 
     * Time Complexity: Higher due to string processing
     * Space Complexity: Similar to other approaches
     */
    static class FileSystemPathBased {
        private Set<String> directories;
        private Map<String, StringBuilder> files;
        
        public FileSystemPathBased() {
            directories = new HashSet<>();
            files = new HashMap<>();
            directories.add("/");
        }
        
        public List<String> ls(String path) {
            List<String> result = new ArrayList<>();
            
            if (files.containsKey(path)) {
                // It's a file
                String[] parts = path.split("/");
                result.add(parts[parts.length - 1]);
                return result;
            }
            
            // It's a directory - find all direct children
            Set<String> children = new HashSet<>();
            String prefix = path.equals("/") ? "/" : path + "/";
            
            // Check directories
            for (String dir : directories) {
                if (dir.startsWith(prefix) && !dir.equals(path)) {
                    String remaining = dir.substring(prefix.length());
                    if (!remaining.contains("/")) {
                        children.add(remaining);
                    } else {
                        String firstPart = remaining.split("/")[0];
                        children.add(firstPart);
                    }
                }
            }
            
            // Check files
            for (String file : files.keySet()) {
                if (file.startsWith(prefix)) {
                    String remaining = file.substring(prefix.length());
                    if (!remaining.contains("/")) {
                        children.add(remaining);
                    } else {
                        String firstPart = remaining.split("/")[0];
                        children.add(firstPart);
                    }
                }
            }
            
            result.addAll(children);
            Collections.sort(result);
            return result;
        }
        
        public void mkdir(String path) {
            String[] parts = path.split("/");
            StringBuilder currentPath = new StringBuilder();
            
            for (int i = 1; i < parts.length; i++) {
                currentPath.append("/").append(parts[i]);
                directories.add(currentPath.toString());
            }
        }
        
        public void addContentToFile(String filePath, String content) {
            // Create parent directories
            String[] parts = filePath.split("/");
            StringBuilder currentPath = new StringBuilder();
            
            for (int i = 1; i < parts.length - 1; i++) {
                currentPath.append("/").append(parts[i]);
                directories.add(currentPath.toString());
            }
            
            // Add/append content to file
            files.putIfAbsent(filePath, new StringBuilder());
            files.get(filePath).append(content);
        }
        
        public String readContentFromFile(String filePath) {
            return files.get(filePath).toString();
        }
        
        public long getSize(String path) {
            long totalSize = 0;
            
            // If it's a file, return its size
            if (files.containsKey(path)) {
                return files.get(path).length();
            }
            
            // If it's a directory, calculate total size of all files in this directory and subdirectories
            String prefix = path.equals("/") ? "/" : path + "/";
            
            for (Map.Entry<String, StringBuilder> entry : files.entrySet()) {
                String filePath = entry.getKey();
                if (filePath.startsWith(prefix)) {
                    totalSize += entry.getValue().length();
                }
            }
            
            return totalSize;
        }
    }
    
    // Test cases
    public static void main(String[] args) {
        System.out.println("Testing In-Memory File System - Multiple Approaches\n");
        
        // Test Trie-based approach
        System.out.println("=== Testing Trie-based Approach ===");
        testFileSystem(new FileSystem());
        
        // Test HashMap-based approach
        System.out.println("\n=== Testing HashMap-based Approach ===");
        testFileSystemHashMap(new FileSystemHashMap());
        
        // Test Path-based approach
        System.out.println("\n=== Testing Path-based Approach ===");
        testFileSystemPathBased(new FileSystemPathBased());
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        performanceTest();
        
        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        testEdgeCases();
        
        // Test getSize functionality
        System.out.println("\n=== Testing getSize Functionality ===");
        testGetSizeFunctionality();
    }
    
    private static void testFileSystem(FileSystem fs) {
        System.out.println("ls(\"/\"): " + fs.ls("/")); // []
        
        fs.mkdir("/a/b/c");
        System.out.println("After mkdir(\"/a/b/c\"):");
        System.out.println("ls(\"/\"): " + fs.ls("/")); // [a]
        System.out.println("ls(\"/a\"): " + fs.ls("/a")); // [b]
        System.out.println("ls(\"/a/b\"): " + fs.ls("/a/b")); // [c]
        
        fs.addContentToFile("/a/b/c/d", "hello");
        System.out.println("After addContentToFile(\"/a/b/c/d\", \"hello\"):");
        System.out.println("ls(\"/a/b/c\"): " + fs.ls("/a/b/c")); // [d]
        System.out.println("readContentFromFile(\"/a/b/c/d\"): " + fs.readContentFromFile("/a/b/c/d")); // hello
        
        fs.addContentToFile("/a/b/c/d", " world");
        System.out.println("After addContentToFile(\"/a/b/c/d\", \" world\"):");
        System.out.println("readContentFromFile(\"/a/b/c/d\"): " + fs.readContentFromFile("/a/b/c/d")); // hello world
        
        fs.addContentToFile("/a/b/e", "file");
        System.out.println("After addContentToFile(\"/a/b/e\", \"file\"):");
        System.out.println("ls(\"/a/b\"): " + fs.ls("/a/b")); // [c, e]
        System.out.println("ls(\"/a/b/e\"): " + fs.ls("/a/b/e")); // [e]
        
        // Test getSize functionality
        System.out.println("\n--- Testing getSize functionality ---");
        System.out.println("getSize(\"/a/b/c/d\"): " + fs.getSize("/a/b/c/d")); // 11 (hello world)
        System.out.println("getSize(\"/a/b/e\"): " + fs.getSize("/a/b/e")); // 4 (file)
        System.out.println("getSize(\"/a/b\"): " + fs.getSize("/a/b")); // 15 (hello world + file)
        System.out.println("getSize(\"/a\"): " + fs.getSize("/a")); // 15 (all files under /a)
        System.out.println("getSize(\"/\"): " + fs.getSize("/")); // 15 (all files in system)
    }
    
    private static void testFileSystemHashMap(FileSystemHashMap fs) {
        System.out.println("ls(\"/\"): " + fs.ls("/")); // []
        
        fs.mkdir("/a/b/c");
        System.out.println("After mkdir(\"/a/b/c\"):");
        System.out.println("ls(\"/\"): " + fs.ls("/")); // [a]
        
        fs.addContentToFile("/a/b/c/d", "hello");
        System.out.println("After addContentToFile(\"/a/b/c/d\", \"hello\"):");
        System.out.println("readContentFromFile(\"/a/b/c/d\"): " + fs.readContentFromFile("/a/b/c/d")); // hello
    }
    
    private static void testFileSystemPathBased(FileSystemPathBased fs) {
        System.out.println("ls(\"/\"): " + fs.ls("/")); // []
        
        fs.mkdir("/a/b/c");
        System.out.println("After mkdir(\"/a/b/c\"):");
        System.out.println("ls(\"/\"): " + fs.ls("/")); // [a]
        
        fs.addContentToFile("/a/b/c/d", "hello");
        System.out.println("After addContentToFile(\"/a/b/c/d\", \"hello\"):");
        System.out.println("readContentFromFile(\"/a/b/c/d\"): " + fs.readContentFromFile("/a/b/c/d")); // hello
    }
    
    private static void performanceTest() {
        int numOperations = 100;
        
        // Test Trie approach
        FileSystem fs1 = new FileSystem();
        long start = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            fs1.mkdir("/dir" + i);
            fs1.addContentToFile("/dir" + i + "/file" + i, "content" + i);
            fs1.ls("/dir" + i);
            fs1.readContentFromFile("/dir" + i + "/file" + i);
        }
        long end = System.nanoTime();
        System.out.println("Trie approach: " + (end - start) / 1000000.0 + " ms");
        
        // Test HashMap approach
        FileSystemHashMap fs2 = new FileSystemHashMap();
        start = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            fs2.mkdir("/dir" + i);
            fs2.addContentToFile("/dir" + i + "/file" + i, "content" + i);
            fs2.ls("/dir" + i);
            fs2.readContentFromFile("/dir" + i + "/file" + i);
        }
        end = System.nanoTime();
        System.out.println("HashMap approach: " + (end - start) / 1000000.0 + " ms");
    }
    
    private static void testEdgeCases() {
        FileSystem fs = new FileSystem();
        
        System.out.println("Testing edge cases:");
        
        // Test root directory
        System.out.println("ls(\"/\") on empty system: " + fs.ls("/")); // []
        
        // Test single level directory
        fs.mkdir("/single");
        System.out.println("ls(\"/\") after mkdir(\"/single\"): " + fs.ls("/")); // [single]
        
        // Test file in root
        fs.addContentToFile("/rootfile", "root content");
        System.out.println("ls(\"/\") after adding root file: " + fs.ls("/")); // [rootfile, single]
        System.out.println("ls(\"/rootfile\"): " + fs.ls("/rootfile")); // [rootfile]
        
        // Test deep nested structure
        fs.mkdir("/a/b/c/d/e/f/g");
        fs.addContentToFile("/a/b/c/d/e/f/g/deep", "deep content");
        System.out.println("readContentFromFile(\"/a/b/c/d/e/f/g/deep\"): " + fs.readContentFromFile("/a/b/c/d/e/f/g/deep"));
        
        // Test multiple files in same directory
        fs.addContentToFile("/a/file1", "content1");
        fs.addContentToFile("/a/file2", "content2");
        fs.addContentToFile("/a/file3", "content3");
        System.out.println("ls(\"/a\") with multiple files: " + fs.ls("/a")); // Should be sorted
        
        // Test appending to existing file
        fs.addContentToFile("/a/file1", " appended");
        System.out.println("readContentFromFile(\"/a/file1\") after append: " + fs.readContentFromFile("/a/file1"));
    }
    
    private static void testGetSizeFunctionality() {
        FileSystem fs = new FileSystem();
        
        System.out.println("Testing getSize functionality:");
        
        // Test empty directory
        System.out.println("getSize(\"/\") on empty system: " + fs.getSize("/")); // 0
        
        // Create some files with known sizes
        fs.addContentToFile("/file1", "hello"); // 5 characters
        fs.addContentToFile("/file2", "world"); // 5 characters
        System.out.println("After adding /file1 (5 chars) and /file2 (5 chars):");
        System.out.println("getSize(\"/file1\"): " + fs.getSize("/file1")); // 5
        System.out.println("getSize(\"/file2\"): " + fs.getSize("/file2")); // 5
        System.out.println("getSize(\"/\"): " + fs.getSize("/")); // 10
        
        // Create nested directory structure
        fs.mkdir("/dir1/subdir1");
        fs.addContentToFile("/dir1/file3", "test"); // 4 characters
        fs.addContentToFile("/dir1/subdir1/file4", "nested"); // 6 characters
        System.out.println("\nAfter creating nested structure:");
        System.out.println("getSize(\"/dir1/file3\"): " + fs.getSize("/dir1/file3")); // 4
        System.out.println("getSize(\"/dir1/subdir1/file4\"): " + fs.getSize("/dir1/subdir1/file4")); // 6
        System.out.println("getSize(\"/dir1/subdir1\"): " + fs.getSize("/dir1/subdir1")); // 6
        System.out.println("getSize(\"/dir1\"): " + fs.getSize("/dir1")); // 10 (4 + 6)
        System.out.println("getSize(\"/\"): " + fs.getSize("/")); // 20 (10 + 10)
        
        // Test appending to existing file
        fs.addContentToFile("/file1", " appended"); // adds 9 more characters
        System.out.println("\nAfter appending to /file1:");
        System.out.println("getSize(\"/file1\"): " + fs.getSize("/file1")); // 14
        System.out.println("getSize(\"/\"): " + fs.getSize("/")); // 29 (14 + 5 + 4 + 6)
        
        // Test empty directory
        fs.mkdir("/empty_dir");
        System.out.println("\nAfter creating empty directory:");
        System.out.println("getSize(\"/empty_dir\"): " + fs.getSize("/empty_dir")); // 0
        
        // Test multiple files in same directory
        fs.addContentToFile("/dir1/file5", "a"); // 1 character
        fs.addContentToFile("/dir1/file6", "bb"); // 2 characters
        fs.addContentToFile("/dir1/file7", "ccc"); // 3 characters
        System.out.println("\nAfter adding multiple files to /dir1:");
        System.out.println("getSize(\"/dir1\"): " + fs.getSize("/dir1")); // 16 (4 + 6 + 1 + 2 + 3)
        System.out.println("getSize(\"/\"): " + fs.getSize("/")); // 35 (14 + 5 + 16)
        
        System.out.println("\n--- Verification with other approaches ---");
        
        // Test HashMap approach
        FileSystemHashMap fsHashMap = new FileSystemHashMap();
        fsHashMap.addContentToFile("/test1", "hello");
        fsHashMap.addContentToFile("/dir/test2", "world");
        System.out.println("HashMap approach - getSize(\"/\"): " + fsHashMap.getSize("/")); // 10
        System.out.println("HashMap approach - getSize(\"/dir\"): " + fsHashMap.getSize("/dir")); // 5
        
        // Test Path-based approach
        FileSystemPathBased fsPathBased = new FileSystemPathBased();
        fsPathBased.addContentToFile("/test1", "hello");
        fsPathBased.addContentToFile("/dir/test2", "world");
        System.out.println("Path-based approach - getSize(\"/\"): " + fsPathBased.getSize("/")); // 10
        System.out.println("Path-based approach - getSize(\"/dir\"): " + fsPathBased.getSize("/dir")); // 5
    }
}
