package F13;

import java.util.Arrays;
import java.util.Random;

public class TesterClosestPoints {
    public static void main(String[] args) {
        var random = new Random();
        int noPoints = 10000;
        var points = new ClosestPoints.Point[noPoints];
        for (int i = 0; i < noPoints; i++) {
            points[i] = new ClosestPoints.Point(random.nextFloat() * 2.0f - 1.0f, random.nextFloat() * 2.0f - 1.0f);
        }

        var copyPoints = new ClosestPoints.Point[points.length];
        for (int i = 0; i < copyPoints.length; i++) {
            copyPoints[i] = new ClosestPoints.Point(points[i].x, points[i].y);
        }

        var minDistPointsSmart = new ClosestPoints(points);
        var minDistPointsDumb = new ClosestPoints(copyPoints);

        {
            var begin = System.nanoTime();
            var distanceSmart = minDistPointsSmart.calc();
            var end = System.nanoTime();
            System.out.println("Smart: " + distanceSmart + " (Took: " + (end - begin) / 1000000 + "ms)");
        }
        {
            var begin = System.nanoTime();
            var distanceDumb = minDistPointsDumb.calcDumb();
            var end = System.nanoTime();
            System.out.println("Dumb: " + distanceDumb + " (Took: " + (end - begin) / 1000000 + "ms)");
        }
    }
}
