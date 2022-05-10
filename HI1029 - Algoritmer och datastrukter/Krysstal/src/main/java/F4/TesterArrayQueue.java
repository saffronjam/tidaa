package F4;

public class TesterArrayQueue {
    public static void main(String[] args) {
        var nameQ = new ArrayQueue<String>(10);

        for (int i = 0; i < 8; i++) {
            nameQ.offer("e" + (i + 1));
        }

        System.out.println("Polled: " + nameQ.poll());
        System.out.println("Polled: " + nameQ.poll());

        for (int i = 3; i < 5; i++) {
            nameQ.offer("e" + (i + 1));
        }

        System.out.println("Before remove: " + nameQ);

        for (int i = 0; i < 5; i++) {
            System.out.println("Polled: " + nameQ.poll());
        }

        System.out.println("After remove: " + nameQ);

        for (int i = 0; i < 100; i++) {
            nameQ.offer("e" + (i + 1));
        }
        System.out.println("Offered 100 elements (size: " + nameQ.size() + ", capacity: " + nameQ.capacity() + "): " + nameQ);
        for (int i = 0; i < 50; i++) {
            nameQ.poll();
        }
        System.out.println("Polled 50 elements (size: " + nameQ.size() + ", capacity: " + nameQ.capacity() + ")");
        for (int i = 0; i < 30; i++) {
            nameQ.poll();
        }
        System.out.println("Polled 30 more elements (size: " + nameQ.size() + ", capacity: " + nameQ.capacity() + ")");
        for (int i = 0; i < 100; i++) {
            nameQ.offer("e" + (i + 1));
        }
        System.out.println("Offered 100 elements again (size: " + nameQ.size() + ", capacity: " + nameQ.capacity() + ")");
    }
}
