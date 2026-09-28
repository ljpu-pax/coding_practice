package openai;

import java.util.*;

/**
 * LeetCode 735 - Asteroid Collision
 *
 * Problem:
 * We are given an array asteroids of integers representing asteroids in a row.
 *
 * For each asteroid, the absolute value represents its size, and the sign represents its direction
 * (positive meaning right, negative meaning left). Each asteroid moves at the same speed.
 *
 * Find out the state of the asteroids after all collisions. If two asteroids meet, the smaller one
 * will explode. If both are the same size, both will explode. Two asteroids moving in the same
 * direction will never meet.
 *
 * Example 1:
 * Input: asteroids = [5, 10, -5]
 * Output: [5, 10]
 * Explanation: The 10 and -5 collide resulting in 10. The 5 and 10 never collide.
 *
 * Example 2:
 * Input: asteroids = [8, -8]
 * Output: []
 * Explanation: The 8 and -8 collide exploding each other.
 *
 * Example 3:
 * Input: asteroids = [10, 2, -5]
 * Output: [10]
 * Explanation: The 2 and -5 collide resulting in -5. The 10 and -5 collide resulting in 10.
 *
 * Example 4:
 * Input: asteroids = [-2, -1, 1, 2]
 * Output: [-2, -1, 1, 2]
 * Explanation: All asteroids moving left, then all moving right. No collisions.
 *
 * Constraints:
 * - 2 <= asteroids.length <= 10^4
 * - -1000 <= asteroids[i] <= 1000
 * - asteroids[i] != 0
 *
 * Solution Approach:
 * Use a stack to simulate the collision process:
 * 1. Iterate through each asteroid
 * 2. If asteroid is moving right (positive), push to stack
 * 3. If asteroid is moving left (negative):
 *    - While there are right-moving asteroids on stack that will collide:
 *      - Compare sizes and handle collision
 *      - If left-moving asteroid survives, continue checking
 *    - If left-moving asteroid survives all collisions, push to stack
 * 4. Convert stack to array
 *
 * Key insight: Collisions only happen when a right-moving asteroid (positive) meets a
 * left-moving asteroid (negative) that comes after it.
 *
 * Time Complexity: O(n) - each asteroid is pushed/popped at most once
 * Space Complexity: O(n) - stack can contain all asteroids in worst case
 */
public class AsteroidCollision {

    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();

        for (int asteroid : asteroids) {
            boolean alive = true;

            // Process collisions when current asteroid is moving left (negative)
            // and there are right-moving asteroids (positive) on the stack
            while (alive && asteroid < 0 && !stack.isEmpty() && stack.peek() > 0) {
                int top = stack.peek();

                // Compare absolute values
                if (Math.abs(asteroid) > top) {
                    // Left-moving asteroid destroys right-moving one
                    stack.pop();
                    // Continue to check for more collisions
                } else if (Math.abs(asteroid) == top) {
                    // Both asteroids destroy each other
                    stack.pop();
                    alive = false;
                } else {
                    // Right-moving asteroid is larger, current asteroid is destroyed
                    alive = false;
                }
            }

            // If asteroid survived all collisions, add to stack
            if (alive) {
                stack.push(asteroid);
            }
        }

        // Convert stack to array
        int[] result = new int[stack.size()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = stack.pop();
        }

        return result;
    }

    /**
     * Alternative solution using ArrayList for cleaner conversion
     */
    public int[] asteroidCollisionV2(int[] asteroids) {
        List<Integer> stack = new ArrayList<>();

        for (int asteroid : asteroids) {
            boolean alive = true;

            while (alive && asteroid < 0 && !stack.isEmpty()
                   && stack.get(stack.size() - 1) > 0) {
                int top = stack.get(stack.size() - 1);

                if (Math.abs(asteroid) > top) {
                    stack.remove(stack.size() - 1);
                } else if (Math.abs(asteroid) == top) {
                    stack.remove(stack.size() - 1);
                    alive = false;
                } else {
                    alive = false;
                }
            }

            if (alive) {
                stack.add(asteroid);
            }
        }

        return stack.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Test the solution
     */
    public static void main(String[] args) {
        AsteroidCollision solution = new AsteroidCollision();

        // Test case 1
        int[] asteroids1 = {5, 10, -5};
        System.out.println("Input: " + Arrays.toString(asteroids1));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids1)));
        System.out.println("Expected: [5, 10]\n");

        // Test case 2
        int[] asteroids2 = {8, -8};
        System.out.println("Input: " + Arrays.toString(asteroids2));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids2)));
        System.out.println("Expected: []\n");

        // Test case 3
        int[] asteroids3 = {10, 2, -5};
        System.out.println("Input: " + Arrays.toString(asteroids3));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids3)));
        System.out.println("Expected: [10]\n");

        // Test case 4: No collisions
        int[] asteroids4 = {-2, -1, 1, 2};
        System.out.println("Input: " + Arrays.toString(asteroids4));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids4)));
        System.out.println("Expected: [-2, -1, 1, 2]\n");

        // Test case 5: All same direction
        int[] asteroids5 = {1, 2, 3, 4};
        System.out.println("Input: " + Arrays.toString(asteroids5));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids5)));
        System.out.println("Expected: [1, 2, 3, 4]\n");

        // Test case 6: Left-moving destroys multiple right-moving
        int[] asteroids6 = {5, 4, 3, -10};
        System.out.println("Input: " + Arrays.toString(asteroids6));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids6)));
        System.out.println("Expected: [-10]\n");

        // Test case 7: Complex scenario
        int[] asteroids7 = {-2, -2, 1, -2};
        System.out.println("Input: " + Arrays.toString(asteroids7));
        System.out.println("Output: " + Arrays.toString(solution.asteroidCollision(asteroids7)));
        System.out.println("Expected: [-2, -2, -2]\n");
    }
}
