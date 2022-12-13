package Tenta160234.uppgift1;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SingleLinkedList<String> list = new SingleLinkedList<String>();

        for (int i = 0; i < 10; i++)
            list.add(4, "Sträng " + i);

        for (int i = 0; i < 10; i++)
            System.out.println(list.get(i));

        System.out.println("Storlek rekursivt: " + list.size());
    }

}