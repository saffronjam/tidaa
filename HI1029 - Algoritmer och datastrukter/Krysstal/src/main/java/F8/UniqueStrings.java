package F8;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class UniqueStrings {
    public static void main(String[] args) {
        var uniqueStrings = new UniqueStrings();

        var strings = new String[]{"Hej", "Tjena", "Hallå", "Hej", "Hej", "Tjena", "Hej där"};
        System.out.println("Number of unique strings: " + uniqueStrings.count(strings));
    }

    public int count(String[] strings) {
        return new HashSet<>(Arrays.asList(strings)).size();
    }

}
