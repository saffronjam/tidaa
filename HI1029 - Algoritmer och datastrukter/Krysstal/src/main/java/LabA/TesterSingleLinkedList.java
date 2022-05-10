package LabA;

import java.util.Iterator;

public class TesterSingleLinkedList {
    public static void main(String[] args) {

//        System.out.println("\n ----- UPPG2 ----- ");
//        testUppg2();
        System.out.println("\n ----- UPPG3 ----- ");
        testUppg3();
    }

    private static void testUppg2() {
        var list = new SingleLinkedListUppg2<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }

        System.out.println("Before remove: " + list + " size: " + list.size());

        System.out.println("Removed: " + list.remove(3));
        System.out.println("Removed: " + list.remove(5));
        System.out.println("Removed: " + list.remove(0));
        System.out.println("Removed: " + list.remove(list.size() - 1));

        System.out.println("After remove: " + list + " size: " + list.size());

        list.removeLast();
        list.remove(list.size() - 1);
        list.remove(list.size() - 1);

        System.out.println("After remove last 3 times: " + list + " size: " + list.size());
    }

    private static void testUppg3() {
        var list = new SingleLinkedListUppg3<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }

        System.out.println("Before remove: " + list + " size: " + list.size());

        Iterator<Integer> itr = list.iterator();
        while (itr.hasNext()) {
            var next = itr.next();
            itr.remove();
        }

        System.out.println("After remove: " + list + " size: " + list.size());

        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }

        System.out.println("After add: " + list + " size: " + list.size());

        itr = list.iterator();
        while (itr.hasNext()) {
            var next = itr.next();
            if(next % 2 == 0)
            {
                itr.remove();
                break;
            }
        }

        System.out.println("After remove only even: " + list + " size: " + list.size());
    }
}
