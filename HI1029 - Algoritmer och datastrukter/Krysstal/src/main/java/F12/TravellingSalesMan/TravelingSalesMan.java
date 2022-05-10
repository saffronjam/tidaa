/*
    Rutten ges genom att route fylls med index för städerna i den ordning de besöks
    Det förutsätts att rutten börjar i staden med index 0 och route[0] ska sättas till 0.
    Sedan ska route[1] sättas till index för den stad som ligger närmast stad 0 osv.
    route har samma dimension som w och sista platsen i route: route[route.length-1] ska sättas
    till sista staden som besöks innan resan tillbaka till stad med index 0.
    minDistanceGreedy ska returnera längden på hela resan inklusive tillbaka till startstaden.
 */

package F12.TravellingSalesMan;

import java.util.BitSet;

public class TravelingSalesMan {
    public static int minDistanceGreedy(int[][] matrix, int[] route) {
        int totalDistance = 0;
        var visisted = new BitSet(matrix.length);
        int next = 0;
        for (int i = 0; i < route.length; i++) {
            visisted.set(next);
            route[i] = next;
            totalDistance += matrix[next][i];
            next = closestNeighbor(matrix, visisted, next);
            assert next != -1;
        }
        totalDistance += matrix[route[route.length - 1]][route[0]];
        return totalDistance;
    }

    private static int closestNeighbor(int[][] matrix, BitSet visited, int from) {
        int shortestIndex = -1;
        int shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < matrix[from].length; i++) {
            if (!visited.get(i) && matrix[from][i] < shortestDistance) {
                shortestIndex = i;
                shortestDistance = matrix[from][i];
            }
        }
        return shortestIndex;
    }
}
