/**
 * Problem: Optimize Table Column Divider Position
 *
 * Given:
 * - A table with 2 columns
 * - Total table width is known
 * - Content for column 1 and column 2 (list of words)
 * - Column divider can be moved freely
 *
 * Goal: Find the optimal position of the column divider to minimize total table height
 *
 * Rules:
 * - Words wrap to next line if they don't fit in column width
 * - A word cannot be split across lines
 * - If a word is longer than column width, it still takes one line (overflow)
 *
 * Solution: Use binary search to find optimal divider position
 *
 * Example:
 * Total width = 20
 * Column 1 words: ["hello", "world", "foo"]
 * Column 2 words: ["bar", "baz", "test"]
 *
 * Try divider at position 10:
 * Col1 width = 10, Col2 width = 10
 * Col1: "hello" (5), "world" (5) -> 2 lines (can fit both on line 1 if total <= 10)
 * Col2: "bar" (3), "baz" (3), "test" (4) -> similar calculation
 * Table height = max(col1_height, col2_height)
 */
public class TableColumnOptimization {

    /**
     * Find optimal column divider position to minimize table height
     *
     * @param totalWidth total width of the table
     * @param col1Words words in column 1
     * @param col2Words words in column 2
     * @return optimal divider position (width allocated to column 1)
     */
    public static int findOptimalDivider(int totalWidth, String[] col1Words, String[] col2Words) {
        if (totalWidth <= 0 || col1Words == null || col2Words == null) {
            return -1;
        }

        // Binary search on column 1 width
        // Column 2 width = totalWidth - col1Width
        int left = 1;  // minimum width for column 1
        int right = totalWidth - 1;  // leave at least 1 for column 2
        int bestDivider = left;
        int minHeight = Integer.MAX_VALUE;

        while (left <= right) {
            int col1Width = left + (right - left) / 2;
            int col2Width = totalWidth - col1Width;

            int height = calculateTableHeight(col1Width, col2Width, col1Words, col2Words);

            if (height < minHeight) {
                minHeight = height;
                bestDivider = col1Width;
            }

            // Try to balance the columns
            int col1Height = calculateColumnHeight(col1Width, col1Words);
            int col2Height = calculateColumnHeight(col2Width, col2Words);

            if (col1Height > col2Height) {
                // Column 1 is taller, give it more width
                left = col1Width + 1;
            } else {
                // Column 2 is taller, give column 1 less width
                right = col1Width - 1;
            }
        }

        return bestDivider;
    }

    /**
     * Calculate total table height given column widths
     */
    private static int calculateTableHeight(int col1Width, int col2Width,
                                           String[] col1Words, String[] col2Words) {
        int col1Height = calculateColumnHeight(col1Width, col1Words);
        int col2Height = calculateColumnHeight(col2Width, col2Words);
        return Math.max(col1Height, col2Height);
    }

    /**
     * Calculate number of lines needed for a column given its width and words
     *
     * Corner cases:
     * - If a word is longer than column width, this configuration is INVALID
     *   Return Integer.MAX_VALUE to signal binary search to avoid this position
     * - Words are separated by spaces (width = 1)
     */
    private static int calculateColumnHeight(int width, String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }

        if (width <= 0) {
            return Integer.MAX_VALUE;  // invalid width
        }

        // Check if any word is longer than width - INVALID configuration
        for (String word : words) {
            if (word.length() > width) {
                return Integer.MAX_VALUE;  // Cannot fit this word
            }
        }

        int lines = 1;
        int currentLineWidth = 0;

        for (String word : words) {
            int wordLen = word.length();

            if (currentLineWidth == 0) {
                // First word on the line
                currentLineWidth = wordLen;
            } else {
                // Need to add space + word
                int neededWidth = currentLineWidth + 1 + wordLen;

                if (neededWidth <= width) {
                    // Fits on current line
                    currentLineWidth = neededWidth;
                } else {
                    // Need new line
                    lines++;
                    currentLineWidth = wordLen;
                }
            }
        }

