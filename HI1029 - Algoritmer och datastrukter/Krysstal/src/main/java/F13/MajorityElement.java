package F13;

public class MajorityElement {

    public static class Result {
        int value, count;

        public Result(int value, int count) {
            this.value = value;
            this.count = count;
        }

        public boolean hasCandidate() {
            return count > 0;
        }

        public static Result Empty = new Result(-1, 0);
    }

    private final int[] arr;

    public MajorityElement(int[] arr) {
        this.arr = arr;
    }

    public Result find() {
        return find(0, arr.length - 1);
    }

    private Result find(int left, int right) {
        if (left == right) return new Result(arr[left], 1);

        int center = left + (right - left) / 2;
        int overHalf = (right - left + 1) / 2;
        var lower = find(left, center);
        var upper = find(center + 1, right);

        // Om vi har inkommande majority från lower, men inte från höger
        lower.count += count(lower.value, center + 1, right);
        if (lower.hasCandidate() && !upper.hasCandidate()) {
            return lower.count > overHalf ? lower : Result.Empty;
        }

        // Om vi har inkommande majority från lower, men inte från höger
        upper.count += count(upper.value, left, center);
        if (upper.hasCandidate() && !lower.hasCandidate()) {
            return upper.count > overHalf ? upper : Result.Empty;
        }

        // Om vi har inkommande majority från både höger och vänster
        if (lower.count > overHalf) {
            return lower;
        }
        if (upper.count > overHalf) {
            return upper;
        }

        // Sist är om vi inte hade majority på någon
        return Result.Empty;
    }

    private int count(int value, int left, int right) {
        int count = 0;
        for (int i = left; i <= right; i++) {
            if (arr[i] == value) count++;
        }
        return count;
    }
}
