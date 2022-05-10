package F13;

public class MaxSequence {
    private int noCalls = 0;
    private final int[] numbers;

    public MaxSequence(int[] numbers) {
        this.numbers = numbers;
    }

    public int calc() {
        noCalls = 0;
        return calc(0, numbers.length - 1);
    }

    private int calc(int left, int right) {
        noCalls++;
        if (left == right) return Math.max(0, numbers[left]);

        int center = (left + right) / 2;

        int maxLower = largestAdditiveSumBackward(numbers, left, center);
        int maxUpper = largestAdditiveSumForward(numbers, center + 1, right);
        int maxResult = maxLower + maxUpper;

        int recursiveLeft = calc(left, center);
        int recursiveRight = calc(center + 1, right);

        return max(maxResult, recursiveLeft, recursiveRight);
    }

    private static int largestAdditiveSumForward(int[] numbers, int start, int end) {
        int sum = 0, max = 0;
        for (int i = start; i <= end; i++) {
            sum += numbers[i];
            max = Math.max(max, sum);
        }
        return max;
    }

    public int getNoCalls() {
        return noCalls;
    }

    private static int largestAdditiveSumBackward(int[] numbers, int start, int end) {
        int sum = 0, max = 0;
        for (int i = end; i >= 0; i--) {
            sum += numbers[i];
            max = Math.max(max, sum);
        }
        return max;
    }

    private static int max(int first, int second, int third) {
        return Math.max(first, Math.max(second, third));
    }
}
