// LeetCode 380: Insert Delete GetRandom O(1)
// https://leetcode.com/problems/insert-delete-getrandom-o1/
// Difficulty: Medium

// Implement the RandomizedSet class:
// - RandomizedSet() Initializes the RandomizedSet object.
// - bool insert(int val) Inserts an item val into the set if not present.
//   Returns true if the item was not present, false otherwise.
// - bool remove(int val) Removes an item val from the set if present.
//   Returns true if the item was present, false otherwise.
// - int getRandom() Returns a random element from the current set of elements
//   (it's guaranteed that at least one element exists when this method is called).
//   Each element must have the same probability of being returned.

// You must implement the functions of the class such that each function works
// in average O(1) time complexity.

// Example 1:
// Input
// ["RandomizedSet", "insert", "remove", "insert", "getRandom", "remove", "insert", "getRandom"]
// [[], [1], [2], [2], [], [1], [2], []]
// Output
// [null, true, false, true, 2, true, false, 2]

import java.util.*;

class RandomizedSet {
    // HashMap to store value -> index mapping
    private Map<Integer, Integer> map;
    // ArrayList to store actual values for random access
    private List<Integer> list;
    // Random number generator
    private Random random;

    public RandomizedSet() {
        map = new HashMap<>();
        list = new ArrayList<>();
        random = new Random();
    }

    // Insert a value to the set. Returns true if the set did not already contain the specified element.
    // Time: O(1) average
    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }

        // Add value to list and store its index in map
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    // Removes a value from the set. Returns true if the set contained the specified element.
    // Time: O(1) average
    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }

        // Get the index of element to remove
        int indexToRemove = map.get(val);
        int lastElement = list.get(list.size() - 1);

        // Move the last element to the position of element to remove
        list.set(indexToRemove, lastElement);
        map.put(lastElement, indexToRemove);

        // Remove the last element from list and map
        list.remove(list.size() - 1);
        map.remove(val);

        return true;
    }

    // Get a random element from the set.
    // Time: O(1)
    public int getRandom() {
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static void main(String[] args) {
        RandomizedSet randomizedSet = new RandomizedSet();

        // Test insert
        System.out.println(randomizedSet.insert(1));  // true
        System.out.println(randomizedSet.insert(2));  // true
        System.out.println(randomizedSet.insert(2));  // false (already exists)

        // Test getRandom
        System.out.println("Random element: " + randomizedSet.getRandom()); // 1 or 2

        // Test remove
        System.out.println(randomizedSet.remove(1));  // true
        System.out.println(randomizedSet.remove(1));  // false (already removed)

        // Insert more elements
        randomizedSet.insert(3);
        randomizedSet.insert(4);
        randomizedSet.insert(5);

        // Test getRandom multiple times
        System.out.println("Random elements:");
        for (int i = 0; i < 5; i++) {
            System.out.print(randomizedSet.getRandom() + " ");
        }
        System.out.println();

        // Test comprehensive scenario
        System.out.println("\nComprehensive test:");
        RandomizedSet set = new RandomizedSet();
        System.out.println("insert(0): " + set.insert(0));     // true
        System.out.println("insert(1): " + set.insert(1));     // true
        System.out.println("remove(0): " + set.remove(0));     // true
        System.out.println("insert(2): " + set.insert(2));     // true
        System.out.println("remove(1): " + set.remove(1));     // true
        System.out.println("getRandom(): " + set.getRandom()); // 2
    }
}

/*
 * Key Insights:
 *
 * 1. HashMap provides O(1) lookup for insert/remove operations
 * 2. ArrayList provides O(1) random access by index
 * 3. To remove in O(1), we swap the element to remove with the last element,
 *    then remove the last element (which is O(1) for ArrayList)
 *
 * Time Complexity:
 * - insert(): O(1) average
 * - remove(): O(1) average
 * - getRandom(): O(1)
 *
 * Space Complexity: O(n) where n is the number of elements in the set
 */
