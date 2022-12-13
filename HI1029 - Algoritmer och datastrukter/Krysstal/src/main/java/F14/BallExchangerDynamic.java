package F14;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class BallExchangerDynamic {
    static class State {
        int b, w, r;

        public State(int b, int w, int r) {
            this.b = b;
            this.w = w;
            this.r = r;
        }

        public boolean equilibrium() {
            return b == w && w == r;
        }

        public static State fromDiff(State state, int b, int w, int r) {
            return new State(state.b + b, state.w + w, state.r + r);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            State state = (State) object;
            return b == state.b &&
                    w == state.w &&
                    r == state.r;
        }

        @Override
        public int hashCode() {
            return Objects.hash(b, w, r);
        }
    }

    final int maxDepth = 15;
    int calls = 0;

    public int exchange(State start) {
        calls = 0;
        return exchange(start, 0, new HashMap<>());
    }


    /*
    1 blå -> 3 vita, 1 röd
    1 vit -> 3 blåa, 4 röda
    1 röd -> 2 blåa, 1 vit
     */
    private int exchange(State state, int cost, HashMap<Integer, Integer> hashMap) {
        calls++;
        if (cost >= maxDepth) {
            return Integer.MAX_VALUE / 2;
        }
        if (state.equilibrium()) {
            return cost;
        }

        int tryB = state.b > 0 ? continueWithHashMap(State.fromDiff(state, -1, +3, +1), cost + 1, hashMap) : Integer.MAX_VALUE / 2;
        int tryW = state.w > 0 ? continueWithHashMap(State.fromDiff(state, +3, -1, +4), cost + 1, hashMap) : Integer.MAX_VALUE / 2;
        int tryR = state.r > 0 ? continueWithHashMap(State.fromDiff(state, +2, +1, -1), cost + 1, hashMap) : Integer.MAX_VALUE / 2;

        return min(tryB, tryW, tryR);
    }

    private int continueWithHashMap(State state, int cost, HashMap<Integer, Integer> hashMap) {
        var hash = state.hashCode();
        if (hashMap.containsKey(hash)) {
            var current = hashMap.get(hash);
            if (cost >= current) {
                return Integer.MAX_VALUE / 2;
            }
        }
        hashMap.put(hash, cost);
        return exchange(state, cost, hashMap);
    }

    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public int getCalls() {
        return calls;
    }
}
