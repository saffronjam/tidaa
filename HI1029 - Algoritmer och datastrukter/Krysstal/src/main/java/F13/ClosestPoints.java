package F13;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPoints {
    public static class Point {
        float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private float distanceTo(Point other) {
            return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
        }
    }

    public static class CompareByX implements Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.x < o2.x) {
                return -1;
            } else if (o1.x > o2.x) {
                return 1;
            }
            return 0;
        }
    }

    public static class CompareByY implements Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.y < o2.y) {
                return -1;
            } else if (o1.y > o2.y) {
                return 1;
            }
            return 0;
        }
    }

    private final Point[] points;

    public ClosestPoints(Point[] points) {
        this.points = Arrays.copyOf(points, points.length);
        Arrays.sort(this.points, new CompareByX());
    }

    public float calcDumb() {
        return bruteForceClosestDistance(0, points.length - 1);
    }

    public float calc() {
        return calc(0, points.length - 1);
    }

    private float calc(int left, int right) {
        // Antagande att mindre än 4 punkter är rimligt att börja bruteforca
        if (right - left < 3) {
            return bruteForceClosestDistance(left, right);
        }
        int center = left + (right - left) / 2;

        // Den 'naiva' delen i koden, som bara bryr sig om
        // punkterna inom sitt eget område
        float tryLower = calc(left, center);
        float tryUpper = calc(center + 1, right);
        float bestNaive = Math.min(tryLower, tryUpper);

        // 'Tillägget' som kollar punkter nära till
        // det andra området, sorterade på y-värdet
        var overlapInterval = new Point[right - left + 1];
        int actualSize = 0;
        for (var i = left; i <= right; i++) {
            float xDistFromMid = Math.abs(points[i].x - points[center].x);
            if (xDistFromMid < bestNaive) {
                overlapInterval[actualSize++] = points[i];
            }
        }
        var bestOverlap = calcOverlap(overlapInterval, actualSize, bestNaive);

        return Math.min(bestNaive, bestOverlap);
    }

    private float calcOverlap(Point[] overlap, int actualSize, float min) {
        Arrays.sort(overlap, 0, actualSize, new CompareByY());
        float minDistance = min;

        for (int i = 0; i < actualSize; i++) {
            for (int j = i + 1; j < actualSize && Math.abs(overlap[i].y - overlap[j].y) < minDistance; j++) {
                var distance = overlap[i].distanceTo(overlap[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    private float bruteForceClosestDistance(int left, int right) {
        float minDistance = Float.MAX_VALUE;
        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                var distance = points[i].distanceTo(points[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }
}
