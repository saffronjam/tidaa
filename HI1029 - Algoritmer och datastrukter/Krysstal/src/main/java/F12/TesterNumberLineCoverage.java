package F12;

import java.util.Arrays;
import java.util.Random;

public class TesterNumberLineCoverage {
    public static void main(String[] args) {
        var myNumberLineCoverage = new NumberLineCoverage();

        {
            System.out.println("--- PRE-DEFINED ---");
            var attempt = new float[]{1, 2, 3, 5, 6, 8, 8, 9, 12};
            System.out.println("Trying with: " + Arrays.toString(attempt));
            var result = myNumberLineCoverage.coverUp(attempt);
            System.out.println(myNumberLineCoverage);
            System.out.println("No Intevals: " + result);
        }

        {
            System.out.println("--- RANDOM ---");
            var attempt = randomFloatArray(20, 20);
            Arrays.sort(attempt);
            System.out.println("Trying with: " + Arrays.toString(attempt));
            var result = myNumberLineCoverage.coverUp(attempt);
            System.out.println(myNumberLineCoverage);
            System.out.println("No Intevals: " + result);
        }
    }

    public static float[] randomFloatArray(int size, float bound) {
        var ret = new float[size];
        var random = new Random();
        for (int i = 0; i < size; i++) {
            ret[i] = bound - 2 * random.nextFloat() * bound;
        }
        return ret;
    }
}
