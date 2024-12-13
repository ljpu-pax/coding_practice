public class SupermarketAisle {
    public static int countArrangements(int[] foodCounts, char[] aisle) {
        return backtrack(foodCounts, 0, aisle, 0);
    }

    private static boolean isValidPlacement(char[] aisle, int start, int foodCount) {
        int foodPlaced = 0;
        for (int i = start; i < start + foodCount; i++) {
            if (i >= aisle.length || aisle[i] == 'W') return false; // Cannot place food on water or out of bounds
            if (aisle[i] == 'F') foodPlaced++; // Pre-existing food counts toward the group
        }
        return foodPlaced <= foodCount; // Ensure placement does not exceed required food count
    }

    private static void placeFood(char[] aisle, int start, int foodCount) {
        for (int i = start; i < start + foodCount; i++) {
            if (aisle[i] == '?') aisle[i] = 'F'; // Place food in empty spots
        }
    }

    private static void removeFood(char[] aisle, int start, int foodCount) {
        for (int i = start; i < start + foodCount; i++) {
            if (aisle[i] == 'F' && (start + foodCount > aisle.length || aisle[i] == '?')) {
                aisle[i] = '?'; // Reset food placement
            }
        }
    }

    private static int backtrack(int[] foodCounts, int foodIndex, char[] aisle, int position) {
        if (foodIndex == foodCounts.length) {
            return 1; // Successfully placed all food groups
        }

        int totalWays = 0;
        int foodCount = foodCounts[foodIndex];

        // Try placing the current food group at all valid positions
        for (int i = position; i <= aisle.length - foodCount; i++) {
            if (isValidPlacement(aisle, i, foodCount)) {
                placeFood(aisle, i, foodCount);
                totalWays += backtrack(foodCounts, foodIndex + 1, aisle, i + foodCount);
                removeFood(aisle, i, foodCount);
            }
        }

        return totalWays;
    }

    public static void main(String[] args) {
        // Example 1
        int[] foodCounts1 = {1, 2};
        char[] aisle1 = {'?', 'F', '?', 'W', 'W', '?', '?'};
        System.out.println("Possible arrangements: " + countArrangements(foodCounts1, aisle1)); // Output: 1

        // Example 2
        int[] foodCounts2 = {1, 1};
        char[] aisle2 = {'?', 'F', '?', 'W', 'W', '?', '?'};
        System.out.println("Possible arrangements: " + countArrangements(foodCounts2, aisle2)); // Output: 2
    }
}
