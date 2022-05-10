package F5;

public class HighestValue {
    public static void main(String[] args) {
        var arr = new int[]{1, 2, 3, 4, 5, 6, 7};

        System.out.println("Highest value: " + highestValueRec(arr, arr.length));
    }

    public static int highestValueRec(int[] arr, int length) {
        if (length == 1) {
            return arr[0];
        }
        return Math.max(highestValueRec(arr, length - 1), arr[length - 1]);
    }
}
