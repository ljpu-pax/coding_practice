package nvda;

import java.util.*;

public class WordBreak {
    
    // LeetCode 139: Word Break
    // DP approach - O(n²) time, O(n) space
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true; // Empty string can be segmented
        
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        
        return dp[s.length()];
    }

    // LeetCode 140: Word Break II
    // DFS with memoization - O(2^n) time, O(2^n) space
    public List<String> wordBreakII(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, List<String>> memo = new HashMap<>();
        return dfs(s, wordSet, memo);
    }
    
    private List<String> dfs(String s, Set<String> wordSet, Map<String, List<String>> memo) {
        if (memo.containsKey(s)) return memo.get(s);
        
        List<String> result = new ArrayList<>();
        if (s.length() == 0) {
            result.add("");
            return result;
        }
        
        for (int i = 1; i <= s.length(); i++) {
            String prefix = s.substring(0, i);
            if (wordSet.contains(prefix)) {
                List<String> suffixes = dfs(s.substring(i), wordSet, memo);
                for (String suffix : suffixes) {
                    result.add(prefix + (suffix.isEmpty() ? "" : " " + suffix));
                }
            }
        }
        
        memo.put(s, result);
        return result;
    }

    // LeetCode 472: Concatenated Words
    // Word Break variation - O(n * m²) time, O(n * m) space
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        Set<String> wordSet = new HashSet<>();
        for (String word : words) {
            if (word.length() > 0) {
                wordSet.add(word);
            }
        }
        
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (word.length() > 0 && canForm(word, wordSet)) {
                result.add(word);
            }
        }
        
        return result;
    }
    
    private boolean canForm(String word, Set<String> wordSet) {
        boolean[] dp = new boolean[word.length() + 1];
        dp[0] = true;
        
        for (int i = 1; i <= word.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(word.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        
        return dp[word.length()];
    }

    // LeetCode 127: Word Ladder
    // BFS approach - O(M² * N) time, O(M * N) space where M is word length, N is word list size
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;
        
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        int level = 1;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                
                if (current.equals(endWord)) return level;
                
                char[] chars = current.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];
                    
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        
                        chars[j] = c;
                        String newWord = new String(chars);
                        
                        if (wordSet.contains(newWord)) {
                            queue.offer(newWord);
                            wordSet.remove(newWord); // Avoid revisiting
                        }
                    }
                    
                    chars[j] = original; // Restore
                }
            }
            
            level++;
        }
        
        return 0;
    }

    // LeetCode 126: Word Ladder II
    // BFS to find shortest paths - O(M² * N) time, O(M * N) space
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        List<List<String>> result = new ArrayList<>();
        
        if (!wordSet.contains(endWord)) return result;
        
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> distance = new HashMap<>();
        
        // BFS to build graph and find distances
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        distance.put(beginWord, 0);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(endWord)) break;
            
            char[] chars = current.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char original = chars[i];
                
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == original) continue;
                    
                    chars[i] = c;
                    String newWord = new String(chars);
                    
                    if (wordSet.contains(newWord)) {
                        if (!distance.containsKey(newWord)) {
                            distance.put(newWord, distance.get(current) + 1);
                            queue.offer(newWord);
                        }
                        
                        if (distance.get(newWord) == distance.get(current) + 1) {
                            graph.computeIfAbsent(current, k -> new ArrayList<>()).add(newWord);
                        }
                    }
                }
                
                chars[i] = original;
            }
        }
        
        // DFS to find all shortest paths
        List<String> path = new ArrayList<>();
        path.add(beginWord);
        dfs(beginWord, endWord, graph, path, result);
        
        return result;
    }
    
    private void dfs(String current, String endWord, Map<String, List<String>> graph, 
                    List<String> path, List<List<String>> result) {
        if (current.equals(endWord)) {
            result.add(new ArrayList<>(path));
            return;
        }
        
        if (!graph.containsKey(current)) return;
        
        for (String next : graph.get(current)) {
            path.add(next);
            dfs(next, endWord, graph, path, result);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        WordBreak solver = new WordBreak();
        
        // Test Word Break
        System.out.println("=== Word Break (LeetCode 139) ===");
        String s1 = "leetcode";
        List<String> wordDict1 = Arrays.asList("leet", "code");
        System.out.println("s = \"" + s1 + "\", wordDict = " + wordDict1);
        System.out.println("Result: " + solver.wordBreak(s1, wordDict1));
        
        String s2 = "applepenapple";
        List<String> wordDict2 = Arrays.asList("apple", "pen");
        System.out.println("s = \"" + s2 + "\", wordDict = " + wordDict2);
        System.out.println("Result: " + solver.wordBreak(s2, wordDict2));
        System.out.println();
        
        // Test Word Break II
        System.out.println("=== Word Break II (LeetCode 140) ===");
        String s3 = "catsanddog";
        List<String> wordDict3 = Arrays.asList("cat", "cats", "and", "sand", "dog");
        System.out.println("s = \"" + s3 + "\", wordDict = " + wordDict3);
        System.out.println("Result: " + solver.wordBreakII(s3, wordDict3));
        System.out.println();
        
        // Test Concatenated Words
        System.out.println("=== Concatenated Words (LeetCode 472) ===");
        String[] words = {"cat", "cats", "catsdogcats", "dog", "dogcatsdog", "hippopotamuses", "rat", "ratcatdogcat"};
        System.out.println("Words: " + Arrays.toString(words));
        System.out.println("Concatenated words: " + solver.findAllConcatenatedWordsInADict(words));
        System.out.println();
        
        // Test Word Ladder
        System.out.println("=== Word Ladder (LeetCode 127) ===");
        String beginWord = "hit";
        String endWord = "cog";
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println("beginWord = \"" + beginWord + "\", endWord = \"" + endWord + "\"");
        System.out.println("wordList = " + wordList);
        System.out.println("Length: " + solver.ladderLength(beginWord, endWord, wordList));
        System.out.println();
        
        // Test Word Ladder II
        System.out.println("=== Word Ladder II (LeetCode 126) ===");
        System.out.println("All shortest transformation sequences:");
        List<List<String>> ladders = solver.findLadders(beginWord, endWord, wordList);
        for (List<String> ladder : ladders) {
            System.out.println(ladder);
        }
    }
}
