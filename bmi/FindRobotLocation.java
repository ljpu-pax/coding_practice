import java.util.*;

/**
 * Applied Intuition Interview Question
 *
 * Problem: Find robot location in 2D plane
 *
 * Given:
 * - A helper function: isRobotInSquare(latitude, longitude, sideLength)
 *   Returns true if robot is inside the square defined by:
 *   - Top-left corner at (latitude, longitude)
 *   - Side length = sideLength
 *
 * Task: Find the robot's coordinates within a given precision
 *
 * Approach: Binary Search in 2D space
 * - Start with large square covering entire space (-180, 180)
 * - Divide into 4 quadrants
 * - Check which quadrant contains the robot
 * - Recursively narrow down until precision is met
 *
 * Time: O(log(1/precision))
 * Space: O(log(1/precision)) for recursion stack
 */
class FindRobotLocation {

    /**
     * Robot location result
     */
    static class RobotLocation {
        double latitude;
        double longitude;
        double sideLength;

        RobotLocation(double latitude, double longitude, double sideLength) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.sideLength = sideLength;
        }

        @Override
        public String toString() {
            return String.format("Location: (%.6f, %.6f), Square size: %.6f",
                               latitude, longitude, sideLength);
        }
    }

    /**
     * Approach 1: Iterative binary search - divide into 4 quadrants
     *
     * Each iteration divides current square into 4 smaller squares:
     * +-------+-------+
     * |  TL   |  TR   |  TL = top-left
     * +-------+-------+  TR = top-right
     * |  BL   |  BR   |  BL = bottom-left
     * +-------+-------+  BR = bottom-right
     */
    public RobotLocation findRobot(double precision) {
        double left = -180;
        double right = 180;
        double top = 90;    // Latitude ranges from -90 to 90
        double bottom = -90;
        double length = right - left;  // 360

        while (length > precision) {
            length /= 2;

            // Four possible corners (top-left of each quadrant)
            double[][] possibleCorners = {
                {top, left},                    // Top-left quadrant
                {top, left + length},           // Top-right quadrant
                {top - length, left},           // Bottom-left quadrant
                {top - length, left + length}   // Bottom-right quadrant
            };

            // Find which quadrant contains the robot
            for (double[] corner : possibleCorners) {
                double lat = corner[0];
                double lon = corner[1];

                if (isRobotInSquare(lat, lon, length)) {
                    top = lat;
                    left = lon;
                    break;
                }
            }
        }

        return new RobotLocation(top, left, length);
    }

    /**
     * Approach 2: Recursive binary search
     */
    public RobotLocation findRobotRecursive(double precision) {
        return findRobotHelper(-180, 180, 90, -90, precision);
    }

    private RobotLocation findRobotHelper(double left, double right,
                                         double top, double bottom,
                                         double precision) {
        double length = right - left;

        if (length <= precision) {
            return new RobotLocation(top, left, length);
        }

        double midLon = left + length / 2;
        double midLat = top - length / 2;
        double halfLength = length / 2;

        // Check 4 quadrants in order
        double[][] quadrants = {
            {top, left},                // Top-left
            {top, midLon},              // Top-right
            {midLat, left},             // Bottom-left
            {midLat, midLon}            // Bottom-right
        };

        for (double[] corner : quadrants) {
            if (isRobotInSquare(corner[0], corner[1], halfLength)) {
                // Found the quadrant, recurse into it
                double newTop = corner[0];
                double newLeft = corner[1];
                return findRobotHelper(newLeft, newLeft + halfLength,
                                      newTop, newTop - halfLength,
                                      precision);
            }
        }

        // Should never reach here if robot exists
        return new RobotLocation(top, left, length);
    }

    /**
     * Approach 3: Get center coordinates instead of top-left corner
     */
    public double[] findRobotCenter(double precision) {
        RobotLocation loc = findRobot(precision);

        // Convert top-left corner to center
        double centerLat = loc.latitude - loc.sideLength / 2;
        double centerLon = loc.longitude + loc.sideLength / 2;

        return new double[]{centerLat, centerLon};
    }

    /**
     * Approach 4: Optimized - binary search on each dimension separately
     *
     * First find latitude range, then longitude range
     * This can be faster in some cases
     */
    public RobotLocation findRobotSeparateDimensions(double precision) {
        // Binary search for latitude
        double topLat = 90, bottomLat = -90;
        while (topLat - bottomLat > precision) {
            double midLat = (topLat + bottomLat) / 2;
            double height = topLat - midLat;

            // Check if robot is in top half
            if (isRobotInSquare(topLat, -180, 360)) {
                if (isRobotInSquare(topLat, -180, height)) {
                    bottomLat = midLat;
                } else {
                    topLat = midLat;
                }
            }
        }

        // Binary search for longitude
        double leftLon = -180, rightLon = 180;
        while (rightLon - leftLon > precision) {
            double midLon = (leftLon + rightLon) / 2;
            double width = midLon - leftLon;

            if (isRobotInSquare(topLat, leftLon, width)) {
                rightLon = midLon;
            } else {
                leftLon = midLon;
            }
        }

        return new RobotLocation(topLat, leftLon, Math.max(topLat - bottomLat, rightLon - leftLon));
    }

    /**
     * Approach 5: Adaptive precision - start with coarse grid, refine
     */
    public RobotLocation findRobotAdaptive(double finalPrecision) {
        double left = -180, top = 90;
        double length = 360;

        // Coarse phase: quickly narrow down to ~10x finalPrecision
        double coarsePrecision = finalPrecision * 10;
        while (length > coarsePrecision) {
            length /= 2;

            double[][] corners = {
                {top, left},
                {top, left + length},
                {top - length, left},
                {top - length, left + length}
            };

            for (double[] corner : corners) {
                if (isRobotInSquare(corner[0], corner[1], length)) {
                    top = corner[0];
                    left = corner[1];
                    break;
                }
            }
        }

        // Fine phase: precise refinement
        while (length > finalPrecision) {
            length /= 2;

            double[][] corners = {
                {top, left},
                {top, left + length},
                {top - length, left},
                {top - length, left + length}
            };

            for (double[] corner : corners) {
                if (isRobotInSquare(corner[0], corner[1], length)) {
                    top = corner[0];
                    left = corner[1];
                    break;
                }
            }
        }

        return new RobotLocation(top, left, length);
    }

    // ========== Helper Function (Provided by interviewer) ==========

    /**
     * Mock implementation for testing
     * In actual interview, this function is provided
     */
    private static double actualRobotLat = 37.7749;  // San Francisco
    private static double actualRobotLon = -122.4194;

    private boolean isRobotInSquare(double latitude, double longitude, double sideLength) {
        // Square is defined by top-left corner (latitude, longitude)
        // and extends sideLength units right and down

        double topLat = latitude;
        double leftLon = longitude;
        double bottomLat = latitude - sideLength;
        double rightLon = longitude + sideLength;

        return actualRobotLat >= bottomLat &&
               actualRobotLat <= topLat &&
               actualRobotLon >= leftLon &&
               actualRobotLon <= rightLon;
    }

    /**
     * Set robot location for testing
     */
    public void setRobotLocation(double lat, double lon) {
        actualRobotLat = lat;
        actualRobotLon = lon;
    }
}

