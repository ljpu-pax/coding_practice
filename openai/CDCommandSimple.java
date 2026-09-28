package openai;

import java.util.*;

/**
 * Implement CD Command - SIMPLIFIED for 40 min interview
 *
 * Problem:
 * Implement cd(currentDir, newDir) like Linux terminal
 *
 * Part 1 (15 min): Basic path resolution
 * - cd("/foo/bar", "baz") = "/foo/bar/baz"
 * - cd("/foo/../", "./baz") = "/baz"
 * - cd("/", "..") = NULL (error)
 * - Handle: '.', '..', '//', trailing '/'
 *
 * Part 2 (5 min): Home directory '~'
 * - cd("/foo", "~/bar") = "/home/user/bar"
 *
 * Part 3 (15 min): Simple symlinks
 * - cd("/foo/bar", "baz", {"/foo/bar": "/abc"}) = "/abc/baz"
 * - Exact match only (no Trie needed for interview!)
 *
 * Discussion: Linux filesystem (5 min if asked)
 * - Inodes: data structure storing file metadata
 * - Directory: mapping name -> inode
 * - Symlinks: file containing path to another file
 *
 * Time: O(n) where n is path length
 * Space: O(n) for stack
 */
public class CDCommandSimple {

    /**
     * Part 1: Basic CD
     */
    public static String cd(String currentDir, String newDir) {
        String fullPath = currentDir + "/" + newDir;
        return simplifyPath(fullPath);
    }

    /**
     * Core method: Simplify path
     * Key: Use stack to handle '..'
     */
    public static String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();
        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.equals("..")) {
                if (stack.isEmpty()) {
                    return "NULL";  // Error: trying to go above root
                }
                stack.pop();
            } else if (!part.isEmpty() && !part.equals(".")) {
                stack.push(part);
            }
            // Skip empty parts (from //) and "."
        }

        // Build result
        if (stack.isEmpty()) return "/";

        StringBuilder sb = new StringBuilder();
        for (String dir : stack) {
            sb.insert(0, "/" + dir);
        }
        return sb.toString();
    }

    /**
     * Part 2: CD with home directory
     */
    public static String cdWithHome(String currentDir, String newDir, String homeDir) {
        // Replace ~ with home directory
        if (newDir.equals("~")) {
            newDir = homeDir;
        } else if (newDir.startsWith("~/")) {
            newDir = homeDir + newDir.substring(1);
        }

        return cd(currentDir, newDir);
    }

    /**
     * Part 3: CD with symlinks (SIMPLIFIED - exact match only)
     *
     * For interview: Just check exact path matches in symlink map
     * Skip Trie complexity unless specifically asked!
     */
    public static String cdWithSymlinks(String currentDir, String newDir,
                                        Map<String, String> symlinks) {
        // Get simplified path first
        String path = cd(currentDir, newDir);
        if (path.equals("NULL")) return path;

        // Simple approach: Check if any prefix is a symlink
        // Start from longest to shortest (greedy)
        String[] parts = path.split("/");

        for (int len = parts.length; len > 0; len--) {
            // Build prefix
            StringBuilder prefix = new StringBuilder();
            for (int i = 1; i <= len; i++) {  // Skip empty first element
                prefix.append("/").append(parts[i]);
            }

            String prefixPath = prefix.toString();
            if (symlinks.containsKey(prefixPath)) {
                // Found symlink - replace prefix
                String target = symlinks.get(prefixPath);

                // Add remaining parts
                for (int i = len + 1; i < parts.length; i++) {
                    target += "/" + parts[i];
                }

                return simplifyPath(target);
            }
        }

        return path;
    }

    /**
     * Tests
     */
    public static void main(String[] args) {
        System.out.println("=== Part 1: Basic CD ===");

        test(cd("/foo/bar", "baz"), "/foo/bar/baz");
        test(cd("/foo/../", "./baz"), "/baz");
        test(cd("/", "foo/bar/../../baz"), "/baz");
        test(cd("/", ".."), "NULL");
        test(cd("/a/b/c", "../d"), "/a/b/d");
        test(cd("/a/b", "./c/./d"), "/a/b/c/d");
        test(cd("/a/b/", "c//d"), "/a/b/c/d");
        System.out.println();

        System.out.println("=== Part 2: Home Directory ===");

        test(cdWithHome("/foo", "~/bar", "/home/user"), "/home/user/bar");
        test(cdWithHome("/foo", "~", "/home/user"), "/home/user");
        test(cdWithHome("/foo", "~/docs/file.txt", "/home/user"), "/home/user/docs/file.txt");
        System.out.println();

        System.out.println("=== Part 3: Symlinks ===");

        Map<String, String> symlinks1 = new HashMap<>();
        symlinks1.put("/foo/bar", "/abc");
        test(cdWithSymlinks("/foo/bar", "baz", symlinks1), "/abc/baz");
        System.out.println();

        Map<String, String> symlinks2 = new HashMap<>();
        symlinks2.put("/usr/local", "/opt");
        test(cdWithSymlinks("/usr/local", "bin/gcc", symlinks2), "/opt/bin/gcc");
        System.out.println();

        // Longest match
        Map<String, String> symlinks3 = new HashMap<>();
        symlinks3.put("/foo", "/x");
        symlinks3.put("/foo/bar", "/y");
        test(cdWithSymlinks("/foo/bar", "baz", symlinks3), "/y/baz");
        System.out.println();
    }

    private static void test(String result, String expected) {
        String status = result.equals(expected) ? "✓" : "✗";
        System.out.println(status + " Got: " + result + " | Expected: " + expected);
    }
}

