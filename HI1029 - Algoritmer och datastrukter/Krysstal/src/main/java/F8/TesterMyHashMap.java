package F8;

public class TesterMyHashMap {
    public static void main(String[] args) {
        var myHashMap = new MyHashMap<String, String>();

        System.out.println("Empty table: \n" + myHashMap);

        for (int i = 0; i < 10; i++) {
            myHashMap.put("KEY" + i * 2, "VALUE" + i * 5);
        }

        System.out.println();

        System.out.println("After 10 elements: ");
        System.out.println(myHashMap);

        for (int i = 0; i < 5; i++) {
            var key = "KEY" + i * 2;
            System.out.println("Element with key=" + key + " has value=" + myHashMap.get(key));
        }

        System.out.println();

        System.out.println("Removing KEY0 and KEY4: ");
        myHashMap.remove("KEY0");
        myHashMap.remove("KEY4");
        System.out.println(myHashMap);

        System.out.println();

        System.out.println("Putting new value for KEY2 to NEWVAL: ");
        myHashMap.put("KEY2", "NEWVAL");
        System.out.println(myHashMap);

        System.out.println("Adding new values to cleaer dummys: ");
        for(int i = 0; i < 50; i++)
        {
            myHashMap.put("KEY" + i * 2, "VALUE" + i * 5);
        }
        System.out.println(myHashMap);
    }
}
