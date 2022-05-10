package Labb1;

import java.io.*;

public class WorstCaseFinder {
    static String alphas = " abcdefghijklmnopqrstuvwxyzåäö";

    public static void main(String[] args) {

        var preHashes = new int[27_930];


        try (var input = new DataInputStream(new FileInputStream(Settings.PrehashWordData))) {

            for (int i = 0; i < 27_930; i++) {
                preHashes[i] = input.readInt();
            }

            int currentMax = 0;
            int currentMaxIndex = -1;
            int lastValidIndex = 0;
            for (int i = 1; i < preHashes.length; i++) {
                if (preHashes[i] != -1) {
                    lastValidIndex = i;
                    break;
                }
            }

            for (int i = 1; i < preHashes.length; i++) {
                if (preHashes[i] != -1) {
                    int diff = preHashes[i] - preHashes[lastValidIndex];
                    if (diff > currentMax) {
                        currentMax = diff;
                        currentMaxIndex = lastValidIndex;
                    }
                    lastValidIndex = i;
                }
            }
            int x = preHashes[26999];

            System.out.println(currentMaxIndex);

            System.out.println(preHashes[Utils.lazyManHash("söö")]);
            System.out.println(findChars());

            int sum = 0;
            for (int i = 3; i >= 0 ; i--) {
                sum += Math.pow(30, i) * 30;
            }
            System.out.println("Sum: " + sum);
        } catch (FileNotFoundException ignored) {
            System.out.println("Error");
        } catch (IOException ignored) {
            System.out.println("Error");
        }
    }
    public static char[] findChars() {
        for (int i = 0; i < alphas.length(); i++) {
            for (int j = 0; j < alphas.length(); j++) {
                for (int k = 0; k < alphas.length(); k++) {
                    char[] arr = {alphas.charAt(i), alphas.charAt(j), alphas.charAt(k)};
                    String str = String.copyValueOf(arr);
                    if (Utils.lazyManHash(str) == 17_999)
                        return arr;
                }
            }
        }
        return null;
    }
}