/**
 * Python equivalent translation
 */
class PythonEquivalent {
    /*
    def find_robot(precision: float) -> tuple[float, float, float]:
        left, right = -180, 180
        top, bottom = 90, -90
        length = right - left

        while length > precision:
            length /= 2

            possible_corners = [
                (top, left),
                (top, left + length),
                (top - length, left),
                (top - length, left + length),
            ]

            for lat, lon in possible_corners:
                if is_robot_in_square(lat, lon, length):
                    top, left = lat, lon
                    break

        return top, left, length

    def find_robot_center(precision: float) -> tuple[float, float]:
        top, left, length = find_robot(precision)
        center_lat = top - length / 2
        center_lon = left + length / 2
        return center_lat, center_lon
    */
}

/**
 * Test cases
 */
class FindRobotLocationTest {
    public static void main(String[] args) {
        testBasicCases();
        testDifferentPrecisions();
        testDifferentLocations();
        testAllApproaches();
    }

    private static void testBasicCases() {
        System.out.println("=== Testing Robot Location Finding ===\n");
        FindRobotLocation finder = new FindRobotLocation();

        // Set robot at San Francisco
        finder.setRobotLocation(37.7749, -122.4194);

        FindRobotLocation.RobotLocation result = finder.findRobot(0.001);
        System.out.println("Test 1: Find robot with precision 0.001");
        System.out.println("  Actual location: (37.7749, -122.4194)");
        System.out.println("  Found: " + result);
        System.out.println();

        double[] center = finder.findRobotCenter(0.001);
        System.out.println("Test 2: Find robot center");
        System.out.printf("  Center: (%.6f, %.6f)\n", center[0], center[1]);
        System.out.println();
    }

    private static void testDifferentPrecisions() {
        System.out.println("=== Testing Different Precisions ===\n");
        FindRobotLocation finder = new FindRobotLocation();
        finder.setRobotLocation(37.7749, -122.4194);

        double[] precisions = {1.0, 0.1, 0.01, 0.001, 0.0001};

        for (double precision : precisions) {
            FindRobotLocation.RobotLocation result = finder.findRobot(precision);
            System.out.printf("Precision: %.4f -> %s\n", precision, result);
        }
        System.out.println();
    }

