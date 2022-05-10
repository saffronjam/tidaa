package F13.SkylineSolver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SkylineSolver {

    public static class Skyline {
        private final List<Point> points;

        public Skyline() {
            this.points = new ArrayList<>();
        }

        public Skyline(List<Point> points) {
            this.points = points;
        }

        public List<Point> getPoints() {
            return points;
        }

        public int getSize() {
            return points.size();
        }

        public Point getPoint(int index) {
            return points.get(index);
        }

        public void addPoint(Point point) {
            points.add(point);
        }
    }

    public Skyline computeSkyline(Building[] buildings) {
        var finalSkyline = new Skyline(new ArrayList<>());
        int noBuildings = buildings.length;

        // Forsätt dela upp byggnader tills att vi har 0 eller 1 byggnad

        // Om vi inte har några byggnader, returnera en tom lista
        if (noBuildings == 0) return finalSkyline;

        // Om vi har en byggnad har vi en simpel skyline, som utgörs av den byggnaden
        if (noBuildings == 1) {
            int x1 = buildings[0].x1;
            int x2 = buildings[0].x2;
            int h = buildings[0].height;

            finalSkyline.addPoint(new Point(x1, h));
            finalSkyline.addPoint(new Point(x2, 0));
            return finalSkyline;
        }

        var lower = computeSkyline(Arrays.copyOfRange(buildings, 0, noBuildings / 2));
        var upper = computeSkyline(Arrays.copyOfRange(buildings, noBuildings / 2, noBuildings));
        return mergeSkylines(lower, upper);
    }

    public Skyline mergeSkylines(Skyline leftSkyline, Skyline rightSkyline) {
        int leftSize = leftSkyline.getSize();
        int rightSize = rightSkyline.getSize();
        Skyline mergedSkyline = new Skyline();

        int leftIndex = 0;
        int rightIndex = 0;
        int currY = 0;
        int currX;
        int leftY = 0;
        int rightY = 0;
        int maxY;

        // Så länge våra skylines överlappar
        while ((leftIndex < leftSize) && (rightIndex < rightSize)) {
            // Hämta ut punkterna från vardera skyline
            var pointL = leftSkyline.getPoint(leftIndex);
            var pointR = rightSkyline.getPoint(rightIndex);

            // Välj hörnet längst till vänster
            // (antingen en byggnads hörn eller en skyline-skärningspunkt)
            if (pointL.x < pointR.x) {
                currX = pointL.x;
                leftY = pointL.y;
                leftIndex++;
            } else {
                currX = pointR.x;
                rightY = pointR.y;
                rightIndex++;
            }

            maxY = Math.max(leftY, rightY);
            if (currY != maxY) {
                updateFinalSkyline(mergedSkyline, new Point(currX, maxY));
                currY = maxY;
            }
        }

        // Lägg till slutet av den skylinen som inte överlappar något, antigen leftSkylin eller rightSkyline
        if (leftIndex < leftSize) {
            addSkylineRange(mergedSkyline, leftSkyline, leftIndex, leftSize, currY);
        } else if (rightIndex < rightSize) {
            addSkylineRange(mergedSkyline, rightSkyline, rightIndex, rightSize, currY);
        }

        return mergedSkyline;
    }

    public void updateFinalSkyline(Skyline finalSkyline, Point point) {
        finalSkyline.addPoint(new Point(point.x, point.y));
        // Hade troligen behövt kolla ifall vi är på samma x, och endast uppdaterat y
        // if (points.get(lastIndex).x == point.x)
        // Typ: points.get(lastIndex).y = point.y;
    }

    public void addSkylineRange(Skyline out, Skyline in, int start, int end, int height) {
        for (int i = start; i < end; i++) {
            var point = in.getPoint(i);
            if (point.y != height) {
                updateFinalSkyline(out, point);
                height = point.y;
            }
        }
    }
}