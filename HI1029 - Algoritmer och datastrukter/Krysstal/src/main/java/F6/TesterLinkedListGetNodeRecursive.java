package F6;

public class TesterLinkedListGetNodeRecursive {
    public static void main(String[] args) {
        var list = new LinkedListGetNodeRecursive<Integer>();

        for (int i = 0; i < 10; i++) {
            list.add(i * 3);
        }

        System.out.println("Full list: " + list);

        for (int i = 0; i < list.size(); i++) {
            System.out.println("Element at index " + i + ": " + list.get(i));
        }
    }
}
