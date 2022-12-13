package F1;

import java.util.ArrayList;

public class Programming2_1_1 {
    public static void main(String[] args) {

    }

    public static void replace(ArrayList<String> alist, String olditem, String newltem) {
        for (int i = 0; i < alist.size(); i++) {
            if (alist.get(i).equals(olditem)) {
                alist.set(i, newltem);
            }
        }
    }
}
