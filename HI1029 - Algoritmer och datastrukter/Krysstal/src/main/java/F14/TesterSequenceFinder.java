package F14;

import java.util.Arrays;
import java.util.Random;

public class TesterSequenceFinder {
    public static void main(String[] args) {

//        var mySequenceFinder = new SequenceFinder(new int[]{1, 7, 8, 9, 1, 2, 10, 11, 10});

        // [2, 4, 4, 7, 9, 9, 5, 9, 5, 8, 2, 0, 7, 8, 2]
        // [1, 8, 7, 4, 8, 4, 1, 5, 4, 9]

        // 6, 2, 4, 7, 8, 7, 3, 0, 5, 9, 4, 8, 8, 7, 0


        var list = randomIntList(1005, 10);
        var mySequenceFinder = new SequenceFinder(list);
        System.out.println(Arrays.toString(list));

        {
            var begin = System.nanoTime();
            var result = mySequenceFinder.findBottomUp();
            var end = System.nanoTime();
            System.out.println("Solution dynamic bottom-up: " + (result + 1) + " (Took: " + (end - begin) / 1000 + " us)");
        }
        {
            var begin = System.nanoTime();
            var result = mySequenceFinder.findTopDown();
            var end = System.nanoTime();
            System.out.println("Solution dynamic top-down: " + (result) + " (Took: " + (end - begin) / 1000 + " us)");
        }
    }

    public static int[] randomIntList(int size, int bound) {
        var ret = new int[size];
        var random = new Random();
        for (int i = 0; i < size; i++) {
            ret[i] = random.nextInt(bound);
        }
        return ret;
    }
}
