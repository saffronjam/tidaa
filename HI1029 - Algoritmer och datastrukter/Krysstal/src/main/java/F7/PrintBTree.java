package F7;

import java.util.Random;

public class PrintBTree {
    public static void main(String[] args) {
        var bst = new BST<Integer>();

        var random = new Random();
        for (int i = 0; i < 10; i++) {
            bst.add(random.nextInt(10));
        }

        bst.printTree();

        System.out.println();

        System.out.println("No leaves: " + bst.numberOfLeaves());
        System.out.println("No nodes: " + bst.numberOfNodes());
        System.out.println("Height: " + bst.height());
        System.out.println("Max It: " + bst.maxIt());
        System.out.println("Max Rec: " + bst.maxRec());

        System.out.println();

        for (int i = 0; i < 10; i++) {
            var result = bst.find(i);
            System.out.println(result == null ? "Did not find " + i : "Found element: " + i + "  (returned: " + result + ")");
        }

    }
}
