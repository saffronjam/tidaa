package F4;

public class TesterLinkedQueue {

    public static void main(String[] args) {
        var linkedQueue = new LinkedQueue<Integer>();

        for (int i = 0; i < 25; i++) {
            linkedQueue.enqueue(i + 1);
        }

        System.out.println("Ennqueued 25 elements: " + linkedQueue);

        for (int i = 0; i < 20; i++) {
            linkedQueue.dequeue();
        }
        System.out.println("Dequeued 20 elements: " + linkedQueue);
        System.out.println("Dequeued one more: " + linkedQueue.dequeue());
        System.out.println("Dequeued one more: " + linkedQueue.dequeue());
        System.out.println("Dequeued one more: " + linkedQueue.dequeue());
        System.out.println("After more deques: " + linkedQueue);

        try {
            System.out.println("Attempting dequeing too many elements, will it throw?");
            for(int i = 0;i < 100; i++)
            {
                linkedQueue.dequeue();
            }
        } catch (IllegalStateException ise) {
            System.out.println("Exception thrown: " + ise.getMessage());
        }

        System.out.println("Adding 5 elements after being empty... Then removing them...");
        for(int i =0 ;i < 5; i++)
        {
            linkedQueue.enqueue(i * 3);
        }
        for(int i =0 ;i < 5; i++)
        {
            linkedQueue.dequeue();
        }
        System.out.println("Result: " + linkedQueue);
        linkedQueue.enqueue(100);
        System.out.println("Enqueued one more, result: " + linkedQueue);


    }


}
