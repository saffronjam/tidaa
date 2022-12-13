package F2;

public class Tester {
    public static void main(String[] args) {

        // -------------------
        // IntArrayList
        // -------------------
        var myList = new IntArrayList(10);
        for (int i = 0; i < 10; i++) {
            myList.add(i);
        }
        System.out.println(myList);
        myList.remove(2);
        System.out.println(myList);
        myList.add(2);
        System.out.println(myList);
        myList.add(2);
        System.out.println(myList);
        System.out.println("size by size() : " + myList.size());

        System.out.println();

        // -------------------
        // Linked list
        // -------------------
        var nodeArray = new Node[4];
        for (int i = nodeArray.length - 1; i >= 0; i--) {
            if (i == nodeArray.length - 1) {
                nodeArray[i] = new Node(null);
            } else {
                nodeArray[i] = new Node(nodeArray[i + 1]);
            }
        }

        nodeArray[0].data = "Gilgamesh";
        nodeArray[1].data = "löper";
        nodeArray[2].data = "på";
        nodeArray[3].data = "stäppen";

        var formattedString = new StringBuilder();
        for (Node node = nodeArray[0]; node != null; node = node.next) {
            formattedString.append(node.data);
            if (node.next != null) {
                formattedString.append(" ---> ");
            }
        }
        System.out.println(formattedString);

    }
}
