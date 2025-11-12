package hackerrank;

import java.util.*;

public class canArrangeNumber {
    public static boolean canArrange(int[] arr, int k) {
        Map<Integer, Integer> countMap = new HashMap<>();

        for (int num : arr) {
            int rem = ((num % k) + k) % k;  // 处理负数
            countMap.put(rem, countMap.getOrDefault(rem, 0) + 1);
        }

        for (int rem : countMap.keySet()) {
            int freq = countMap.get(rem);

            if (rem == 0) {
                if (freq % 2 != 0) return false;
            } else if (k % 2 == 0 && rem == k / 2) {
                if (freq % 2 != 0) return false;
            } else {
                int otherFreq = countMap.getOrDefault(k - rem, 0);
                if (freq != otherFreq) return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        int[] arr = {7, 2, 3, 3};
        int k = 3;
        System.out.println(canArrange(arr, k));  // true
    }
}

