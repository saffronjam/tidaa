/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F11.MinSpanTreeMatrix;

import java.util.BitSet;

/**
 * Denna använde ett bitSet för att representera T i föreläsningen
 * Läs gärna mer om BitSet
 * Man kan också använda ett Set vilket blir snyggare men kostar mer overhead
 *
 * @author bfelt
 */


public class Dijktras {

    public static void main(String[] args) {
        int X = Integer.MAX_VALUE;
        int[][] w = {
                {X, 2, X, X, X, 1, X, X},
                {2, X, 2, 2, 4, X, X, X},
                {X, 2, X, X, 3, X, X, 1},
                {X, 2, X, X, 3, 1, X, X},
                {X, 4, 3, 3, X, X, 7, X},
                {1, X, X, 1, X, X, 5, X},
                {X, X, X, X, 7, 5, X, 6},
                {X, X, 1, X, X, X, 6, X}};

        int d[] = {0, X, X, X, X, X, X, X};
        int p[] = new int[8];
        BitSet setT = new BitSet(8);
        setT.set(0, 8);

        for (int i = 0; i < d.length - 1; i++) {
            int u = next(d, setT);
            setT.clear(u);
            for (int v = 0; v < w[u].length; v++)
                if (w[u][v] < X && d[v] > d[u] + w[u][v]) {
                    d[v] = d[u] + w[u][v];
                    p[v] = u;
                }
        }
        for (int i = 1; i < d.length; i++)
            System.out.println("Nod " + (char) (i + 'A') + " har avstånd " + d[i] + " till nod A via: " + (char) (p[i] + 'A'));
    }

    static int next(int[] d, BitSet setT) {
        int dMin = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < d.length; i++)
            if (d[i] < dMin && setT.get(i)) {
                dMin = d[i];
                minIndex = i;
            }
        return minIndex;
    }

}
