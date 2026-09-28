import java.util.*;

/**
 * LeetCode 973: K Closest Points to Origin
 *
 * Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane
 * and an integer k, return the k closest points to the origin (0, 0).
 *
 * The distance between two points on the X-Y plane is the Euclidean distance
 * (i.e., √(x1 - x2)² + (y1 - y2)²).
 *
 * You may return the answer in any order. The answer is guaranteed to be unique
 * (except for the order that it is in).
 *
 * Example 1:
 * Input: points = [[1,3],[-2,2]], k = 1
 * Output: [[-2,2]]
 * Explanation:
 * The distance between (1, 3) and the origin is sqrt(10).
 * The distance between (-2, 2) and the origin is sqrt(8).
 * Since sqrt(8) < sqrt(10), (-2, 2) is closer to the origin.
 * We only want the closest k = 1 points from the origin, so the answer is just [[-2,2]].
 *
 * Example 2:
 * Input: points = [[3,3],[5,-1],[-2,4]], k = 2
 * Output: [[3,3],[-2,4]]
 * Explanation: The answer [[-2,4],[3,3]] would also be accepted.
 *
 * Constraints:
 * - 1 <= k <= points.length <= 10^4
 * - -10^4 <= xi, yi <= 10^4
 */
class KClosestPointsToOrigin {

    /**
     * Approach 1: Max Heap (Priority Queue)
     *
     * Use a max heap of size k to keep track of k closest points.
     * If heap size > k, remove the farthest point.
     *
     * Time: O(N log k) where N = number of points
     * Space: O(k) for the heap
     */
    public int[][] kClosest(int[][] points, int k) {
        // Max heap based on distance (farthest point at top)
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                b[0] * b[0] + b[1] * b[1],
                a[0] * a[0] + a[1] * a[1]
            )
        );

        for (int[] point : points) {
            maxHeap.offer(point);

            // Keep only k closest points
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        // Extract results
        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }

        return result;
    }

    /**
     * Approach 2: Min Heap - Sort all and take k
     *
     * Time: O(N log N)
     * Space: O(N)
     */
    public int[][] kClosestMinHeap(int[][] points, int k) {
        // Min heap based on distance
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                a[0] * a[0] + a[1] * a[1],
                b[0] * b[0] + b[1] * b[1]
            )
        );

        for (int[] point : points) {
            minHeap.offer(point);
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }

        return result;
    }

    /**
     * Approach 3: Sorting
     *
     * Sort all points by distance, then take first k
     *
     * Time: O(N log N)
     * Space: O(log N) for sorting
     */
    public int[][] kClosestSort(int[][] points, int k) {
        Arrays.sort(points, (a, b) ->
            Integer.compare(
                a[0] * a[0] + a[1] * a[1],
                b[0] * b[0] + b[1] * b[1]
            )
        );

        return Arrays.copyOfRange(points, 0, k);
    }

    /**
     * Approach 4: QuickSelect (Optimal)
     *
     * Use QuickSelect to find the k-th smallest element.
     * Average Time: O(N), Worst: O(N²)
     * Space: O(log N) for recursion
     *
     * This is the most efficient approach!
     */
    public int[][] kClosestQuickSelect(int[][] points, int k) {
        quickSelect(points, 0, points.length - 1, k);
        return Arrays.copyOfRange(points, 0, k);
    }

    private void quickSelect(int[][] points, int left, int right, int k) {
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(points, left, right);

        if (pivotIndex == k) {
            return; // Found k-th element
        } else if (pivotIndex < k) {
            quickSelect(points, pivotIndex + 1, right, k); // Search right
        } else {
            quickSelect(points, left, pivotIndex - 1, k); // Search left
        }
    }

    private int partition(int[][] points, int left, int right) {
        int[] pivot = points[right];
        int pivotDist = distance(pivot);
        int i = left;

        for (int j = left; j < right; j++) {
            if (distance(points[j]) <= pivotDist) {
                swap(points, i, j);
                i++;
            }
        }

        swap(points, i, right);
        return i;
    }

    private int distance(int[] point) {
        return point[0] * point[0] + point[1] * point[1];
    }

    private void swap(int[][] points, int i, int j) {
        int[] temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }

    /**
     * Approach 5: TreeMap (when need sorted order)
     *
     * Time: O(N log N)
     * Space: O(N)
     */
    public int[][] kClosestTreeMap(int[][] points, int k) {
        // TreeMap: distance -> list of points with that distance
        TreeMap<Integer, List<int[]>> distanceMap = new TreeMap<>();

        for (int[] point : points) {
            int dist = distance(point);
            distanceMap.putIfAbsent(dist, new ArrayList<>());
            distanceMap.get(dist).add(point);
        }

        List<int[]> result = new ArrayList<>();
        for (List<int[]> pointList : distanceMap.values()) {
            for (int[] point : pointList) {
                if (result.size() < k) {
                    result.add(point);
                } else {
                    break;
                }
            }
            if (result.size() >= k) {
                break;
            }
        }

        return result.toArray(new int[k][2]);
    }

    /**
     * Approach 6: Bucket Sort (when distances are in limited range)
     *
     * Time: O(N + R) where R = range of distances
     * Space: O(R)
     *
     * Useful when coordinate range is limited
     */
    public int[][] kClosestBucketSort(int[][] points, int k) {
        int maxDist = 0;
        for (int[] point : points) {
            maxDist = Math.max(maxDist, distance(point));
        }

        // Bucket: distance -> list of points
        List<int[]>[] buckets = new ArrayList[maxDist + 1];
        for (int i = 0; i <= maxDist; i++) {
            buckets[i] = new ArrayList<>();
        }

        for (int[] point : points) {
            buckets[distance(point)].add(point);
        }

        // Collect k closest points
        List<int[]> result = new ArrayList<>();
        for (int dist = 0; dist <= maxDist && result.size() < k; dist++) {
            for (int[] point : buckets[dist]) {
                if (result.size() < k) {
                    result.add(point);
                } else {
                    break;
                }
            }
        }

        return result.toArray(new int[k][2]);
    }
}