        return lines;
    }

    /**
     * Brute force approach - try all possible divider positions
     * Use this to verify binary search result
     */
    public static int findOptimalDividerBruteForce(int totalWidth,
                                                   String[] col1Words,
                                                   String[] col2Words) {
        int minHeight = Integer.MAX_VALUE;
        int bestDivider = 1;

        for (int col1Width = 1; col1Width < totalWidth; col1Width++) {
            int col2Width = totalWidth - col1Width;
            int height = calculateTableHeight(col1Width, col2Width, col1Words, col2Words);

            if (height < minHeight) {
                minHeight = height;
                bestDivider = col1Width;
            }
        }

        return bestDivider;
    }

    /**
     * Print table visualization
     */
    public static void printTable(int totalWidth, int dividerPos,
                                 String[] col1Words, String[] col2Words) {
        int col1Width = dividerPos;
        int col2Width = totalWidth - dividerPos;

        System.out.println("=".repeat(totalWidth));
        System.out.println("Col1 (width=" + col1Width + ") | Col2 (width=" + col2Width + ")");
        System.out.println("=".repeat(totalWidth));

        // Build lines for each column
        java.util.List<String> col1Lines = buildColumnLines(col1Width, col1Words);
        java.util.List<String> col2Lines = buildColumnLines(col2Width, col2Words);

        int maxLines = Math.max(col1Lines.size(), col2Lines.size());

        for (int i = 0; i < maxLines; i++) {
            String line1 = i < col1Lines.size() ? col1Lines.get(i) : "";
            String line2 = i < col2Lines.size() ? col2Lines.get(i) : "";

            // Pad line1 to col1Width
            line1 = String.format("%-" + col1Width + "s", line1);

            System.out.println(line1 + "|" + line2);
        }

        System.out.println("=".repeat(totalWidth));
        System.out.println("Table height: " + maxLines + " lines");
    }

    private static java.util.List<String> buildColumnLines(int width, String[] words) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (words == null || words.length == 0) {
            return lines;
        }

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else if (currentLine.length() + 1 + word.length() <= width) {
                currentLine.append(" ").append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    // Test cases
    public static void main(String[] args) {
        System.out.println("=== Test 1: Basic case ===");
        int totalWidth1 = 20;
        String[] col1_1 = {"hello", "world", "foo"};
        String[] col2_1 = {"bar", "baz", "test", "example"};

        int optimal1 = findOptimalDivider(totalWidth1, col1_1, col2_1);
        int bruteForce1 = findOptimalDividerBruteForce(totalWidth1, col1_1, col2_1);

        System.out.println("Binary Search result: " + optimal1);
        System.out.println("Brute Force result: " + bruteForce1);
        printTable(totalWidth1, optimal1, col1_1, col2_1);

        System.out.println("\n=== Test 2: Word longer than column width (corner case) ===");
        int totalWidth2 = 15;
        String[] col1_2 = {"verylongword", "hi"};
        String[] col2_2 = {"a", "b", "c"};

        int optimal2 = findOptimalDivider(totalWidth2, col1_2, col2_2);
        System.out.println("Optimal divider: " + optimal2);
        printTable(totalWidth2, optimal2, col1_2, col2_2);

        System.out.println("\n=== Test 3: Unbalanced columns ===");
        int totalWidth3 = 30;
        String[] col1_3 = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] col2_3 = {"short"};

        int optimal3 = findOptimalDivider(totalWidth3, col1_3, col2_3);
        System.out.println("Optimal divider: " + optimal3);
        printTable(totalWidth3, optimal3, col1_3, col2_3);

        System.out.println("\n=== Test 4: Many long words ===");
        int totalWidth4 = 25;
        String[] col1_4 = {"superlongword", "another", "long"};
        String[] col2_4 = {"extremelylongwordhere", "x", "y"};

        int optimal4 = findOptimalDivider(totalWidth4, col1_4, col2_4);
        System.out.println("Optimal divider: " + optimal4);
        printTable(totalWidth4, optimal4, col1_4, col2_4);

        System.out.println("\n=== Test 5: Compare all possible positions ===");
        System.out.println("Width | Col1 Height | Col2 Height | Table Height");
        for (int w = 1; w < totalWidth1; w++) {
            int h1 = calculateColumnHeight(w, col1_1);
            int h2 = calculateColumnHeight(totalWidth1 - w, col2_1);
            System.out.printf("%5d | %11d | %11d | %12d%s\n",
                w, h1, h2, Math.max(h1, h2),
                (w == optimal1 ? " <-- OPTIMAL" : ""));
        }
    }
}
