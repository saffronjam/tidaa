package F4;

import java.util.EmptyStackException;

public class TesterLinkedDequeue {
    public static void main(String[] args) {
        var linkedDequeue = new LinkedDequeue<Integer>();

        for (int i = 0; i < 15; i++) {
            linkedDequeue.offerFirst(i + 1);
        }
        System.out.println("Offer-first 10 elements (size: " + linkedDequeue.size() + "): " + linkedDequeue);

        for (int i = 0; i < 5; i++) {
            linkedDequeue.pollFirst();
        }
        System.out.println("Poll-first 5 elements (size: " + linkedDequeue.size() + "): " + linkedDequeue);
        for (int i = 0; i < 5; i++) {
            linkedDequeue.pollLast();
        }
        System.out.println("Poll-last 5 elements (size: " + linkedDequeue.size() + "): " + linkedDequeue);

        for (int i = 0; i < 5; i++) {
            linkedDequeue.pollLast();
        }
        System.out.println("Poll-last the last 5 elements (size: " + linkedDequeue.size() + "): " + linkedDequeue);

        try {
            System.out.println("Trying to poll one more element, will it throw?");
            linkedDequeue.pollLast();
        } catch (IllegalStateException ise) {
            System.out.println("Exception! " + ise.getMessage());
        }

        System.out.println("Refilling again with 3 elements");
        linkedDequeue.offerLast(100);
        linkedDequeue.offerLast(100);
        linkedDequeue.offerFirst(200);

        System.out.println("Final (size: " + linkedDequeue.size() + "): " + linkedDequeue);


    }
}
