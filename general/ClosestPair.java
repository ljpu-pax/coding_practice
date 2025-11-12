import java.util.*;

public class ClosestPair {

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static double closestPair(Point[] points) {
        int n = points.length;
        Point[] px = points.clone();
        Point[] py = points.clone();
        Arrays.sort(px, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(py, Comparator.comparingDouble(p -> p.y));
        return closestUtil(px, py);
    }

    private static double closestUtil(Point[] px, Point[] py) {
        int n = px.length;
        if (n <= 3) return bruteForce(px);

        int mid = n / 2;
        Point midPoint = px[mid];

        // Divide py into pyl and pyr based on midPoint.x
        List<Point> pylList = new ArrayList<>();
        List<Point> pyrList = new ArrayList<>();
        for (Point point : py) {
            if (point.x <= midPoint.x)
                pylList.add(point);
            else
                pyrList.add(point);
        }

        Point[] pxLeft = Arrays.copyOfRange(px, 0, mid);
        Point[] pxRight = Arrays.copyOfRange(px, mid, n);
        Point[] pyl = pylList.toArray(new Point[0]);
        Point[] pyr = pyrList.toArray(new Point[0]);

        double dl = closestUtil(pxLeft, pyl);
        double dr = closestUtil(pxRight, pyr);

        double d = Math.min(dl, dr);

        List<Point> strip = new ArrayList<>();
        for (Point point : py) {
            if (Math.abs(point.x - midPoint.x) < d)
                strip.add(point);
        }

        return Math.min(d, stripClosest(strip, d));
    }

    private static double stripClosest(List<Point> strip, double d) {
        double min = d;
        int n = strip.size();

        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n && (strip.get(j).y - strip.get(i).y) < min; ++j) {
                double dist = distance(strip.get(i), strip.get(j));
                if (dist < min) min = dist;
            }
        }
        return min;
    }

    private static double bruteForce(Point[] points) {
        double min = Double.MAX_VALUE;
        int n = points.length;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                min = Math.min(min, distance(points[i], points[j]));
            }
        }
        return min;
    }

    private static double distance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }

    public static void main(String[] args) {
        Point[] points = {
            new Point(2.0, 3.0),
            new Point(12.0, 30.0),
            new Point(40.0, 50.0),
            new Point(5.0, 1.0),
            new Point(12.0, 10.0),
            new Point(3.0, 4.0)
        };
        System.out.printf("The smallest distance is %.4f\n", closestPair(points));
    }
}