/**
 * INTERVIEW STRATEGY (40 minutes):
 * =================================
 *
 * Phase 1 (15 min): Basic CD
 * ---------------------------
 * 1. Concatenate current + new path
 * 2. Split by "/"
 * 3. Use stack:
 *    - ".." → pop
 *    - "." or "" → skip
 *    - else → push
 * 4. Handle empty stack (root)
 * 5. Handle pop from empty (error)
 *
 * Phase 2 (5 min): Home directory
 * --------------------------------
 * 1. Check if newDir starts with "~"
 * 2. Replace with homeDir
 * 3. Call basic cd()
 *
 * Phase 3 (15 min): Simple symlinks
 * ----------------------------------
 * 1. First call basic cd()
 * 2. Check each prefix (longest first)
 * 3. If found in map, replace and return
 * 4. Skip Trie unless asked!
 *
 * Phase 4 (5 min): Discussion
 * ----------------------------
 * If asked about Linux filesystem:
 *
 * "Linux uses inodes to store file metadata:
 *  - Inode: data structure with permissions, size, pointers to blocks
 *  - Directory: special file mapping names to inode numbers
 *  - Symlink: file containing text path to another file
 *  - Hard link: multiple directory entries pointing to same inode
 *
 *  When you 'cd', the shell:
 *  1. Resolves path (handles .., ., symlinks)
 *  2. Checks if target is directory (via inode)
 *  3. Updates PWD environment variable
 *  4. No actual 'cd' syscall - it's a shell builtin!"
 *
 * WHAT TO CODE IN 40 MINUTES:
 * ============================
 * ✅ Part 1: Basic cd with stack (15 min)
 * ✅ Part 2: Home directory support (5 min)
 * ✅ Part 3: Simple symlink check (15 min)
 * ✅ Tests (5 min)
 *
 * DON'T CODE (unless specifically asked):
 * ========================================
 * ✗ Trie for symlinks (too complex!)
 * ✗ Cycle detection (mention it, don't code)
 * ✗ Relative symlinks (assume absolute)
 *
 * KEY POINTS TO MENTION:
 * ======================
 * 1. Stack is standard for path simplification
 * 2. Handle edge cases: empty string, root, above root
 * 3. Symlinks can chain - would need cycle detection
 * 4. Real implementation would use system calls
 * 5. PWD is environment variable, not actual directory change
 *
 * FOLLOW-UP ANSWERS:
 * ==================
 * Q: "How to handle cycles in symlinks?"
 * A: Track visited paths in a set, throw error if revisit
 *
 * Q: "What about relative symlinks?"
 * A: Resolve relative to symlink's parent directory
 *
 * Q: "How does real cd work?"
 * A: Shell builtin that calls chdir() syscall, updates PWD
 *
 * Q: "What's the difference between hard and soft links?"
 * A: Hard link: same inode, different names
 *    Soft link: different inode, contains path string
 *
 * TIME BREAKDOWN:
 * ===============
 * 0-2 min: Understand problem, clarify requirements
 * 2-17 min: Code Part 1 (basic cd with stack)
 * 17-22 min: Code Part 2 (home directory)
 * 22-37 min: Code Part 3 (simple symlinks)
 * 37-40 min: Test and discuss
 *
 * This is REALISTIC for 40 minutes! ✅
 */
