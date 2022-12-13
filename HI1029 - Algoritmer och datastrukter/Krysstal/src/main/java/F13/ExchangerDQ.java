package F13;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class ExchangerDQ {
    private final int[] change;
    private HashSet<Integer> prevStates;

    public ExchangerDQ(int[] change) {
        this.change = change;
        this.prevStates = new HashSet<>();
    }

    int skipped = 0;

    public int[] exchange(int amount) {
        var result = exchange(amount, 0);
        System.out.println("Skipped " + skipped + " identical states");
        return result;
    }

    private int[] exchange(int amount, int startFrom) {
        if (amount < 0) return null;

        for (int i = 0; i < change.length; i++) {
            if (change[i] == amount) {
                int[] initialList = new int[change.length];
                initialList[i] = 1;
                return initialList;
            }
        }

        var minimumCoins = Integer.MAX_VALUE;
        int[] coinOrder = null;
        for (int i = startFrom; i < change.length; i++) {
            var attempt = exchange(amount - change[i], i);
            if (attempt != null) {
                if (beenHereBefore(amount, attempt)) {
                    continue;
                }
                attempt[i]++;
                var noCoins = Arrays.stream(attempt).sum();
                if (noCoins < minimumCoins) {
                    coinOrder = attempt;
                    minimumCoins = noCoins;
                }
            }
        }
        return coinOrder;
    }

    private boolean beenHereBefore(int amount, int[] attempt) {
        boolean result = false;
        var hash = hashCode(amount, attempt);
        if (prevStates.contains(hash)) {
            skipped++;
            result = true;
        }
        prevStates.add(hash);
        return result;
    }

    private int hashCode(int amount, int[] coinOrder) {
        int result = 31 * amount;
        for (int element : coinOrder)
            result = 31 * (1 + result) + element;

        return result;
    }
}
