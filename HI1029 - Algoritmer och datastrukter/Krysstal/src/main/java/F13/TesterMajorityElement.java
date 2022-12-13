package F13;

public class TesterMajorityElement {
    public static void main(String[] args) {
        // Lösning:
        // Hitta de mest förekommande elementet i arrayen
        // Titta sedan igenom en gång ifall detta fanns fler än n / 2 gånger
        // T(n) = O(n) + O( nlog(n) ) = O( nlog(n) )

        var myMajorityElement = new MajorityElement(new int[]{1, 2, 2, 1, 1});
        var result = myMajorityElement.find();

        if (result.hasCandidate()) {
            System.out.println("Value: " + result.value + " Count: " + result.count);
        } else {
            System.out.println("No majority element found");

        }
    }
}
