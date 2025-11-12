package anthropic;

import java.util.*;

/**
 * ===============================================
 * 分布式算法题目 & 解法总结
 * ===============================================
 *
 * 题目背景：
 * - 有 10 台机器组成的分布式集群。
 * - 数据传输接口已经写好，不能改。
 * - 系统瓶颈在于「单点接收流量」，所以要避免所有机器同时把数据发到同一台机器。
 *
 * 问题 1：找重复数
 * - 数据集中，除了两个相同的数，其余都唯一。
 * - 要找到这两个相同的数。
 * - 解法：每台机器本地先对数据取 mod(10)，然后发到对应机器。
 *   相同的两个数一定落在同一台机器上，在本地用 HashSet 就能检测重复。
 *
 * 问题 2：找众数 (Mode)
 * - 找出全局出现次数最多的数。
 * - 解法：每台机器本地统计频率 (HashMap)。
 *   然后通过 hash 聚合相同数字的频率，最后全局合并，取最大频率。
 *
 * 问题 3：找中位数 (Median)
 * - 在大规模分布式环境下找到全局中位数。
 * - 解法：使用分布式 Quickselect。
 *   1. 随机选择一个 pivot；
 *   2. 所有机器并行统计各自 <= pivot 的数量；
 *   3. 汇总总数，判断中位数落在左边还是右边；
 *   4. 递归缩小搜索区间，直到找到中位数。
 *
 * ===============================================
 * 这类题在 LeetCode 没有原题，但和以下题类似：
 * - 找重复数 → LC 287 (Find the Duplicate Number) 的分布式版
 * - 找众数   → LC 169 (Majority Element), LC 347 (Top K Frequent Elements)
 * - 找中位数 → LC 295 (Find Median from Data Stream)，扩展为分布式
 *
 * 面试讲法总结（3 分钟版本）：
 * - Duplicate：hash 分桶 (mod N)，保证重复数落在同一机器，本地检测即可。
 * - Mode：本地计数，分布式聚合，再取最大。
 * - Median：分布式 Quickselect，通过 pivot + 统计递归收敛。
 *
 * ===============================================
 */
public class DistributedAlgorithms {

    // ========== 1. 找重复数 ==========
    static class DuplicateFinder {
        // 分桶 (mod N)
        public Map<Integer, List<Integer>> shardData(List<Integer> localData, int totalMachines) {
            Map<Integer, List<Integer>> shardMap = new HashMap<>();
            for (int num : localData) {
                int target = num % totalMachines;
                shardMap.computeIfAbsent(target, k -> new ArrayList<>()).add(num);
            }
            return shardMap;
        }

        // 在接收端检测重复
        public int detectDuplicate(List<Integer> received) {
            Set<Integer> seen = new HashSet<>();
            for (int num : received) {
                if (!seen.add(num)) {
                    return num;
                }
            }
            return -1;
        }
    }

    // ========== 2. 找众数 ==========
    static class ModeFinder {
        public Map<Integer, Integer> localCount(List<Integer> data) {
            Map<Integer, Integer> freq = new HashMap<>();
            for (int num : data) {
                freq.put(num, freq.getOrDefault(num, 0) + 1);
            }
            return freq;
        }

        public Map<Integer, Integer> aggregateCounts(List<Map<Integer, Integer>> allCounts) {
            Map<Integer, Integer> global = new HashMap<>();
            for (Map<Integer, Integer> local : allCounts) {
                for (Map.Entry<Integer, Integer> e : local.entrySet()) {
                    global.put(e.getKey(), global.getOrDefault(e.getKey(), 0) + e.getValue());
                }
            }
            return global;
        }

        public int findMode(Map<Integer, Integer> global) {
            int mode = -1, maxFreq = 0;
            for (Map.Entry<Integer, Integer> e : global.entrySet()) {
                if (e.getValue() > maxFreq) {
                    maxFreq = e.getValue();
                    mode = e.getKey();
                }
            }
            return mode;
        }
    }

    // ========== 3. 找中位数 ==========
    static class MedianFinder {
        private Random rand = new Random();

        public int findMedian(List<List<Integer>> allMachines, int totalCount) {
            int left = Integer.MIN_VALUE, right = Integer.MAX_VALUE;
            int k = (totalCount + 1) / 2;

            while (left < right) {
                int pivot = pickPivot(left, right);
                int count = 0;
                for (List<Integer> local : allMachines) {
                    count += countLE(local, pivot);
                }
                if (count >= k) {
                    right = pivot;
                } else {
                    left = pivot + 1;
                }
            }
            return left;
        }

        private int pickPivot(int left, int right) {
            return left + rand.nextInt(right - left + 1);
        }

        private int countLE(List<Integer> local, int pivot) {
            int cnt = 0;
            for (int num : local) if (num <= pivot) cnt++;
            return cnt;
        }
    }

    // ========== Demo ==========
    public static void main(String[] args) {
        List<Integer> data1 = Arrays.asList(1, 5, 9, 3, 7, 5);
        List<Integer> data2 = Arrays.asList(2, 4, 6, 8, 10);

        DuplicateFinder df = new DuplicateFinder();
        int dup = df.detectDuplicate(data1);
        System.out.println("Duplicate: " + dup);

        ModeFinder mf = new ModeFinder();
        Map<Integer, Integer> freq1 = mf.localCount(data1);
        Map<Integer, Integer> freq2 = mf.localCount(data2);
        int mode = mf.findMode(mf.aggregateCounts(Arrays.asList(freq1, freq2)));
        System.out.println("Mode: " + mode);

        MedianFinder medf = new MedianFinder();
        int median = medf.findMedian(Arrays.asList(data1, data2), data1.size() + data2.size());
        System.out.println("Median: " + median);
    }
}

