// 📝 Code Interview – Scrabble 題
// 題目 1 – Dictionary Lookup
// 給定 dictionary 檔 (dictionary.txt)，要求：
// 將單字讀入 set 中
// 判斷 "DDA" 與 "ADD" 是否存在於 dictionary



// 題目 2 – Find All Valid Permutations
// 實作 function find_all_possible(string: str, lookup: set)：
// 找出輸入字串（如 "DDA"）所有長度 ≥ 2 的排列組合，且存在於 dictionary 的單字
// 回傳 list

// 題目 3 – Validate Scrabble Board
// 給定多個 Scrabble board (boards.txt)，每個為 15x15 字母矩陣。實作 valid_board(board) 判斷是否有效。
// 定義有效
// 中央位置 (第8行第8列) 有字母
// 所有 row 與 column 上形成的單字（長度 ≥ 2）存在於 dictionary
// 所有放置的字母形成一個連通區塊

// 🗣️ 補充: 提早解完後，與面試官討論如何用 AI 解此類對弈 open problems。

package ml;

import java.io.*;
import java.util.*;

public class ScrabbleSolver {

    private static final int BOARD_SIZE = 15;
    private static final int MIN_WORD_LENGTH = 2;
    private static final int CENTER = 7; // 0-indexed: 8th row/column = index 7

    public static void main(String[] args) throws IOException {
        Set<String> dictionary = loadDictionary("ml/dictionary.txt");

        // 題目 1: Dictionary Lookup
        System.out.println("== 題目 1 ==");
        lookupWords(dictionary, Arrays.asList("DDA", "ADD"));

        // 題目 2: Find All Valid Permutations
        System.out.println("\n== 題目 2 ==");
        List<String> permutations = findAllPossible("DDA", dictionary);
        System.out.println("Valid permutations: " + permutations);

        // 題目 3: Validate Boards
        System.out.println("\n== 題目 3 ==");
        List<char[][]> boards = loadBoards("ml/boards.txt");
        for (int i = 0; i < boards.size(); i++) {
            boolean isValid = validBoard(boards.get(i), dictionary);
            System.out.println("Board " + (i + 1) + ": " + (isValid ? "Valid" : "Invalid"));
        }
    }

    // 題目 1: Load dictionary
    public static Set<String> loadDictionary(String path) throws IOException {
        Set<String> dict = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                dict.add(line.trim().toUpperCase());
            }
        }
        return dict;
    }

    public static void lookupWords(Set<String> dictionary, List<String> words) {
        for (String word : words) {
            if (dictionary.contains(word.toUpperCase())) {
                System.out.println(word + " is in dictionary.");
            } else {
                System.out.println(word + " is NOT in dictionary.");
            }
        }
    }

    // 題目 2: Find All Valid Permutations
    public static List<String> findAllPossible(String input, Set<String> dictionary) {
        Set<String> results = new HashSet<>();
        char[] arr = input.toUpperCase().toCharArray();
        boolean[] used = new boolean[arr.length];
        backtrack("", arr, used, dictionary, results);
        return new ArrayList<>(results);
    }

    private static void backtrack(String current, char[] arr, boolean[] used,
                                  Set<String> dictionary, Set<String> results) {
        if (current.length() >= MIN_WORD_LENGTH && dictionary.contains(current)) {
            results.add(current);
        }
        if (current.length() == arr.length) return;

        for (int i = 0; i < arr.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            backtrack(current + arr[i], arr, used, dictionary, results);
            used[i] = false;
        }
    }

    // 題目 3: Load boards
    public static List<char[][]> loadBoards(String path) throws IOException {
        List<char[][]> boards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            List<char[]> board = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!board.isEmpty()) {
                        boards.add(board.toArray(new char[BOARD_SIZE][]));
                        board.clear();
                    }
                } else {
                    board.add(line.trim().toUpperCase().toCharArray());
                }
            }
            if (!board.isEmpty()) {
                boards.add(board.toArray(new char[BOARD_SIZE][]));
            }
        }
        return boards;
    }

    public static boolean validBoard(char[][] board, Set<String> dictionary) {
        if (board[CENTER][CENTER] == ' ' || board[CENTER][CENTER] == '.') return false;

        Set<String> allWords = new HashSet<>();

        // 橫向
        for (int i = 0; i < BOARD_SIZE; i++) {
            int j = 0;
            while (j < BOARD_SIZE) {
                StringBuilder sb = new StringBuilder();
                while (j < BOARD_SIZE && board[i][j] != ' ' && board[i][j] != '.') {
                    sb.append(board[i][j]);
                    j++;
                }
                if (sb.length() >= MIN_WORD_LENGTH) {
                    allWords.add(sb.toString());
                }
                j++;
            }
        }

        // 縱向
        for (int j = 0; j < BOARD_SIZE; j++) {
            int i = 0;
            while (i < BOARD_SIZE) {
                StringBuilder sb = new StringBuilder();
                while (i < BOARD_SIZE && board[i][j] != ' ' && board[i][j] != '.') {
                    sb.append(board[i][j]);
                    i++;
                }
                if (sb.length() >= MIN_WORD_LENGTH) {
                    allWords.add(sb.toString());
                }
                i++;
            }
        }

        for (String word : allWords) {
            if (!dictionary.contains(word)) return false;
        }

        return isConnected(board);
    }

    private static boolean isConnected(char[][] board) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        Queue<int[]> queue = new LinkedList<>();

        // 找到第一個非空字母起點
        outer:
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (Character.isLetter(board[i][j])) {
                    queue.offer(new int[]{i, j});
                    visited[i][j] = true;
                    break outer;
                }
            }
        }

        if (queue.isEmpty()) return false;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            for (int[] d : dirs) {
                int ni = curr[0] + d[0], nj = curr[1] + d[1];
                if (ni >= 0 && ni < BOARD_SIZE && nj >= 0 && nj < BOARD_SIZE &&
                        !visited[ni][nj] && Character.isLetter(board[ni][nj])) {
                    visited[ni][nj] = true;
                    queue.offer(new int[]{ni, nj});
                }
            }
        }

        // 檢查所有字母是否都被訪問
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (Character.isLetter(board[i][j]) && !visited[i][j]) return false;
            }
        }
        return true;
    }
}
