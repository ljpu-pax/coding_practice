import java.util.*;

public class SecondLargestPermutation {
    public static int[] secondLargest(int[] digits) {
        if (digits == null || digits.length == 0) return null;

        Arrays.sort(digits);

        reverse(digits, 0, digits.length - 1);

        findSecondLargest(digits);

        return digits;
    }

    private static void reverse(int[] digits, int left, int right) {

        while (left < right) {
            swap(digits, left++, right--);
        }
    }

    private static void swap(int[] digits, int left, int right) {
        int tmp = digits[left];
        digits[left] = digits[right];
        digits[right] = tmp;
    }

    private static void findSecondLargest(int[] digits) {
        int i = digits.length - 2;
        
        while (i >= 0 && digits[i] <= digits[i + 1]) {
            i--;
        }

        if (i >= 0) {
            int j = digits.length - 1;

            while (digits[j] >= digits[i]) {
                j--;
            }
            swap(digits, i, j);
        }

        reverse(digits, i + 1, digits.length - 1);

    }

    // Demo and test
    public static void main(String[] args) {
        int[] input = {2, 5, 3, 1, 1};
        int[] result = secondLargest(input);

        System.out.println("Second largest permutation: " + Arrays.toString(result));
        // Output should be: [5, 3, 1, 2, 1]
    }
}
