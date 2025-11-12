package nvda;

import java.util.*;

public class SmallestDivisor {

    public static int smallestDivisor(int[] nums, int threshold) {
        int left = 1, right = Arrays.stream(nums).max().orElse(1);
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (divisionSum(nums, mid) <= threshold) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private static int divisionSum(int[] nums, int divisor) {
        int sum = 0;
        for (int num : nums) {
            sum += (num + divisor - 1) / divisor; // ceiling division
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println(smallestDivisor(new int[]{1,2,5,9}, 6)); // 5
        System.out.println(smallestDivisor(new int[]{44,22,33,11,1}, 5)); // 44
    }
}
