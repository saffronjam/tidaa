package F14;

import java.util.Arrays;

public class TesterUniquePaths {

    public static void main(String[] args) {
        var myUniquePaths = new UniquePaths(5);

        {
            var begin = System.nanoTime();
            var result = myUniquePaths.count();
            var end = System.nanoTime();
            System.out.println("Solution dynamic bottom-up: " + result + " (Took: " + (end - begin) / 1000 + " us)");
        }
    }

}
