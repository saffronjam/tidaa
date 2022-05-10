package F14;

import java.lang.reflect.Array;
import java.util.Arrays;

public class TesterExchangerDynamic {
    public static void main(String[] args) {
        var billsArray = new int[]{1000, 500, 100, 50, 20, 10, 5, 1};
        var myExchangerDynamic = new ExchangerDynamic(billsArray);

        int amount = 5231;
        {
            var begin = System.nanoTime();
            var result = myExchangerDynamic.exchange(amount);
            var end = System.nanoTime();
            System.out.println("Solution dynamic top-down: " + Arrays.toString(result) + " (Took: " + (end - begin) / 1000 + " us)");
        }
    }
}
