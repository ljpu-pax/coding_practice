package nvda;

import java.util.*;

public class TopKFrequentElements {

    public static int[] topKFrequent(int[] nums, int k) {
        // 1. 统计频率
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // 2. 构建最小堆，堆中存 k 个频率最高的元素
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();  // 只保留前 k 个
            }
        }

        // 3. 构建结果数组
        int[] result = new int[k];
        int i = 0;
        while (!minHeap.isEmpty()) {
            result[i++] = minHeap.poll().getKey();
        }

        return result;
    }

    // ✅ 测试
    public static void main(String[] args) {
        test(new int[]{1, 1, 1, 2, 2, 3}, 2);
        test(new int[]{1}, 1);
        test(new int[]{4,1,-1,2,-1,2,3}, 2);
    }

    private static void test(int[] nums, int k) {
        int[] result = topKFrequent(nums, k);
        System.out.println("Top " + k + " frequent elements in " + Arrays.toString(nums) + ": " + Arrays.toString(result));
    }
}

