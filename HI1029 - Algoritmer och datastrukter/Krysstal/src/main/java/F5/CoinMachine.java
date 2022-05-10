package F5;

public class CoinMachine {

    public static void main(String[] args) {
        for (int i = 0; i < 115; i++) {
            System.out.println("Points: " + i + " Coins: " + calc(1, i));
        }
    }

    public static int calc(int points, int to) {
        if (points > to) {
            return -1;
        }
        if (points == to) {
            return 0;
        }

        int plus = calc(points + 4, to);
        int mult = calc(points * 3, to);
        if (plus < 0 && mult < 0) {
            return -1;
        }
        if (plus < 0) {
            return mult + 10;
        }
        if (mult < 0) {
            return plus + 5;
        }
        return Math.min(plus + 5, mult + 10);
    }
}
