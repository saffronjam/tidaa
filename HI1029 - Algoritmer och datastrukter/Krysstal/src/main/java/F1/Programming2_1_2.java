package F1;

import java.util.ArrayList;

public class Programming2_1_2 {
    public static void main(String[] args) {

    }

    public static void delete(ArrayList<String> alist, String target) {
        for (int i = 0; i < alist.size(); i++) {
            if (alist.get(i).equals(target)) {
                alist.remove(i);
                break;
            }
        }
    }
}
