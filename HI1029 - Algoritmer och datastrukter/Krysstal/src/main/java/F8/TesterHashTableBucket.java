package F8;

public class TesterHashTableBucket {
    public static void main(String[] args) {
        var hashTableBucket = new HashTableBucket<String, String>(10);

        System.out.println("Empty table: \n" + hashTableBucket);

        for (int i = 0; i < 10; i++) {
            hashTableBucket.put("KEY" + i * 2, "VALUE" + i * 5);
        }

        System.out.println();

        System.out.println("After 10 elements: ");
        System.out.println(hashTableBucket);

        for (int i = 0; i < 5; i++) {
            var key = "KEY" + i * 2;
            System.out.println("Element with key=" + key + " has value=" + hashTableBucket.get(key));
        }

        System.out.println();

        System.out.println("Removing KEY0 and KEY4: ");
        hashTableBucket.remove("KEY0");
        hashTableBucket.remove("KEY4");
        System.out.println(hashTableBucket);

        System.out.println();

        System.out.println("Putting new value for KEY2 to NEWVAL: ");
        hashTableBucket.put("KEY2", "NEWVAL");
        System.out.println(hashTableBucket);

        System.out.println("Adding new values to cleaer dummys: ");
        for(int i = 0; i < 50; i++)
        {
            hashTableBucket.put("KEY" + i * 2, "VALUE" + i * 5);
        }
        System.out.println(hashTableBucket);

        System.out.println("Finally deleting two keys that are in the same bucket: ");
        hashTableBucket.remove("KEY18");
        hashTableBucket.remove("KEY90");
        System.out.println(hashTableBucket);
    }
}