    private static void testDifferentLocations() {
        System.out.println("=== Testing Different Locations ===\n");
        FindRobotLocation finder = new FindRobotLocation();

        double[][] locations = {
            {0, 0},           // Origin
            {37.7749, -122.4194},  // San Francisco
            {40.7128, -74.0060},   // New York
            {51.5074, -0.1278},    // London
            {35.6762, 139.6503},   // Tokyo
            {-33.8688, 151.2093}   // Sydney
        };

        for (double[] location : locations) {
            finder.setRobotLocation(location[0], location[1]);
            FindRobotLocation.RobotLocation result = finder.findRobot(0.001);
            System.out.printf("Actual: (%.4f, %.4f) -> Found: (%.4f, %.4f), Size: %.6f\n",
                           location[0], location[1],
                           result.latitude, result.longitude, result.sideLength);
        }
        System.out.println();
    }

    private static void testAllApproaches() {
        System.out.println("=== Testing All Approaches ===\n");
        FindRobotLocation finder = new FindRobotLocation();
        finder.setRobotLocation(37.7749, -122.4194);

        double precision = 0.001;

        FindRobotLocation.RobotLocation result1 = finder.findRobot(precision);
        System.out.println("Iterative: " + result1);

        FindRobotLocation.RobotLocation result2 = finder.findRobotRecursive(precision);
        System.out.println("Recursive: " + result2);

        FindRobotLocation.RobotLocation result3 = finder.findRobotAdaptive(precision);
        System.out.println("Adaptive: " + result3);

        double[] center = finder.findRobotCenter(precision);
        System.out.printf("Center coordinates: (%.6f, %.6f)\n", center[0], center[1]);
        System.out.println();
    }
}

/**
 * Follow-up Questions and Optimizations
 */
class RobotLocationFollowUps {
    /*
     * Q1: What if the search space is not square but rectangular?
     * A1:
     *   - Track width and height separately
     *   - Divide into 4 rectangles instead of squares
     *   - Binary search on each dimension independently
     *
     * Q2: What if there are multiple robots?
     * A2:
     *   - Use the same approach for each robot
     *   - Modify helper to return which robots are in square
     *   - Can parallelize searches for different robots
     *
     * Q3: How to optimize number of API calls to isRobotInSquare?
     * A3:
     *   - Current: O(log(1/precision)) calls
     *   - Can't do better asymptotically with binary search
     *   - Could use interpolation search if we have distance info
     *   - Cache results if function is expensive
     *
     * Q4: What if coordinates are not lat/lon but arbitrary 2D space?
     * A4:
     *   - Algorithm works for any 2D rectangular bounds
     *   - Just change initial bounds to match search space
     *   - No other changes needed
     *
     * Q5: How to handle edge cases (robot on boundary)?
     * A5:
     *   - Depends on isRobotInSquare implementation
     *   - Usually inclusive boundaries are used
     *   - Precision parameter handles boundary cases
     *
     * Q6: Can we use gradient descent or other optimization?
     * A6:
     *   - Binary search is already optimal for discrete queries
     *   - Gradient methods need continuous distance function
     *   - If we have "distance to robot" function, can use:
     *     - Gradient descent
     *     - Newton's method
     *     - Ternary search
     *
     * Q7: What's the minimum number of queries needed?
     * A7:
     *   - Theoretical minimum: log₄(area / precision²)
     *   - Each query eliminates 3/4 of search space
     *   - Our algorithm is optimal with 4-way split
     *
     * Q8: How to handle 3D space?
     * A8:
     *   - Divide cube into 8 octants instead of 4 quadrants
     *   - Same algorithm, just 8-way branching
     *   - Queries: O(log₈(volume / precision³))
     */
}

/**
 * Complexity Analysis
 */
class ComplexityAnalysis {
    /*
     * Time Complexity:
     * - Each iteration reduces search space by 1/4
     * - Initial area: 360 × 180 = 64,800 square degrees
     * - Final area: precision²
     * - Iterations: log₄(64800 / precision²)
     * - Approximately: 2 × log₂(1/precision) + constant
     *
     * For precision = 0.001:
     * - log₂(1/0.001) ≈ 10
     * - Total iterations ≈ 20
     * - API calls ≈ 20 (checking 4 quadrants, but only 1 succeeds)
     *
     * Space Complexity:
     * - Iterative: O(1)
     * - Recursive: O(log(1/precision)) for call stack
     *
     * Number of API Calls:
     * - Worst case: 4 calls per iteration
     * - Total: 4 × log₄(area/precision²)
     * - Optimization: Can stop after finding correct quadrant
     * - Actual: ~log₄(area/precision²) calls
     */
}
