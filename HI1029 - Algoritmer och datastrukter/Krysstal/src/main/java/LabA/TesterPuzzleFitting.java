package LabA;

public class TesterPuzzleFitting {

    public static void main(String[] args) {

        var puzzleFitting = new PuzzleFitting(3, 1, 5, 8);
        long begin = System.currentTimeMillis();
        puzzleFitting.solve();
        long end = System.currentTimeMillis();
        System.out.println("Took " + (end - begin) + " ms");
    }

}
