import java.util.*;

public class TalentWindowFinder {

    public static int[] minWindowLengths(int[] v, int talentCount) {
        int n = v.length;
        int[] result = new int[n];
        Arrays.fill(result, -1); // Default to -1 if no valid window is found

        Map<Integer, Integer> freq = new HashMap<>();
        int unique = 0;  // Count of unique talents in current window
        int j = 0;

        for (int i = 0; i < n; i++) {
            // Shrink window from left if needed
            if (i > 0) {
                int leftVal = v[i - 1];
                freq.put(leftVal, freq.get(leftVal) - 1);
                if (freq.get(leftVal) == 0) {
                    freq.remove(leftVal);
                    unique--;
                }
            }

            // Expand window from right to include all talents
            while (j < n && unique < talentCount) {
                int val = v[j];
                if (!freq.containsKey(val)) unique++;
                freq.put(val, freq.getOrDefault(val, 0) + 1);
                j++;
            }

            // If we found a valid window
            if (unique == talentCount) {
                result[i] = j - i;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] v = {1, 2, 1, 3, 4};
        int talentCount = 3;

        int[] res = minWindowLengths(v, talentCount);
        System.out.println("Result: " + Arrays.toString(res));
    }
}

