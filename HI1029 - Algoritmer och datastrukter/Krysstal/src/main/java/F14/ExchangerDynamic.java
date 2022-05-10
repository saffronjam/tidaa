package F14;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ExchangerDynamic {
    private final int[] change;

    public ExchangerDynamic(int[] change) {
        this.change = change;
    }

    public int[] exchange(int amount) {
        return exchange(amount, 0, new int[change.length], new HashMap<>());
    }

//    private int[] exchange(int amount, int[] coinOrder, int startFrom, HashMap<Integer, Integer> hashMap) {
//        if (amount < 0) return null;
//
//        for (int i = 0; i < change.length; i++) {
//            if (change[i] == amount) {
//                coinOrder[i]++;
//                return coinOrder;
//            }
//        }
//
//        int[] winner = new int[change.length];
//        var minimumCoins = Integer.MAX_VALUE;
//        for (int i = startFrom; i < change.length; i++) {
//            coinOrder[i]++;
//            var attempt = continueWithHashMap(amount - change[i], coinOrder, i, hashMap);
//            if (attempt != null) {
//                var noCoins = Arrays.stream(attempt).sum();
//                if (noCoins < minimumCoins) {
//                    minimumCoins = noCoins;
//                    winner = Arrays.copyOf(attempt, attempt.length);
//                }
//            }
//            coinOrder[i]--;
//        }
//        coinOrder = winner;
//        return coinOrder;
//    }

    private int[] exchange(int amount, int startFrom, int[] coinOrder, HashMap<Integer, Integer> hashMap) {
        if (amount < 0) return null;

        for (int i = 0; i < change.length; i++) {
            if (change[i] == amount) {
                int[] initialList = new int[change.length];
                initialList[i] = 1;
                return initialList;
            }
        }

        var minimumCoins = Integer.MAX_VALUE;
        int[] winner = null;
        var candidateBase = Arrays.stream(coinOrder).sum();
        var copy = Arrays.copyOf(coinOrder, coinOrder.length);
        for (int i = startFrom; i < change.length; i++) {

            copy[i]++;
            if (hashMap.containsKey(amount - change[i]) && hashMap.get(amount - change[i]) <= candidateBase + 1) {
                continue;
            }
            hashMap.put(amount, candidateBase + 1);

            var attempt = exchange(amount - change[i], i, copy, hashMap);
            if (attempt != null) {
                attempt[i]++;
                var noCoins = Arrays.stream(attempt).sum();
                if (noCoins < minimumCoins) {
                    winner = attempt;
                    minimumCoins = noCoins;
                }
            }
            copy[i]--;
        }
        return winner;
    }
}
