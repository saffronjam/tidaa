/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
    LinkedList<String> closestWords = null;

    final HashMap<Integer, int[][]> savedMatrices = new HashMap<>();


    int closestDistance = -1;

    public ClosestWords(String w, List<String> wordList) {

        String lastword = "";
        int[][] prevMat = null;
        for (String s : wordList) {

            if(Math.abs(s.length() - w.length()) > closestDistance && closestDistance != -1)
            {
                continue;
            }

            int p = commonLetters(s, lastword);
            var dynProgMat = getDynProgMat(w.length(), s.length());
            if (prevMat != null && p > 0) {
                for (int row = 0; row < p; row++) {
                    dynProgMat[row] = Arrays.copyOfRange(prevMat[row], 0, prevMat.length);
                }
            }



            for(int row = 1; row < s.length() + 1; row++)
            {
                for(int col = 1; col < w.length() + 1; col++)
                {
                    var replace = dynProgMat[row - 1][col - 1] ;
                    if(s.charAt(row-1) != w.charAt(col-1))
                        replace++;

                    var add = dynProgMat[row - 1][col] + 1;
                    var delete = dynProgMat[row][col - 1] + 1;

                    dynProgMat[row][col] = Math.min(Math.min(replace, add), delete );
                }
            }
            /**
             *      replace = M[row - 1][col-1]
             * 		if w1[row-1] != w2[col-1] then replace += 1
             * 		add = M[row-1][col] + 1
             * 		delete = M[row][col-1] + 1
             * 		M[row][col] = min(replace, add, delete)
             */

            int dist = dynProgMat[s.length()][w.length()];


            // System.out.println("d(" + w + "," + s + ")=" + dist);
            if (dist < closestDistance || closestDistance == -1) {
                closestDistance = dist;
                closestWords = new LinkedList<String>();
                closestWords.add(s);
            } else if (dist == closestDistance)
                closestWords.add(s);

            prevMat = dynProgMat;
        }
    }

    private int commonLetters(String s, String lastword) {
        int count = 0;
        for (int i = 0; i < s.length() && i < lastword.length(); i++) {
            if (s.charAt(i) == lastword.charAt(i))
                count++;
        }
        return count;
    }


    int[][] getDynProgMat(int wLen, int sLen) {

        if(savedMatrices.containsKey(sLen))
        {
            return savedMatrices.get(sLen);
        }
        var dynProgMat = new int[sLen + 1][wLen + 1];
        for(int i = 0; i < dynProgMat[0].length; i++) {
            dynProgMat[0][i] = i;
        }

        for(int i = 0; i < dynProgMat.length; i++) {
            dynProgMat[i][0] = i;
        }
        return dynProgMat;
    }


    int partDist(String w1, String w2, int w1len, int w2len) {



        if (w1len == 0)
            return w2len;
        if (w2len == 0)
            return w1len;
        int res = partDist(w1, w2, w1len - 1, w2len - 1) +
                (w1.charAt(w1len - 1) == w2.charAt(w2len - 1) ? 0 : 1);
        int addLetter = partDist(w1, w2, w1len - 1, w2len) + 1;
        if (addLetter < res)
            res = addLetter;
        int deleteLetter = partDist(w1, w2, w1len, w2len - 1) + 1;
        if (deleteLetter < res)
            res = deleteLetter;

        return res;
    }

    int distance(String w1, String w2, int[][] dynProgMat) {
        return partDist(w1, w2, w1.length(), w2.length());
    }


    int getMinDistance() {
        return closestDistance;
    }

    List<String> getClosestWords() {
        return closestWords;
    }
}
