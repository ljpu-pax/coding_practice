// LeetCode 381: Insert Delete GetRandom O(1) - Duplicates allowed
// https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/
// Difficulty: Hard

// RandomizedCollection is a data structure that contains a collection of numbers,
// possibly duplicates (i.e., a multiset). It should support inserting and removing
// specific elements and also reporting a random element.

// Implement the RandomizedCollection class:
// - RandomizedCollection() Initializes the RandomizedCollection object.
// - bool insert(int val) Inserts an item val into the multiset, even if the item is
//   already present. Returns true if the item is not present, false otherwise.
// - bool remove(int val) Removes an item val from the multiset if present. Returns true
//   if the item is present, false otherwise. Note that if val has multiple occurrences
//   in the multiset, we only remove one of them.
// - int getRandom() Returns a random element from the current multiset of elements
//   (it's guaranteed that at least one element exists when this method is called).
//   The probability of each element being returned is linearly related to the number
//   of the same values the multiset contains.

// You must implement the functions of the class such that each function works in
// average O(1) time complexity.

// Example 1:
// Input
// ["RandomizedCollection", "insert", "insert", "insert", "getRandom", "remove", "getRandom"]
// [[], [1], [1], [2], [], [1], []]
// Output
// [null, true, false, true, 2, true, 1]

// Explanation
// RandomizedCollection randomizedCollection = new RandomizedCollection();
// randomizedCollection.insert(1);   // return true. Collection: [1]
// randomizedCollection.insert(1);   // return false. Collection: [1, 1]
// randomizedCollection.insert(2);   // return true. Collection: [1, 1, 2]
// randomizedCollection.getRandom(); // getRandom should:
//                                   // - return 2 with probability 1/3,
//                                   // - return 1 with probability 2/3.
// randomizedCollection.remove(1);   // return true. Collection: [1, 2]
// randomizedCollection.getRandom(); // getRandom should return 1 and 2 with equal probability.

import java.util.*;

class RandomizedCollection {
    // Map: value -> set of indices in the list
    private Map<Integer, Set<Integer>> valueToIndices;
    // List to store actual values for random access
    private List<Integer> values;
    // Random number generator
    private Random random;

    public RandomizedCollection() {
        valueToIndices = new HashMap<>();
        values = new ArrayList<>();
        random = new Random();
    }

    // Inserts a value to the collection. Returns true if the collection did not already contain the specified element.
    // Time: O(1) average
    public boolean insert(int val) {
        // Check if value already exists
        boolean notPresent = !valueToIndices.containsKey(val);

        // Add to map
        valueToIndices.putIfAbsent(val, new HashSet<>());
        valueToIndices.get(val).add(values.size());

        // Add to list
        values.add(val);

        return notPresent;
    }

    // Removes a value from the collection. Returns true if the collection contained the specified element.
    // Time: O(1) average
    public boolean remove(int val) {
        if (!valueToIndices.containsKey(val) || valueToIndices.get(val).isEmpty()) {
            return false;
        }

        // Get an index to remove (any index from the set)
        Set<Integer> indices = valueToIndices.get(val);
        int indexToRemove = indices.iterator().next();

        // Remove this index from the set
        indices.remove(indexToRemove);

        // Get the last element in the list
        int lastIndex = values.size() - 1;
        int lastValue = values.get(lastIndex);

        // Move the last element to the position of element to remove
        values.set(indexToRemove, lastValue);

        // Update the index mapping for the last element
        Set<Integer> lastValueIndices = valueToIndices.get(lastValue);
        lastValueIndices.remove(lastIndex);

        // Only add new index if we're not removing the last element itself
        if (indexToRemove != lastIndex) {
            lastValueIndices.add(indexToRemove);
        }

        // Remove the last element from list
        values.remove(lastIndex);

        // Clean up empty sets
        if (indices.isEmpty()) {
            valueToIndices.remove(val);
        }

        return true;
    }

    // Get a random element from the collection.
    // Time: O(1)
    public int getRandom() {
        int randomIndex = random.nextInt(values.size());
        return values.get(randomIndex);
    }

    public static void main(String[] args) {
        RandomizedCollection collection = new RandomizedCollection();

        System.out.println("=== Test Case 1 ===");
        System.out.println("insert(1): " + collection.insert(1));   // true
        System.out.println("insert(1): " + collection.insert(1));   // false (duplicate)
        System.out.println("insert(2): " + collection.insert(2));   // true

        System.out.println("\nCollection: " + collection.values);

        // Test getRandom
        System.out.println("\nTesting getRandom (10 times):");
        Map<Integer, Integer> frequency = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            int random = collection.getRandom();
            frequency.put(random, frequency.getOrDefault(random, 0) + 1);
        }
        System.out.println("Frequency: " + frequency);

        System.out.println("\nremove(1): " + collection.remove(1));  // true
        System.out.println("Collection after remove: " + collection.values);

