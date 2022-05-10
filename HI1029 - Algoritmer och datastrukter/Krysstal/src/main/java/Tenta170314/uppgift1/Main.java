package Tenta170314.uppgift1;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        SingleLinkedList<String> list = new SingleLinkedList<>();
        for (int i = 1; i <= 4; i++)//O(n2)
        {
            list.add("e" + i);
        }
        //uppgift 1a
        for(String d:list)//O(n)
        {
            System.out.println(d);
        }
        //uppgift 1b
        Iterator<String> iter = list.iterator(2);
        while(iter.hasNext())
            System.out.println(iter.next());
    }

}
