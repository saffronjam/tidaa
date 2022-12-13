package F14;

public class TesterCoinMachineDynamic {
    public static void main(String[] args) {
        var myCoinMachineDynamic = new CoinMachineDynamic();

        int coinGoal = 109;
        {
            var begin = System.nanoTime();
            var result = myCoinMachineDynamic.solve(coinGoal, CoinMachineDynamic.Strategy.DynamicTopDown);
            var end = System.nanoTime();
            var swaps = myCoinMachineDynamic.getSwaps();
            System.out.println("Solution dynamic top-down: " + result + " (Swaps: " + swaps + ") (Took: " + (end - begin) / 1000 + " us)");
        }
        {
            var begin = System.nanoTime();
            var result = myCoinMachineDynamic.solve(coinGoal, CoinMachineDynamic.Strategy.NativeRecursion);
            var end = System.nanoTime();
            var swaps = myCoinMachineDynamic.getSwaps();
            System.out.println("Solution recursive: " + result + " (Swaps: " + swaps + ") (Took: " + (end - begin) / 1000 + " us)");
        }

        // För coinGoal == 109 gör vi många gånger färre funktionsanrop, men då vi hela tiden
        // behöver gå in i en hashmap lägger det på tid. Detta är mikrosekunder, så inget som
        // märks. Däremot ser vi alltså att tiden som algoritmen tar inte blir bättre för så
        // här små tal.

        // För coinGoal == 1093 blir den dynamiska lösningen många gånger bättre.
    }
}
