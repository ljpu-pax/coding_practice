import java.util.*;

/**
 * Confluent: Pod Load Logs with Global Increments (easy).
 *
 * Process operations on pod loads:
 *   ["add", x] - insert a load x
 *   ["inc", d] - add d to EVERY load currently stored (d may be negative)
 *   ["pop"]    - remove and output the smallest current load; empty -> None (null)
 * Return the outputs of all pops.
 *
 * Example 1: add 5, add 2, inc 3, pop, add 1, pop, pop -> [5, 1, 8]
 * Example 2: pop, add 4, inc -1, pop                    -> [None, 3]
 *
 * Brute force: inc loops over every element -> O(n) per inc.
 *
 * Optimal: min-heap + one global offset.
 *   Actual load = storedValue + offset.
 *   add x -> push (x - offset)      O(log n)
 *   inc d -> offset += d            O(1)   (same shift for all, heap order unchanged)
 *   pop   -> poll() + offset        O(log n)
 * The trick: storing x - offset means a load added AFTER an inc does not
 * receive that earlier increment.
 *
 * Use long to avoid overflow after many increments.
 */
public class PodLoadLogs {

    public static List<Long> process(Object[][] ops) {
        PriorityQueue<Long> heap = new PriorityQueue<>();
        long offset = 0;
        List<Long> out = new ArrayList<>();
        for (Object[] op : ops) {
            String type = (String) op[0];
            switch (type) {
                case "add":
                    heap.add(((Number) op[1]).longValue() - offset);
                    break;
                case "inc":
                    offset += ((Number) op[1]).longValue();
                    break;
                case "pop":
                    out.add(heap.isEmpty() ? null : heap.poll() + offset);
                    break;
                default:
                    throw new IllegalArgumentException("unknown op " + type);
            }
        }
        return out;
    }

    public static void main(String[] args) {
        Object[][] ex1 = {{"add", 5}, {"add", 2}, {"inc", 3}, {"pop"}, {"add", 1}, {"pop"}, {"pop"}};
        Object[][] ex2 = {{"pop"}, {"add", 4}, {"inc", -1}, {"pop"}};
        Object[][] ex3 = {{"add", 10}, {"inc", 5}, {"add", 12}, {"inc", -20}, {"pop"}, {"pop"}, {"pop"}};

        System.out.println(process(ex1)); // [5, 1, 8]
        System.out.println(process(ex2)); // [null, 3]
        System.out.println(process(ex3)); // [-8, -5, null]  (12-20=-8, 10+5-20=-5)
    }
}
