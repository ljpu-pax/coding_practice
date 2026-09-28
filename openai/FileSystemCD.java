package openai;

import java.util.*;

/**
 * File System CD Command with Symlinks
 *
 * Problem:
 * Implement cd(currentDir, newDir) that returns the final path.
 *
 * Part 1: Basic path resolution
 * - cd("/foo/bar", "baz") = "/foo/bar/baz"
 * - cd("/foo/../", "./baz") = "/baz"
 * - cd("/", "..") = NULL (cannot go above root)
 *
 * Part 2: Home directory support
 * - cd("/foo", "~/bar") = "/home/user/bar"
 *
 * Part 3: Symlink support (complex!)
 * - cd("/foo/bar", "baz", {"/foo/bar": "/abc"}) = "/abc/baz"
 * - Match longest symlink first (more specific)
 * - Detect cycles
 *
 * Solution: Use Trie to store symlinks for longest-prefix matching
 *
 * Time Complexity:
 * - Without symlinks: O(n) where n is path length
 * - With symlinks: O(n × k) where k is max symlink chain depth
 *
 * Space Complexity: O(m × l) where m is number of symlinks, l is avg path length
 */
public class FileSystemCD {

    /**
     * Part 1: Basic CD without symlinks
     */
    public static String cd(String currentDir, String newDir) {
        String fullPath = currentDir + "/" + newDir;
        return simplifyPath(fullPath);
    }

    /**
     * Part 2: CD with home directory support
     */
    public static String cdWithHome(String currentDir, String newDir, String homeDir) {
        // Expand ~ to home directory
        if (newDir.startsWith("~/")) {
            newDir = homeDir + newDir.substring(1);
        } else if (newDir.equals("~")) {
            newDir = homeDir;
        }

        String fullPath = currentDir + "/" + newDir;
        return simplifyPath(fullPath);
    }

    /**
     * Simplify path by resolving . and ..
     */
    public static String simplifyPath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }

        Deque<String> stack = new ArrayDeque<>();
        String[] tokens = path.split("/");

        for (String token : tokens) {
            if (token.equals("..")) {
                if (stack.isEmpty()) {
                    // Trying to go above root
                    return "NULL";
                }
                stack.pop();
            } else if (!token.isEmpty() && !token.equals(".")) {
                stack.push(token);
            }
        }

        // Build result path
        if (stack.isEmpty()) {
            return "/";
        }

        StringBuilder result = new StringBuilder();
        for (String dir : stack) {
            result.insert(0, "/" + dir);
        }

        return result.toString();
    }

    /**
     * Trie node for storing symlinks
     */
    static class TrieNode {
        String segment;                    // Path segment (e.g., "foo")
        String linkPath;                   // Symlink target (e.g., "/abc")
        Map<String, TrieNode> children;    // Children nodes

        TrieNode(String segment) {
            this.segment = segment;
            this.linkPath = null;
            this.children = new HashMap<>();
        }
    }

    /**
     * Build Trie from symlinks map
     */
    public static TrieNode buildTrie(Map<String, String> symlinks) {
        TrieNode root = new TrieNode("");

        for (Map.Entry<String, String> entry : symlinks.entrySet()) {
            String sourcePath = entry.getKey();
            String targetPath = entry.getValue();
            insertPath(sourcePath, targetPath, root);
        }

        return root;
    }

    /**
     * Insert path into Trie
     */
    private static void insertPath(String path, String linkTarget, TrieNode root) {
        String[] tokens = path.split("/");
        TrieNode current = root;

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            current.children.putIfAbsent(token, new TrieNode(token));
            current = current.children.get(token);
        }

        current.linkPath = linkTarget;
    }

    /**
     * Find longest matching symlink prefix
     * Returns the resolved path tokens or empty list if no match
     */
    private static List<String> findSymlinkMatch(List<String> pathTokens, TrieNode root) {
        TrieNode current = root;
        String longestMatch = null;
        int longestMatchIndex = -1;

        // Find longest prefix match in Trie
        for (int i = 0; i < pathTokens.size(); i++) {
            String token = pathTokens.get(i);

            if (!current.children.containsKey(token)) {
                break;
            }

            current = current.children.get(token);

            // Update longest match if this node has a symlink
            if (current.linkPath != null) {
                longestMatch = current.linkPath;
                longestMatchIndex = i;
            }
        }

        // No match found
        if (longestMatch == null) {
            return Collections.emptyList();
        }

        // Build result: symlink target + remaining path
        List<String> result = new ArrayList<>();
        String[] targetTokens = longestMatch.split("/");

        for (String token : targetTokens) {
            if (!token.isEmpty()) {
                result.add(token);
            }
        }

        // Add remaining path after the matched prefix
        for (int i = longestMatchIndex + 1; i < pathTokens.size(); i++) {
            result.add(pathTokens.get(i));
        }

        return result;
    }

    /**
     * Part 3: CD with symlink support
     */
    public static String cdWithSymlinks(String currentDir, String newDir, Map<String, String> symlinks) {
        // Step 1: Get simplified path
        String path = cd(currentDir, newDir);

        if (path.equals("NULL")) {
            return path;
        }

        // Step 2: Build Trie from symlinks
        TrieNode root = buildTrie(symlinks);

        // Step 3: Convert path to token list
        List<String> pathTokens = new ArrayList<>();
        String[] tokens = path.split("/");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                pathTokens.add(token);
            }
        }

        // Step 4: Resolve symlinks iteratively
        Set<String> visited = new HashSet<>();
        visited.add(tokensToPath(pathTokens));

        while (true) {
            List<String> resolved = findSymlinkMatch(pathTokens, root);

            // No more symlinks to resolve
            if (resolved.isEmpty()) {
                break;
            }

            // Cycle detection
            String resolvedPath = tokensToPath(resolved);
            if (visited.contains(resolvedPath)) {
                throw new IllegalStateException("Symlink loop detected: " + resolvedPath);
            }

            visited.add(resolvedPath);
            pathTokens = resolved;
        }

        return tokensToPath(pathTokens);
    }

    /**
     * Convert token list to path string
     */
    private static String tokensToPath(List<String> tokens) {
        if (tokens.isEmpty()) {
            return "/";
        }
        return "/" + String.join("/", tokens);
    }

    /**
     * Test cases
     */
    public static void main(String[] args) {
        System.out.println("=== Part 1: Basic CD ===");
        System.out.println(cd("/foo/bar", "baz"));           // /foo/bar/baz
        System.out.println(cd("/foo/../", "./baz"));         // /baz
        System.out.println(cd("/", "foo/bar/../../baz"));    // /baz
        System.out.println(cd("/", ".."));                   // NULL
        System.out.println(cd("/a/b/c", "../d"));            // /a/b/d
        System.out.println();

        System.out.println("=== Part 2: Home Directory Support ===");
        System.out.println(cdWithHome("/foo", "~/bar", "/home/user"));      // /home/user/bar
        System.out.println(cdWithHome("/foo", "~", "/home/user"));          // /home/user
        System.out.println(cdWithHome("/foo", "~/docs/file", "/home/user")); // /home/user/docs/file
        System.out.println();

        System.out.println("=== Part 3: Symlinks - Basic ===");
        Map<String, String> symlinks1 = new HashMap<>();
        symlinks1.put("/foo/bar", "/abc");
        System.out.println(cdWithSymlinks("/foo/bar", "baz", symlinks1));  // /abc/baz
        System.out.println();

        System.out.println("=== Part 3: Symlinks - Chain ===");
        Map<String, String> symlinks2 = new HashMap<>();
        symlinks2.put("/foo/bar", "/abc");
        symlinks2.put("/abc", "/bcd");
        symlinks2.put("/bcd/baz", "/xyz");
        System.out.println(cdWithSymlinks("/foo/bar", "baz", symlinks2));  // /xyz
        System.out.println();

        System.out.println("=== Part 3: Symlinks - Longest Match ===");
        Map<String, String> symlinks3 = new HashMap<>();
        symlinks3.put("/foo/bar", "/abc");
        symlinks3.put("/foo/bar/baz", "/xyz");
        System.out.println(cdWithSymlinks("/foo/bar", "baz", symlinks3));  // /xyz (more specific)
        System.out.println();

        System.out.println("=== Part 3: Symlinks - No match ===");
        Map<String, String> symlinks4 = new HashMap<>();
        symlinks4.put("/foo/bar", "/abc");
        System.out.println(cdWithSymlinks("/other/path", "baz", symlinks4));  // /other/path/baz
        System.out.println();

        System.out.println("=== Part 3: Symlinks - Cycle Detection ===");
        try {
            Map<String, String> symlinks5 = new HashMap<>();
            symlinks5.put("/a", "/b");
            symlinks5.put("/b", "/a");
            cdWithSymlinks("/a", "file", symlinks5);
            System.out.println("ERROR: Cycle not detected!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Cycle detected: " + e.getMessage());
        }
        System.out.println();

        System.out.println("=== Part 3: Symlinks - Complex chain ===");
        Map<String, String> symlinks6 = new HashMap<>();
        symlinks6.put("/usr/local", "/opt/local");
        symlinks6.put("/opt/local/bin", "/tools/bin");
        System.out.println(cdWithSymlinks("/usr/local", "bin/gcc", symlinks6));  // /tools/bin/gcc
    }
}

