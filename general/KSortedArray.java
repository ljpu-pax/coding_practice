import java.util.*;

public class KSortedArray {

    public List<Integer> mergeSortedArrays(int[] A, int[] B, int[] C) {
        int i = 0, j = 0, k = 0;
        List<Integer> result = new ArrayList<>();

        while (i < A.length || j < B.length || k < C.length) {
            int min = Integer.MAX_VALUE;

            if (i < A.length) min = Math.min(min, A[i]);
            if (j < B.length) min = Math.min(min, B[j]);
            if (k < C.length) min = Math.min(min, C[k]);

            // Add only if it's not a duplicate
            if (result.isEmpty() || result.get(result.size() - 1) != min) {
                result.add(min);
            }

            if (i < A.length && A[i] == min) i++;
            if (j < B.length && B[j] == min) j++;
            if (k < C.length && C[k] == min) k++;
        }

        return result;
    }

    // Test cases
    public static void main(String[] args) {
        KSortedArray sol = new KSortedArray();

        // Test 1: Example from the prompt
        System.out.println(sol.mergeSortedArrays(
                new int[]{-1, -1, 0, 1},
                new int[]{-2, 3, 5},
                new int[]{-1, 0, 5}
        )); // [-2, -1, 0, 1, 3, 5]

        // Test 2: All arrays empty
        System.out.println(sol.mergeSortedArrays(
                new int[]{},
                new int[]{},
                new int[]{}
        )); // []

        // Test 3: No duplicates across arrays
        System.out.println(sol.mergeSortedArrays(
                new int[]{1, 4, 7},
                new int[]{2, 5, 8},
                new int[]{3, 6, 9}
        )); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

        // Test 4: All arrays have same values
        System.out.println(sol.mergeSortedArrays(
                new int[]{1, 2, 3},
                new int[]{1, 2, 3},
                new int[]{1, 2, 3}
        )); // [1, 2, 3]

        // Test 5: One empty, two with content and overlap
        System.out.println(sol.mergeSortedArrays(
                new int[]{},
                new int[]{1, 3, 5},
                new int[]{3, 4, 6}
        )); // [1, 3, 4, 5, 6]

        // Test 6: Large range with negative numbers
        System.out.println(sol.mergeSortedArrays(
                new int[]{-1000, -10, 0},
                new int[]{-1000, 5, 6},
                new int[]{-999, -10, 0, 7}
        )); // [-1000, -999, -10, 0, 5, 6, 7]
    }
}
