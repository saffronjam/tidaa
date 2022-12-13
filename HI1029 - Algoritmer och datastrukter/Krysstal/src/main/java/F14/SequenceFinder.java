package F14;

import java.util.Arrays;
import java.util.HashMap;

public class SequenceFinder {

    int[] sequence;
    int longest = 1;

    public SequenceFinder(int[] sequence) {
        this.sequence = sequence;
    }

    public int findBottomUp() {
        // Beräkna alla platser med bottom-up strategi
        // Vi beräknar från start till varje annan plats
        var cache = new int[sequence.length];
        for (int i = 1; i < sequence.length; i++)
            for (int j = 0; j < i; j++)
                // Här kollar vi både om vårt tal är större än det vi jämför mot,
                // men sen också om vi redan har en längre delsekvens
                if (sequence[i] > sequence[j] && cache[i] < cache[j] + 1)
                    cache[i] = cache[j] + 1;

        // Hitta nu de högsta bland de alla beräknade värdena
        return Arrays.stream(cache).max().orElse(-1);
    }

    public int findTopDown() {
        // Markera varje värden i cachen som oanvänd
        // Denna behöver vara två-dimensionell då varje element
        // Kan ha "svansar" som börjar på något av de andra talen

        // Exempel:
        //      1, 2, 3, 8, 4, 5  bör returnera 5, och inte max 3 (då 8 "blockar")

        var cache = new int[sequence.length + 1][sequence.length];
        for (var internal : cache) {
            Arrays.fill(internal, -1);
        }
        return findTopDown(-1, 0, cache);
    }

    public int findTopDown(int prev, int current, int[][] cache) {
        // Basfall
        if (current == sequence.length) {
            return 0;
        }

        // Checka cachen i början, för att se om vi redan varit här
        if (cache[prev + 1][current] > -1) {
            return cache[prev + 1][current];
        }

        // Första fallet:
        // Vi traverserar rekursivt genom arrayen
        // och inkluderar current
        int includeThis = 0;
        if (prev < 0 || sequence[current] > sequence[prev]) {
            includeThis = 1 + findTopDown(current, current + 1, cache);
        }

        // Andra fallet:
        // Vi exkluderar den vi är på nu, och
        int excludeThis = findTopDown(prev, current + 1, cache);

        cache[prev + 1][current] = Math.max(includeThis, excludeThis);
        return cache[prev + 1][current];
    }
}


