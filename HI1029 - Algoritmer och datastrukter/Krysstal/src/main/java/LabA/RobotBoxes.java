package LabA;

import java.util.Arrays;
import java.util.stream.IntStream;

public class RobotBoxes {

    public static final char[] correctAnswer = new char[]{'A', 'B', 'C', 'D', 'E'};

    public static void main(String[] args) {
        var robotBoxes = new RobotBoxes();

        var result1 = robotBoxes.moveBoxes(new char[]{'B', 'E', 'A', 'C', 'D'});
        var result2 = robotBoxes.moveBoxes(new char[]{'B', 'A', 'E', 'C', 'D'});
        var result3 = robotBoxes.moveBoxes(new char[]{'E', 'A', 'D', 'B', 'C'});
        System.out.println("first: " + (result1.length() > 15 ? "Failed" : result1) );
        System.out.println("second: " + (result2.length() > 15 ? "Failed" : result2) );
        System.out.println("third: " + (result3.length() > 15 ? "Failed" : result3) );

    }

    public String moveBoxes(char[] boxes) {
        return moveBoxesRecursive(boxes, 0);
    }

    private String moveBoxesRecursive(char[] boxes, int depth) {
        if (depth > 15 || isDone(boxes)) {
            return "";
        }

        var swapLeftMost = swapLeftMost(boxes, depth);
        var shiftRight = shiftRight(boxes, depth);

        return swapLeftMost.length() < shiftRight.length() ?  "b" + swapLeftMost : "s" + shiftRight;
    }

    private String swapLeftMost(char[] boxes, int depth) {
        var newBoxes = Arrays.copyOf(boxes, boxes.length);
        var tmp = newBoxes[0];
        newBoxes[0] = newBoxes[1];
        newBoxes[1] = tmp;
        return moveBoxesRecursive(newBoxes, depth + 1);
    }

    private String shiftRight(char[] boxes, int depth) {
        var newBoxes = Arrays.copyOf(boxes, boxes.length);
        var edge = newBoxes[newBoxes.length - 1];
        System.arraycopy(newBoxes, 0, newBoxes, 1, newBoxes.length - 1);
        newBoxes[0] = edge;
        return moveBoxesRecursive(newBoxes, depth + 1);
    }

    private boolean isDone(char[] boxes) {
        if (boxes.length != correctAnswer.length) {
            return false;
        }
        return IntStream.range(0, correctAnswer.length).allMatch(i -> boxes[i] == correctAnswer[i]);
    }

    private String getBoxesString(char[] boxes) {
        var builder = new StringBuilder();
        for (var characer : boxes) {
            builder.append(characer);
        }
        return builder.toString();
    }

}
