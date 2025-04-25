public class SupermarketAisle {

    public static int countArrangements(int[] foodCounts, char[] aisle) {
        return backtrack(foodCounts, 0, aisle, 0);
    }

    private static int backtrack(int[] foodCounts, int foodIndex, char[] aisle, int position) {
        // Base case: all groups have been placed
        if (foodIndex == foodCounts.length) {
            return 1; // Valid arrangement
        }

        int totalWays = 0;
        int foodCount = foodCounts[foodIndex];

        // Try placing the current food group
        for (int i = position; i <= aisle.length - foodCount; i++) {
            if (canPlace(aisle, i, foodCount)) {
                placeFood(aisle, i, foodCount);
                totalWays += backtrack(foodCounts, foodIndex + 1, aisle, i + foodCount);
                removeFood(aisle, i, foodCount);
            }
        }

        return totalWays;
    }

    private static boolean canPlace(char[] aisle, int start, int foodCount) {
        int foodRequired = foodCount;
        for (int i = start; i < start + foodCount; i++) {
            if (i >= aisle.length || aisle[i] == 'W') return false; // Cannot place food over water
            if (aisle[i] == 'F') foodRequired--; // Pre-existing food satisfies part of the requirement
        }
        return foodRequired >= 0; // Ensure the group can be placed
    }

    private static void placeFood(char[] aisle, int start, int foodCount) {
        for (int i = start; i < start + foodCount; i++) {
            if (aisle[i] == '?') aisle[i] = 'F'; // Mark placement
        }
    }

    private static void removeFood(char[] aisle, int start, int foodCount) {
        for (int i = start; i < start + foodCount; i++) {
            if (aisle[i] == 'F') aisle[i] = '?'; // Reset placement
        }
    }

    public static void main(String[] args) {
        // Example 1
        int[] foodCounts1 = {1, 2};
        char[] aisle1 = {'?', 'F', '?', 'W', 'W', '?', '?'};
        System.out.println("Possible arrangements: " + countArrangements(foodCounts1, aisle1)); // Expected: 1

        // Example 2
        int[] foodCounts2 = {1, 1};
        char[] aisle2 = {'?', 'F', '?', 'W', 'W', '?', '?'};
        System.out.println("Possible arrangements: " + countArrangements(foodCounts2, aisle2)); // Expected: 2
    }
}
