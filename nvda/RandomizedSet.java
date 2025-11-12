package nvda;

import java.util.*;

public class RandomizedSet {
    private Map<Integer, Integer> valToIndex;
    private List<Integer> values;
    private Random rand;

    public RandomizedSet() {
        valToIndex = new HashMap<>();
        values = new ArrayList<>();
        rand = new Random();
    }

    public boolean insert(int val) {
        if (valToIndex.containsKey(val)) return false;
        valToIndex.put(val, values.size());
        values.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!valToIndex.containsKey(val)) return false;
        int index = valToIndex.get(val);
        int lastVal = values.get(values.size() - 1);
        values.set(index, lastVal);
        valToIndex.put(lastVal, index);
        values.remove(values.size() - 1);
        valToIndex.remove(val);
        return true;
    }

    public int getRandom() {
        return values.get(rand.nextInt(values.size()));
    }

    public static void main(String[] args) {
        RandomizedSet set = new RandomizedSet();

        System.out.println(set.insert(1)); // true
        System.out.println(set.remove(2)); // false
        System.out.println(set.insert(2)); // true
        System.out.println(set.getRandom()); // 1 or 2
        System.out.println(set.remove(1)); // true
        System.out.println(set.insert(2)); // false
        System.out.println(set.getRandom()); // 2
    }
}

