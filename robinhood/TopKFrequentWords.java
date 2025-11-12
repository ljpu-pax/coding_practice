package robinhood;

import java.util.*;

public class TopKFrequentWords {
    public List<String> topKFrequent(String[] words, int k) {
        // Step 1: Build frequency map
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        // Step 2: Build min-heap with custom comparator
        PriorityQueue<String> heap = new PriorityQueue<>((w1, w2) -> {
            int freqCompare = freqMap.get(w1) - freqMap.get(w2);
            if (freqCompare == 0) {
                return w2.compareTo(w1); // reverse lex order
            }
            return freqCompare;
        });

        // Step 3: Maintain top k elements in heap
        for (String word : freqMap.keySet()) {
            heap.offer(word);
            if (heap.size() > k) {
                heap.poll(); // pop smallest
            }
        }

        // Step 4: Pop from heap into result (reverse order)
        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.poll());
        }
        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        TopKFrequentWords solution = new TopKFrequentWords();
        String[] words = {"i", "love", "leetcode", "i", "love", "coding"};
        int k = 2;
        System.out.println(solution.topKFrequent(words, k)); // Output: ["i", "love"]
    }
}

