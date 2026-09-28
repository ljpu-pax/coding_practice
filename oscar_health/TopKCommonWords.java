import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Return the top-k most common words in a list of strings.
 * Ties are broken lexicographically (A-Z preferred), matching the typical LeetCode 692 definition.
 */
public class TopKCommonWords {

    public List<String> topKFrequent(List<String> words, int k) {
        if (words == null || k <= 0) {
            return Collections.emptyList();
        }

        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.merge(word, 1, Integer::sum);
        }

        PriorityQueue<String> heap = new PriorityQueue<>((a, b) -> {
            int compareCount = freq.get(a).compareTo(freq.get(b));
            if (compareCount != 0) {
                return compareCount; // min-heap by count
            }
            return b.compareTo(a); // reverse lexicographical for tie (so min-heap drops lexicographically larger word)
        });

        for (String word : freq.keySet()) {
            heap.offer(word);
            if (heap.size() > k) {
                heap.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(0, heap.poll()); // build in reverse since min-heap
        }

        return result;
    }

    public static void main(String[] args) {
        TopKCommonWords solver = new TopKCommonWords();
        List<String> words = Arrays.asList("i", "love", "leetcode", "i", "love", "coding");
        System.out.println(solver.topKFrequent(words, 2)); // [i, love]

        List<String> words2 = Arrays.asList("the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is");
        System.out.println(solver.topKFrequent(words2, 4)); // [the, is, sunny, day]
    }
}