/**
 * Related variations and extensions
 */
class KClosestVariations {

    /**
     * Variation 1: Find k closest points to a given point (not origin)
     */
    public int[][] kClosestToPoint(int[][] points, int k, int[] target) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                distanceToPoint(b, target),
                distanceToPoint(a, target)
            )
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }

    private int distanceToPoint(int[] p1, int[] p2) {
        int dx = p1[0] - p2[0];
        int dy = p1[1] - p2[1];
        return dx * dx + dy * dy;
    }

    /**
     * Variation 2: Find k closest points in 3D space
     */
    public int[][] kClosest3D(int[][] points, int k) {
        // points[i] = [x, y, z]
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                distance3D(b),
                distance3D(a)
            )
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        int[][] result = new int[k][3];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }

    private int distance3D(int[] point) {
        return point[0] * point[0] + point[1] * point[1] + point[2] * point[2];
    }

    /**
     * Variation 3: Find k farthest points from origin
     */
    public int[][] kFarthest(int[][] points, int k) {
        // Use min heap instead (remove closest points)
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                a[0] * a[0] + a[1] * a[1],
                b[0] * b[0] + b[1] * b[1]
            )
        );

        for (int[] point : points) {
            minHeap.offer(point);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }
        return result;
    }

    /**
     * Variation 4: Return points sorted by distance
     */
    public int[][] kClosestSorted(int[][] points, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                a[0] * a[0] + a[1] * a[1],
                b[0] * b[0] + b[1] * b[1]
            )
        );

        for (int[] point : points) {
            minHeap.offer(point);
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }
        return result;
    }
}

/**
 * Test cases
 */
class KClosestPointsToOriginTest {
    public static void main(String[] args) {
        testBasicCases();
        testAllApproaches();
        testVariations();
        performanceComparison();
    }

