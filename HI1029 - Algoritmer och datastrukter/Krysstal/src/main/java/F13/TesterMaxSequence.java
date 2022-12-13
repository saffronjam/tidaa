package F13;

public class TesterMaxSequence {
    public static void main(String[] args) {
        var numbers = new int[]{1, 2, -5, 6, 7, -3, 4};
        var myMaxSequence = new MaxSequence(numbers);
        var result = myMaxSequence.calc();
        System.out.println("Best sequence: " + result);
        System.out.println("No calls: " + myMaxSequence.getNoCalls());
    }
}
