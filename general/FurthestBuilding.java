import java.util.*;

public class FurthestBuilding {
    public int furthestBuilding(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        List<String> actions = new ArrayList<>();

        for (int i = 0; i < heights.length - 1; i++) {
            int climb = heights[i + 1] - heights[i];

            if (climb <= 0) {
                actions.add("Step " + i + " -> " + (i + 1) + ": No need");
                continue;
            }

            minHeap.add(climb);

            if (minHeap.size() > ladders) {
                int brickUse = minHeap.poll();
                bricks -= brickUse;
                actions.add("Step " + i + " -> " + (i + 1) + ": Used bricks for climb of " + climb);
            } else {
                actions.add("Step " + i + " -> " + (i + 1) + ": Used ladder for climb of " + climb);
            }

            if (bricks < 0) {
                System.out.println("Reached building: " + i);
                for (int j = 0; j < actions.size(); j++) {
                    System.out.println(actions.get(j));
                }
                return i;
            }
        }

        // If reached end
        System.out.println("Reached building: " + (heights.length - 1));
        for (String action : actions) {
            System.out.println(action);
        }

        return heights.length - 1;
    }

    // For testing
    public static void main(String[] args) {
        FurthestBuilding sol = new FurthestBuilding();
        int[] heights = {4,2,7,6,9,14,12};
        int bricks = 5, ladders = 1;
        sol.furthestBuilding(heights, bricks, ladders);
    }
}

