package robinhood;

import java.util.*;

public class CountPairs {
    public int countPairs(int[] deliciousness) {
        Map<Integer, Integer> countMap = new HashMap<>();
        int mod = 1_000_000_007;
        int maxVal = 0;
        for (int x : deliciousness) maxVal = Math.max(maxVal, x);
        int maxSum = maxVal * 2;

        int result = 0;
        for (int x : deliciousness) {
            for (int power = 1; power <= maxSum; power <<= 1) {
                int complement = power - x;
                result = (result + countMap.getOrDefault(complement, 0)) % mod;
            }
            countMap.put(x, countMap.getOrDefault(x, 0) + 1);
        }
        return result;
    }

    public static void main(String[] args) {
        CountPairs s = new CountPairs();
        int[] nums = {1, 3, 5, 7, 9};
        System.out.println(s.countPairs(nums)); // Output: 4
    }
}