/**
 * INTERVIEW STRATEGY:
 * ===================
 *
 * Part 1 (15 min): Basic path resolution
 * ----------------------------------------
 * 1. Concatenate current + new path
 * 2. Split by "/"
 * 3. Use stack: push dirs, pop on "..", skip "." and ""
 * 4. Return "NULL" if try to pop empty stack
 * 5. Build result from stack
 *
 * Part 2 (5 min): Home directory
 * -------------------------------
 * 1. Check if newDir starts with "~"
 * 2. Replace ~ with homeDir
 * 3. Continue with Part 1 logic
 *
 * Part 3 (25 min): Symlinks
 * --------------------------
 * 1. Build Trie from symlinks
 *    - Each path segment is a Trie node
 *    - Leaf nodes store symlink target
 *
 * 2. Find longest matching prefix
 *    - Traverse Trie following path tokens
 *    - Track the longest node with linkPath set
 *
 * 3. Replace and iterate
 *    - Replace matched prefix with symlink target
 *    - Append remaining path
 *    - Repeat until no more matches
 *
 * 4. Cycle detection
 *    - Track visited paths
 *    - Throw error if revisit
 *
 * TOTAL: ~45 minutes
 *
 * KEY INSIGHTS:
 * =============
 * 1. Trie ensures longest-prefix matching (O(n) lookup)
 * 2. Iterative resolution handles symlink chains
 * 3. Visited set prevents infinite loops
 * 4. Stack-based path simplification is standard approach
 *
 * FOLLOW-UP QUESTIONS:
 * ====================
 * Q: "What if symlink points to relative path?"
 * A: Resolve it relative to symlink's parent directory
 *
 * Q: "What about . and .. in symlink targets?"
 * A: Simplify symlink targets when building Trie
 *
 * Q: "Performance with many symlinks?"
 * A: Trie gives O(n) lookup where n is path length
 *    Better than linear search through all symlinks
 *
 * Q: "What if symlink is in the middle of path?"
 * A: Trie handles this - we match longest prefix,
 *    then append remaining path segments
 */
