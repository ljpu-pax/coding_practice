package robinhood;

import java.util.*;

public class BrightestPosition {
    public int brightestPosition(int[][] lights) {
        TreeMap<Integer, Integer> diffMap = new TreeMap<>();

        // Step 1: Apply difference array idea
        for (int[] light : lights) {
            int start = light[0] - light[1];
            int end = light[0] + light[1];
            diffMap.put(start, diffMap.getOrDefault(start, 0) + 1);
            diffMap.put(end + 1, diffMap.getOrDefault(end + 1, 0) - 1);
        }

        // Step 2: Traverse in order and compute prefix sum
        int maxBrightness = 0;
        int currBrightness = 0;
        int bestPosition = 0;

        for (Map.Entry<Integer, Integer> entry : diffMap.entrySet()) {
            int position = entry.getKey();
            int delta = entry.getValue();

            currBrightness += delta;

            if (currBrightness > maxBrightness) {
                maxBrightness = currBrightness;
                bestPosition = position;
            }
        }

        return bestPosition;
    }

    public static void main(String[] args) {
        BrightestPosition s = new BrightestPosition();
        int[][] lights = {{1, 2}, {2, 1}, {3, 1}};
        System.out.println(s.brightestPosition(lights)); // Output: 2
    }
}

