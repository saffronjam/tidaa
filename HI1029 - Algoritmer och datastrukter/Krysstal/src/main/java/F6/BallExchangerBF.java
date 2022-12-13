package F6;

import java.util.LinkedList;

public class BallExchangerBF {

    private static class State {
        int r;
        int w;
        int b;
        int count;

        public State(int b, int w, int r, int count) {
            this.b = b;
            this.w = w;
            this.r = r;
            this.count = count;
        }

        @Override
        public String toString() {
            return "Args{" +
                    "r=" + r +
                    ", w=" + w +
                    ", b=" + b +
                    ", count=" + count +
                    '}';
        }
    }

    public static void main(String[] args) {
        System.out.println(calc(5, 1, 3));
        System.out.println(calc(2, 1, 3));
        System.out.println(calc(22, 22, 13));
    }

    /*
    1 blå -> 3 vita, 1 röd
    1 vit -> 3 blåa, 4 röda
    1 röd -> 2 blåa, 1 vit
     */
    public static int calc(int b, int w, int r) {
        var states = new LinkedList<State>();
        var next = new State(b, w, r, 0);

        while (!isSame(next) && next.count < 15) {
            states.offer(new State(next.b - 1, next.w + 3, next.r + 1, next.count + 1));
            states.offer(new State(next.b + 3, next.w - 1, next.r + 4, next.count + 1));
            states.offer(new State(next.b + 2, next.w + 1, next.r - 1, next.count + 1));
            next = states.poll();
        }
        return next.count;
    }

    private static boolean isSame(State a) {
        return a.b == a.w && a.w == a.r;
    }
}