    private static void testBasicCases() {
        System.out.println("=== LeetCode 973: K Closest Points to Origin ===\n");
        KClosestPointsToOrigin solution = new KClosestPointsToOrigin();

        // Test 1
        int[][] points1 = {{1, 3}, {-2, 2}};
        int k1 = 1;
        int[][] result1 = solution.kClosest(points1, k1);
        System.out.println("Test 1: k=1, two points");
        System.out.println("  Input: " + Arrays.deepToString(points1));
        System.out.println("  Output: " + Arrays.deepToString(result1));
        System.out.println("  Expected: [[-2, 2]]");
        System.out.println();

        // Test 2
        int[][] points2 = {{3, 3}, {5, -1}, {-2, 4}};
        int k2 = 2;
        int[][] result2 = solution.kClosest(points2, k2);
        System.out.println("Test 2: k=2, three points");
        System.out.println("  Input: " + Arrays.deepToString(points2));
        System.out.println("  Output: " + Arrays.deepToString(result2));
        System.out.println("  Expected: [[3, 3], [-2, 4]] (any order)");
        System.out.println();

        // Test 3
        int[][] points3 = {{0, 1}, {1, 0}};
        int k3 = 2;
        int[][] result3 = solution.kClosest(points3, k3);
        System.out.println("Test 3: All points requested");
        System.out.println("  Output: " + Arrays.deepToString(result3));
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Testing All Approaches ===\n");
        KClosestPointsToOrigin solution = new KClosestPointsToOrigin();

        int[][] points = {{1, 3}, {-2, 2}, {5, 8}, {0, 1}};
        int k = 2;

        System.out.println("Input: " + Arrays.deepToString(points) + ", k=" + k + "\n");

        int[][] result1 = solution.kClosest(points.clone(), k);
        System.out.println("Max Heap: " + Arrays.deepToString(result1));

        int[][] result2 = solution.kClosestMinHeap(points.clone(), k);
        System.out.println("Min Heap: " + Arrays.deepToString(result2));

        int[][] result3 = solution.kClosestSort(points.clone(), k);
        System.out.println("Sorting: " + Arrays.deepToString(result3));

        int[][] result4 = solution.kClosestQuickSelect(points.clone(), k);
        System.out.println("QuickSelect: " + Arrays.deepToString(result4));

        int[][] result5 = solution.kClosestTreeMap(points.clone(), k);
        System.out.println("TreeMap: " + Arrays.deepToString(result5));

        int[][] result6 = solution.kClosestBucketSort(points.clone(), k);
        System.out.println("Bucket Sort: " + Arrays.deepToString(result6));
        System.out.println();
    }

    private static void testVariations() {
        System.out.println("=== Testing Variations ===\n");
        KClosestVariations variations = new KClosestVariations();

        // Test 1: Closest to specific point
        int[][] points1 = {{1, 1}, {3, 3}, {2, 2}};
        int[] target = {0, 0};
        int[][] result1 = variations.kClosestToPoint(points1, 2, target);
        System.out.println("Test 1: K closest to point (0, 0)");
        System.out.println("  Output: " + Arrays.deepToString(result1));
        System.out.println();

        // Test 2: K farthest points
        int[][] points2 = {{1, 3}, {-2, 2}, {5, 8}, {0, 1}};
        int[][] result2 = variations.kFarthest(points2, 2);
        System.out.println("Test 2: K farthest points");
        System.out.println("  Output: " + Arrays.deepToString(result2));
        System.out.println();
    }

    private static void performanceComparison() {
        System.out.println("=== Performance Comparison ===\n");

        System.out.println("Approach Comparison:");
        System.out.println("1. Max Heap: O(N log k) - BEST for k << N");
        System.out.println("   - Only stores k elements at a time");
        System.out.println("   - Optimal when k is much smaller than N");
        System.out.println();

        System.out.println("2. Min Heap: O(N log N) - Good for returning sorted");
        System.out.println("   - Stores all elements");
        System.out.println("   - Results are naturally sorted by distance");
        System.out.println();

        System.out.println("3. Sorting: O(N log N) - Simple implementation");
        System.out.println("   - Easy to understand and implement");
        System.out.println("   - Modifies original array");
        System.out.println();

        System.out.println("4. QuickSelect: O(N) average - BEST overall");
        System.out.println("   - Average O(N), worst case O(N²)");
        System.out.println("   - Best for large datasets");
        System.out.println("   - Result not sorted");
        System.out.println();

        System.out.println("5. Bucket Sort: O(N + R) - BEST for limited range");
        System.out.println("   - When distance range is small");
        System.out.println("   - Requires extra space for buckets");
        System.out.println();

        System.out.println("Recommendation:");
        System.out.println("- k << N: Use Max Heap");
        System.out.println("- k ≈ N/2: Use QuickSelect");
        System.out.println("- Need sorted: Use Min Heap");
        System.out.println("- Limited range: Use Bucket Sort");
        System.out.println();
    }
}
