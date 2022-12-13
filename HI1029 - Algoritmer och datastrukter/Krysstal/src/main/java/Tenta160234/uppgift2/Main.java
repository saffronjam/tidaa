package Tenta160234.uppgift2;


public class Main {

	public static void buildTree(BinarySearchTree<String> b){
		b.add("H");
		b.add("C");
		b.add("I");
		b.add("E");
		b.add("D");
		b.add("F");
	}
	public static void main(String[] args) {

		BinarySearchTree<String> bst= new BinarySearchTree<String>();
		buildTree(bst);
		System.out.println(bst.minLeaf());  
	}

}
