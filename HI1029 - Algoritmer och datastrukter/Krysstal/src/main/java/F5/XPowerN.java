package F5;

public class XPowerN {
    public static void main(String[] args) {
        int x = 2, n = 0;
        System.out.println("Iterative: " + iterative(x, n));
        System.out.println("Recursive: " + recursive(x, n));
    }

    public static int recursive(int x, int n) {
        return n == 0 ? 1 : x * recursive(x, n);
    }

    public static int iterative(int x, int n) {
        int result = 1;
        for (int i = 0; i < n; i++) {
            result *= x;
        }
        return result;
    }
}