        System.out.println("\n=== Test Case 2 ===");
        RandomizedCollection collection2 = new RandomizedCollection();
        System.out.println("insert(1): " + collection2.insert(1));
        System.out.println("insert(1): " + collection2.insert(1));
        System.out.println("insert(2): " + collection2.insert(2));
        System.out.println("insert(1): " + collection2.insert(1));
        System.out.println("insert(2): " + collection2.insert(2));
        System.out.println("insert(2): " + collection2.insert(2));

        System.out.println("\nCollection: " + collection2.values);
        System.out.println("Value to indices map: " + collection2.valueToIndices);

        System.out.println("\nremove(1): " + collection2.remove(1));
        System.out.println("remove(2): " + collection2.remove(2));
        System.out.println("remove(2): " + collection2.remove(2));
        System.out.println("remove(2): " + collection2.remove(2));

        System.out.println("\nCollection after removes: " + collection2.values);
        System.out.println("Value to indices map: " + collection2.valueToIndices);

        System.out.println("\n=== Test Case 3: Edge Cases ===");
        RandomizedCollection collection3 = new RandomizedCollection();
        collection3.insert(10);
        collection3.insert(10);
        collection3.insert(20);
        collection3.insert(20);
        collection3.insert(30);
        System.out.println("Collection: " + collection3.values);

        collection3.remove(10);
        System.out.println("After remove(10): " + collection3.values);

        collection3.remove(20);
        System.out.println("After remove(20): " + collection3.values);

        collection3.remove(20);
        System.out.println("After remove(20): " + collection3.values);

        collection3.remove(10);
        System.out.println("After remove(10): " + collection3.values);

        System.out.println("getRandom: " + collection3.getRandom());
    }
}

/*
 * Key Insights:
 *
 * Differences from LeetCode 380 (No Duplicates):
 * ==============================================
 * - LeetCode 380: Map<Integer, Integer> (value -> single index)
 * - LeetCode 381: Map<Integer, Set<Integer>> (value -> set of indices)
 *
 * Why Set<Integer> for Indices?
 * ==============================
 * - Multiple occurrences of same value need multiple index tracking
 * - Set allows O(1) add/remove of specific indices
 * - Can use LinkedHashSet for consistent ordering (optional)
 *
 * Remove Operation Details:
 * =========================
 * 1. Get any index of the value to remove (use iterator().next())
 * 2. Remove that index from the set
 * 3. Get the last element in the list
 * 4. Swap last element with element to remove (copy last to removed position)
 * 5. Update index mapping for last element:
 *    - Remove last index from its set
 *    - Add new index to its set (if not removing last element itself)
 * 6. Remove last element from list
 * 7. Clean up empty sets in map
 *
 * Tricky Case: Removing Last Element
 * ===================================
 * When indexToRemove == lastIndex:
 * - We're removing the last element
 * - Don't add indexToRemove to lastValueIndices (would be invalid)
 * - Just remove from list and update map
 *
 * Example Walkthrough:
 * ====================
 * insert(1) -> values=[1], map={1->{0}}
 * insert(1) -> values=[1,1], map={1->{0,1}}
 * insert(2) -> values=[1,1,2], map={1->{0,1}, 2->{2}}
 *
 * remove(1):
 * - Choose index 0 (or 1, doesn't matter)
 * - Last element is 2 at index 2
 * - Move 2 to index 0: values=[2,1,2]
 * - Update map: 2->{0,2}, 1->{1}
 * - Remove last: values=[2,1], map={2->{0}, 1->{1}}
 *
 * getRandom():
 * - Generate random index in [0, values.size())
 * - Return values[randomIndex]
 * - Probability proportional to frequency (automatic due to duplicates in list)
 *
 * Time Complexity:
 * ================
 * - insert(): O(1) average (HashSet add is O(1) average)
 * - remove(): O(1) average (HashSet operations are O(1) average)
 * - getRandom(): O(1)
 *
 * Space Complexity: O(n) where n is the number of elements (including duplicates)
 *
 * Why HashSet instead of ArrayList for indices?
 * ==============================================
 * - Need O(1) removal of specific index
 * - ArrayList removal is O(n) in worst case
 * - HashSet provides O(1) average add/remove
 * - Order doesn't matter for our use case
 *
 * Alternative: LinkedHashSet
 * ==========================
 * - Maintains insertion order
 * - Can remove first/last element efficiently
 * - Slightly more memory overhead
 * - Use if consistent removal order is desired
 *
 * Interview Tips:
 * ===============
 * 1. Explain the difference from problem 380 (duplicates allowed)
 * 2. Start with the data structures: Map + List
 * 3. Walk through the remove operation step by step
 * 4. Highlight the edge case of removing last element
 * 5. Discuss why Set<Integer> is needed instead of single Integer
 * 6. Explain how getRandom maintains correct probability
 * 7. Mention that duplicates in list automatically give correct probabilities
 *
 * Common Mistakes:
 * ================
 * 1. Forgetting to update indices when swapping with last element
 * 2. Not handling the case when removing the last element itself
 * 3. Not cleaning up empty sets from the map
 * 4. Using List<Integer> instead of Set<Integer> for indices (O(n) remove)
 * 5. Forgetting to remove old index before adding new index for last element
 */
