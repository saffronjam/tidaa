package F14;

import java.util.Arrays;

public class TesterBallExchangerDynamic {
    public static void main(String[] args) {
        var myBallExchangerDynamic = new BallExchangerDynamic();

        var start = new BallExchangerDynamic.State(22, 22, 13);
        {
            var begin = System.nanoTime();
            var result = myBallExchangerDynamic.exchange(start);
            var end = System.nanoTime();
            var noCalls = myBallExchangerDynamic.getCalls();
            System.out.println("Solution dynamic top-down: " + result + " (Calls: " + noCalls + ") (Took: " + (end - begin) / 1000 + " us)");
        }
    }
}
