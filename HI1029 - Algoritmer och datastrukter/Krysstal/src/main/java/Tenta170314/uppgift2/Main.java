
package Tenta170314.uppgift2;


public class Main {

    public static void buildTree(BinarySearchTree<String> b) {
        b.add("H");
        b.add("B");
        b.add("N");
        b.add("A");
        b.add("E");
        b.add("C");
        b.add("F");
        b.add("D");
        b.add("G");
        b.add("J");
        b.add("I");
    }

    public static void main(String[] args) {

        BinarySearchTree<String> bst = new BinarySearchTree<>();
        buildTree(bst);
        bst.printTree();
        System.out.println(bst.noSingleParents());
    }

}
