package LabA;

public class TesterBSTNextLarger {
    public static void main(String[] args) {
        var bst = new BSTNextLarger<Integer>();

//        for (int i = 0; i < 10; i++) {
//            bst.add(i);
//        }

        bst.add(12);
        bst.add(5);
        bst.add(1);
        bst.add(8);
        bst.add(2);

        for (int i = 0; i < 15; i++) {
            System.out.println("Closest bigger than " + i + ": " + bst.getNextLarger(i));
        }
    }
}
