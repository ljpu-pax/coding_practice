// Download the following file: https://raw.githubusercontent.com/jonbcard/scrabble-bot/master/src/dictionary.txt 

// Use the dictionary to determine if a word is valid. 
// Test Cases: 
// Input: "DDA", Output: False
// Input: "ADD", Output: True

// Note: You are welcome to look up anything you normally would, from syntax to algorithms

// Given an input string, return all possible valid words. 
// Test Case: 
// Input: "DDA", Output: "ADD", "DAD", "AD"
// da, dad, ad, add,  
// Input: "SBAD", Output: 'AS', 'BAD', 'AD', 'DAB', 'BA', 'BADS', 'AB', 'ABS', 'SAB', 'DABS', 'ADS', 'BAS', 'SAD'

// Note: You can ignore one letter words

// Given an input string, return all possible valid words. 
// Test Case: 
// Input: "DDA", Output: "ADD", "DAD", "AD"
// Input: "SBAD", Output: 'AS', 'BAD', 'AD', 'DAB', 'BA', 'BADS', 'AB', 'ABS', 'SAB', 'DABS', 'ADS', 'BAS', 'SAD'

// Note: You can ignore one letter words
 
// Check if a given board is valid in scrabble: 
// All words are valid
// Letter on the center tile
// Everything’s connected

// 15x15 Example boards: https://gist.githubusercontent.com/bawr/460fdadd7e6c301471596e3b2f6ed90b/raw/296de5b6ad40050748f166832675b94b2c900db6/boards.txt

package imbue;

import java.io.*;
import java.util.*;

public class FindWords {
    public static void main(String[] args) throws IOException {
        Set<String> dict = loadDictionary("imbue/dictionary.txt");
        
        // part 1 tests
        // System.out.println(findWords(dict, "DDA"));
        // System.out.println(findWords(dict, "ADD"));

        // part 2 tests
        // System.out.println(findAllPossible("DDA", dict));
        // System.out.println(findAllPossible("SBAD", dict));

        // part 3 tests
        System.out.println(loadBoards("imbue/board.txt").get(0));
    }

    // part 1
    public static Set<String> loadDictionary(String path) throws IOException {
        Set<String> dict = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                dict.add(line.trim().toUpperCase());
            }
        } catch(IOException e) {
            System.out.println(e);;
        }

        return dict;
    }

    public static boolean findWords(Set<String> dict, String word) {
        if (dict.contains(word)) {
            return true;
        }
        return false;
    }

    // part 2
    public static List<String> findAllPossible(String input, Set<String> dict) {
        Set<String> results = new HashSet<>();
        char[] arr = input.toUpperCase().toCharArray();
        boolean[] used = new boolean[arr.length];
        backtrack("", results, arr, used, dict);
        return new ArrayList<>(results);
    }

    public static void backtrack(String current, Set<String> results, char[] arr, boolean[] used, Set<String> dict) {
        if (current.length() >= 2 && dict.contains(current)) {
            results.add(current);
        }

        if (current.length() == arr.length) return;

        for (int i = 0; i < arr.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            backtrack(current + arr[i], results, arr, used, dict);
            used[i] = false;
        }
    }

    // part 3
    public static List<char[][]> loadBoards(String path) throws IOException {
        List<char[][]> boards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            List<char[]> board = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!board.isEmpty()) {
                        boards.add(board.toArray(new char[15][]));
                        board.clear();
                    }
                } else {
                    board.add(line.trim().toUpperCase().toCharArray());
                }
            }

            if (!board.isEmpty()) {
                boards.add(board.toArray(new char[15][]));
            }
        }

        return boards;
    }

    public static boolean validateBoard(char[][] board, Set<String> dict) {
        if (board[7][7] == ' ' || board[7][7] == '.') return false;

        Set<String> allWords = new HashSet<>();

        // horizontal way
        for (int i = 0; i < 15; i++) {
            int j = 0;
            while (j < 15) {
                StringBuilder sb = new StringBuilder();
                while (j < 15 && board[i][j] != ' ' && board[i][j] != '.') {
                    sb.append(board[i][j]);
                    j++;
                }
                if (sb.length() >= 2) {
                    allWords.add(sb.toString());
                }
                j++;
            }
        }

        // vertical way
        for (int j = 0; j < 15; j++) {
            int i = 0;
            while (i < 15) {
                StringBuilder sb = new StringBuilder();
                while (i < 15 && board[i][j] != ' ' && board[i][j] != '.') {
                    sb.append(board[i][j]);
                    i++;
                }
                if (sb.length() >= 2) {
                    allWords.add(sb.toString());
                }
                i++;
            }
        }

        for (String word : allWords) {
            if (!dict.contains(word)) return false;
        }

        return isConnected(board);
    }

    public static boolean isConnected(char[][] board) {
        boolean[][] visited = new boolean[15][15];
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (Character.isLetter(board[i][j])) {
                    queue.offer(new int[]{i, j});
                    visited[i][j] = true;
                    break;
                }
            }
        }

        if (queue.isEmpty()) return false;

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            for (int[] d : dirs) {
                int ni = curr + d[0], nj = 
            }
        }
    }
}
