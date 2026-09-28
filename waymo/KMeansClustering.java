import java.util.*;

/**
 * K-Means Clustering - Nearest Neighbor Clustering
 *
 * Problem:
 * Implement K-Means clustering algorithm to group n data points into k clusters.
 *
 * Algorithm Steps:
 * 1. Initialize: Randomly select k points as initial centroids
 * 2. Assignment: Assign each point to the nearest centroid
 * 3. Update: Recalculate centroids as the mean of all points in each cluster
 * 4. Repeat steps 2-3 until convergence
 *
 * Convergence Criteria:
 * - Centroids don't change significantly (within epsilon)
 * - OR maximum iterations reached
 * - OR no points change clusters
 *
 * Key Points:
 * - Must randomly select k initial centroids from existing points (not arbitrary points)
 * - This ensures all centroids start within the data range
 * - Prevents empty clusters from bad initialization
 *
 * Time Complexity: O(n * k * i * d) where:
 * - n = number of points
 * - k = number of clusters
 * - i = number of iterations
 * - d = number of dimensions
 *
 * Space Complexity: O(n * d + k * d)
 */
public class KMeansClustering {

    private static final double EPSILON = 1e-4;
    private static final int MAX_ITERATIONS = 100;

    /**
     * Main K-Means clustering method
     *
     * @param points Array of data points, each point is a double array
     * @param k Number of clusters
     * @return Array of cluster assignments for each point
     */
    public int[] kMeans(double[][] points, int k) {
        if (points == null || points.length == 0 || k <= 0 || k > points.length) {
            throw new IllegalArgumentException("Invalid input");
        }

        int n = points.length;
        int dimensions = points[0].length;

        // Step 1: Initialize centroids by randomly selecting k points
        double[][] centroids = initializeCentroids(points, k);

        // Store cluster assignments for each point
        int[] assignments = new int[n];
        Arrays.fill(assignments, -1);

        boolean converged = false;
        int iteration = 0;

        while (!converged && iteration < MAX_ITERATIONS) {
            iteration++;

            // Step 2: Assignment step - assign each point to nearest centroid
            int[] newAssignments = assignPointsToClusters(points, centroids);

            // Step 3: Check convergence (no points changed clusters)
            converged = Arrays.equals(assignments, newAssignments);
            assignments = newAssignments;

            if (converged) {
                break;
            }

            // Step 4: Update step - recalculate centroids
            double[][] newCentroids = updateCentroids(points, assignments, k, dimensions);

            // Alternative convergence check: centroids didn't move significantly
            if (centroidsConverged(centroids, newCentroids)) {
                converged = true;
            }

            centroids = newCentroids;
        }

        System.out.println("Converged after " + iteration + " iterations");
        return assignments;
    }

    /**
     * Initialize centroids by randomly selecting k distinct points from the dataset
     * This ensures centroids start within the data range and prevents empty clusters
     */
    private double[][] initializeCentroids(double[][] points, int k) {
        int n = points.length;
        int dimensions = points[0].length;

        double[][] centroids = new double[k][dimensions];

        // Use a set to track selected indices to avoid duplicates
        Set<Integer> selectedIndices = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < k; i++) {
            int randomIndex;

            // Keep generating until we find an unselected point
            do {
                randomIndex = random.nextInt(n);
            } while (selectedIndices.contains(randomIndex));

            selectedIndices.add(randomIndex);

            // Copy the selected point as a centroid
            centroids[i] = Arrays.copyOf(points[randomIndex], dimensions);
        }

