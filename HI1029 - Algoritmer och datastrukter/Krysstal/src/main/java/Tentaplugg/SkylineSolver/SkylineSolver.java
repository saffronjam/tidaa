package Tentaplugg.SkylineSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkylineSolver {

    public static class Skyline {

    }

    private final Building[] buildings;

    public SkylineSolver(Building[] buildings) {
        this.buildings = buildings;
    }

    public List<Point> getSkyline() {
        return getSkyline(this.buildings);
    }

    private List<Point> getSkyline(Building[] buildings) {
        if (buildings.length == 0) {
            return new ArrayList<>();
        }
        if (buildings.length == 1) {
            var b = buildings[0];
            var list = new ArrayList<Point>(2);
            list.add(new Point(b.x1, b.height));
            list.add(new Point(b.x2, 0));
            return list;
        }

        var center = buildings.length / 2;
        var leftSkyline = getSkyline(Arrays.copyOfRange(buildings, 0, center));
        var rightSkyline = getSkyline(Arrays.copyOfRange(buildings, center, buildings.length));

        return mergeSkylines(leftSkyline, rightSkyline);
    }

    private List<Point> mergeSkylines(List<Point> left, List<Point> right) {
        int leftIndex = 0;
        int rightIndex = 0;
        int currX, currY = 0;
        int maxY = 0;
        int leftY = 0, rightY = 0;
        List<Point> result = new ArrayList<>();

        while (leftIndex < left.size() && rightIndex < right.size()) {
            var leftPoint = left.get(leftIndex);
            var rightPoint = right.get(rightIndex);

            if (leftPoint.x < rightPoint.x) {
                currX = leftPoint.x;
                leftY = leftPoint.y;
                leftIndex++;
            } else {
                currX = rightPoint.x;
                rightY = rightPoint.y;
                rightIndex++;
            }

            maxY = Math.max(leftY, rightY);
            if (maxY != currY) {
                result.add(new Point(currX, maxY));
                currY = maxY;
            }
        }

        appendRest(left, result, leftIndex, currY);
        appendRest(right, result, rightIndex, currY);

        return result;
    }

    private void appendRest(List<Point> from, List<Point> to, int startingFrom, int currentHeight) {
        for (int i = startingFrom; i < from.size(); i++) {
            var candidate = from.get(i);
            if (candidate.y != currentHeight) {
                to.add(candidate);
                currentHeight = candidate.y;
            }
        }
    }
}