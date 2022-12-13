package F5;

public class SquareRoot {
    public static void main(String[] args) {
        int x = 49;
        System.out.println(ROT(x, 1, 1));
    }

    public static double ROT(double n, double a, double e) {
        return Math.abs(Math.pow(a, 2) - n) < e ? a : ROT(n, (Math.pow(a, 2) + n) / (2 * a),e);
    }
}
