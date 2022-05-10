import java.util.Arrays;
import java.util.Collections;

public class MainTesting {
    public static void main(String[] args) {
        var words = new ClosestWords("laab", Collections.singletonList("blad"));
        System.out.println(words.closestDistance);
    }
}