        return centroids;
    }

    /**
     * Assign each point to the nearest centroid
     */
    private int[] assignPointsToClusters(double[][] points, double[][] centroids) {
        int n = points.length;
        int[] assignments = new int[n];

        for (int i = 0; i < n; i++) {
            double minDistance = Double.MAX_VALUE;
            int closestCluster = 0;

            // Find the nearest centroid
            for (int j = 0; j < centroids.length; j++) {
                double distance = euclideanDistance(points[i], centroids[j]);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestCluster = j;
                }
            }

            assignments[i] = closestCluster;
        }

        return assignments;
    }

    /**
     * Update centroids by calculating the mean of all points in each cluster
     */
    private double[][] updateCentroids(double[][] points, int[] assignments, int k, int dimensions) {
        double[][] newCentroids = new double[k][dimensions];
        int[] clusterSizes = new int[k];

        // Sum all points in each cluster
        for (int i = 0; i < points.length; i++) {
            int cluster = assignments[i];
            clusterSizes[cluster]++;

            for (int d = 0; d < dimensions; d++) {
                newCentroids[cluster][d] += points[i][d];
            }
        }

        // Calculate mean for each cluster
        for (int i = 0; i < k; i++) {
            if (clusterSizes[i] > 0) {
                for (int d = 0; d < dimensions; d++) {
                    newCentroids[i][d] /= clusterSizes[i];
                }
            }
            // If cluster is empty (shouldn't happen with good initialization),
            // keep the previous centroid
        }

        return newCentroids;
    }

    /**
     * Check if centroids have converged (moved less than EPSILON)
     */
    private boolean centroidsConverged(double[][] oldCentroids, double[][] newCentroids) {
        for (int i = 0; i < oldCentroids.length; i++) {
            double distance = euclideanDistance(oldCentroids[i], newCentroids[i]);
            if (distance > EPSILON) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate Euclidean distance between two points
     */
    private double euclideanDistance(double[] point1, double[] point2) {
        double sum = 0;
        for (int i = 0; i < point1.length; i++) {
            double diff = point1[i] - point2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    /**
     * Parse test data from string format
     * Format: "x1,y1;x2,y2;x3,y3;..."
     * Example: "1.0,2.0;3.0,4.0;5.0,6.0"
     */
    public double[][] parseTestData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new double[0][0];
        }

        String[] pointStrings = data.trim().split(";");
        List<double[]> points = new ArrayList<>();

        for (String pointStr : pointStrings) {
            String[] coords = pointStr.trim().split(",");
            double[] point = new double[coords.length];

            for (int i = 0; i < coords.length; i++) {
                point[i] = Double.parseDouble(coords[i].trim());
            }

            points.add(point);
        }

        return points.toArray(new double[0][]);
    }

    /**
     * Parse test data from multiple lines
     * Each line is a point with comma-separated coordinates
     */
    public double[][] parseTestDataMultiline(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new double[0][0];
        }

        String[] lines = data.trim().split("\n");
        List<double[]> points = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] coords = line.split(",");
            double[] point = new double[coords.length];

            for (int i = 0; i < coords.length; i++) {
                point[i] = Double.parseDouble(coords[i].trim());
            }

            points.add(point);
        }

        return points.toArray(new double[0][]);
    }

    /**
     * Get cluster centers (final centroids)
     */
    public double[][] getCentroids(double[][] points, int[] assignments, int k) {
        int dimensions = points[0].length;
        double[][] centroids = new double[k][dimensions];
        int[] counts = new int[k];

        for (int i = 0; i < points.length; i++) {
            int cluster = assignments[i];
            counts[cluster]++;

            for (int d = 0; d < dimensions; d++) {
                centroids[cluster][d] += points[i][d];
            }
        }

        for (int i = 0; i < k; i++) {
            if (counts[i] > 0) {
                for (int d = 0; d < dimensions; d++) {
                    centroids[i][d] /= counts[i];
                }
            }
        }

        return centroids;
    }

    /**
     * Print clustering results
     */
    public void printResults(double[][] points, int[] assignments, int k) {
        System.out.println("\n=== Clustering Results ===");

        for (int i = 0; i < k; i++) {
            System.out.println("\nCluster " + i + ":");
            List<Integer> clusterPoints = new ArrayList<>();

            for (int j = 0; j < assignments.length; j++) {
                if (assignments[j] == i) {
                    clusterPoints.add(j);
                }
            }

            System.out.println("Size: " + clusterPoints.size());
            System.out.println("Points: ");
            for (int idx : clusterPoints) {
                System.out.println("  " + Arrays.toString(points[idx]));
            }
        }

        // Print centroids
        double[][] centroids = getCentroids(points, assignments, k);
        System.out.println("\nFinal Centroids:");
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + i + ": " + Arrays.toString(centroids[i]));
        }
    }

    // ==================== Test Cases ====================

    public static void main(String[] args) {
        KMeansClustering kmeans = new KMeansClustering();

        System.out.println("=== K-Means Clustering Tests ===\n");

        // Test 1: Parse test data
        System.out.println("Test 1: Parse test data");
        String testData1 = "1.0,1.0;1.5,2.0;3.0,4.0;5.0,7.0;3.5,5.0;4.5,5.0;3.5,4.5";
        double[][] points1 = kmeans.parseTestData(testData1);
        System.out.println("Parsed " + points1.length + " points");
        for (double[] point : points1) {
            System.out.println(Arrays.toString(point));
        }
        System.out.println();

        // Test 2: Simple 2D clustering
        System.out.println("Test 2: Simple 2D clustering (k=2)");
        double[][] points2 = {
            {1.0, 1.0},
            {1.5, 2.0},
            {2.0, 1.0},
            {8.0, 8.0},
            {8.5, 8.0},
            {9.0, 9.0}
        };
        int[] result2 = kmeans.kMeans(points2, 2);
        kmeans.printResults(points2, result2, 2);
        System.out.println();

        // Test 3: 3 clusters in 2D
        System.out.println("Test 3: Three clusters (k=3)");
        double[][] points3 = {
            {1.0, 1.0}, {1.5, 2.0}, {2.0, 1.0},     // Cluster 1
            {8.0, 8.0}, {8.5, 8.0}, {9.0, 9.0},     // Cluster 2
            {1.0, 8.0}, {1.5, 9.0}, {2.0, 8.5}      // Cluster 3
        };
        int[] result3 = kmeans.kMeans(points3, 3);
        kmeans.printResults(points3, result3, 3);
        System.out.println();

        // Test 4: Parse multiline format
        System.out.println("Test 4: Parse multiline test data");
        String testData4 = """
            1.0,1.0
            2.0,2.0
            8.0,8.0
            9.0,9.0
            """;
        double[][] points4 = kmeans.parseTestDataMultiline(testData4);
        System.out.println("Parsed " + points4.length + " points");
        int[] result4 = kmeans.kMeans(points4, 2);
        kmeans.printResults(points4, result4, 2);
        System.out.println();

        // Test 5: Higher dimensional data (3D)
        System.out.println("Test 5: 3D clustering");
        double[][] points5 = {
            {1.0, 1.0, 1.0}, {1.5, 1.5, 1.5},
            {8.0, 8.0, 8.0}, {8.5, 8.5, 8.5},
            {1.0, 8.0, 1.0}, {1.5, 8.5, 1.5}
        };
        int[] result5 = kmeans.kMeans(points5, 3);
        kmeans.printResults(points5, result5, 3);
        System.out.println();

        // Test 6: Demonstrate why random initialization matters
        System.out.println("Test 6: Multiple runs show different results due to random initialization");
        double[][] points6 = {
            {1.0, 1.0}, {2.0, 1.0}, {1.5, 2.0},
            {8.0, 8.0}, {9.0, 8.0}, {8.5, 9.0},
            {1.0, 8.0}, {2.0, 8.0}, {1.5, 9.0}
        };

        System.out.println("Run 1:");
        int[] result6a = kmeans.kMeans(points6, 3);
        System.out.println("Assignments: " + Arrays.toString(result6a));

        System.out.println("\nRun 2:");
        int[] result6b = kmeans.kMeans(points6, 3);
        System.out.println("Assignments: " + Arrays.toString(result6b));

        System.out.println("\nNote: Results may differ due to random initialization,");
        System.out.println("but clusters should be semantically similar.");
    }
}
