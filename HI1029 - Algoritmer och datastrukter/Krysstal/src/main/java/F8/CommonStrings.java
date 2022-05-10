package F8;

import java.util.*;

public class CommonStrings {
    public static void main(String[] args) {
        var commonStrings = new CommonStrings();

        var strings = new String[]{"Hej", "Tjena", "Hallå", "Hej", "Hej", "Tjena"};
        System.out.println("Most common string occured: " + commonStrings.count(strings));
    }

    public int count(String[] strings) {
        var hashMap = new HashMap<String, Integer>();
        for (var string : strings) {
            hashMap.put(string, hashMap.containsKey(string) ? hashMap.get(string) + 1 : 1);
        }
        return hashMap.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
    }
}
