package F5;

import F3.ArrayListStack;

public class BallExchanger {
    public static void main(String[] args) {
        int b = 22, w = 22, r = 13;
        var begin = System.nanoTime();
        int exchanges = calc(b, w, r, 0);
        var end = System.nanoTime();
        System.out.println("Number of Exchanges (" + b + ", " + w + ", " + r + "): " + exchanges + " (Took: " + (end - begin) / 1000 + " us)");
    }

    /*
    1 blå -> 3 vita, 1 röd
    1 vit -> 3 blåa, 4 röda
    1 röd -> 2 blåa, 1 vit
     */
    public static int calc(int b, int w, int r, int cost) {
        if (cost > 15 || (b == w && w == r)) {
            return 0;
        }

        int tryB = b > 0 ? calc(b - 1, w + 3, r + 1, cost + 1) : 15;
        int tryW = w > 0 ? calc(b + 3, w - 1, r + 4, cost + 1) : 15;
        int tryR = r > 0 ? calc(b + 2, w + 1, r - 1, cost + 1) : 15;

        return 1 + Math.min(tryB, Math.min(tryW, tryR));
    }
}
