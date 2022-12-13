package F12;

import java.util.Arrays;

public class Exchanger {

    public static void main(String[] args) {
        var myExchanger = new Exchanger(new int[]{1000, 500, 100, 50, 20, 10, 5, 1});
        System.out.println("Exchaning 789: " + Arrays.toString(myExchanger.exchange(789)));
        System.out.println("Exchaning 5131: " + Arrays.toString(myExchanger.exchange(5131)));
        System.out.println("Exchaning 21: " + Arrays.toString(myExchanger.exchange(21)));
        System.out.println("Exchaning 6: " + Arrays.toString(myExchanger.exchange(6)));
    }

    private final int[] change;

    public Exchanger(int[] change) {
        this.change = change;
    }

    public int[] exchange(int amount) {
        var result = new int[change.length];
        for (int i = 0; i < change.length; i++) {
            while (change[i] <= amount) {
                result[i]++;
                amount -= change[i];
            }
        }
        return result;
    }
}
