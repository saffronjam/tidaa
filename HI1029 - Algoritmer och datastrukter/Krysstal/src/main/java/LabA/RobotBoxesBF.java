package LabA;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.stream.IntStream;

public class RobotBoxesBF {

    public static final char[] correctAnswer = new char[]{'A', 'B', 'C', 'D', 'E'};

    private class State {
        char[] boxes;
        String instructions;

        public State(char[] boxes, String instructions) {
            this.boxes = boxes;
            this.instructions = instructions;
        }
    }

    public static void main(String[] args) {
        var robotBoxes = new RobotBoxesBF();

        var result1 = robotBoxes.moveBoxes(new char[]{'B', 'E', 'A', 'C', 'D'});
        var result2 = robotBoxes.moveBoxes(new char[]{'B', 'A', 'E', 'C', 'D'});
        var result3 = robotBoxes.moveBoxes(new char[]{'E', 'A', 'D', 'B', 'C'});
        System.out.println("first: " + (result1.length() > 15 ? "Failed" : result1) );
        System.out.println("second: " + (result2.length() > 15 ? "Failed" : result2) );
        System.out.println("third: " + (result3.length() > 15 ? "Failed" : result3) );
    }

    public String moveBoxes(char[] boxes) {

        var stateQueue = new ArrayDeque<State>();
        var next = new State(boxes, "");
        while (!isDone(next.boxes)) {
            stateQueue.offer(swapLeftMost(next));
            stateQueue.offer(shiftRight(next));
            next = stateQueue.poll();
        }
        return next.instructions;
    }

    private State swapLeftMost(State state) {
        var newBoxes = Arrays.copyOf(state.boxes, state.boxes.length);
        var tmp = newBoxes[0];
        newBoxes[0] = newBoxes[1];
        newBoxes[1] = tmp;
        return new State(newBoxes, state.instructions + "b");
    }

    private State shiftRight(State state) {
        var newBoxes = Arrays.copyOf(state.boxes, state.boxes.length);
        var edge = newBoxes[newBoxes.length - 1];
        System.arraycopy(newBoxes, 0, newBoxes, 1, newBoxes.length - 1);
        newBoxes[0] = edge;
        return new State(newBoxes, state.instructions + "s");
    }

    private boolean isDone(char[] boxes) {
        if (boxes.length != correctAnswer.length) {
            return false;
        }
        return IntStream.range(0, correctAnswer.length).allMatch(i -> boxes[i] == correctAnswer[i]);
    }

}
