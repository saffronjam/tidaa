package LabA;

import java.util.Arrays;

public class RadixSort {

    public void sort(int[] arr) {
        // Det högsta värdet är viktigt för det avgör
        // den högsta möjliga expontent som sorteras
        int highest = highestValue(arr);
        int[][] outBuffers = new int[2][arr.length];
        for (int exponent = 1, i = 0; highest / exponent > 0; exponent *= 10, i++) {
            // Sortera för varje "tio-exponent"
            sort(arr, exponent, outBuffers[i % 2]);
        }
    }

    private void sort(int[] arr, int exponent, int[] out) {
        var counter = new int[10];

        // Räkna upp hur många av varje siffra vi har genom
        // att dela med exponent (som en shift till höger)
        // Modulus 10 körs för att filtrera fram ental-siffran
        for (var number : arr) {
            int index = (number / exponent) % 10;
            counter[index]++;
        }

        // Justera antalet av varje index i counter sådant att
        // varje index representerar den faktiskta platsen i resultat-arrayen
        // Ex:          [ 3, 2,  6,  4,  0,  3,  7,  1,  3 ]
        // Kommer bli:  [ 3, 5, 11, 15, 15, 18, 25, 26, 29 ]
        //
        // Alltså exempelvis: Alla tvåor måste börja placeras det index
        //                    sådant att alla ettor får plats innan
        for (int i = 1; i < 10; i++) {
            counter[i] += counter[i - 1];
        }

        // Placera över elementen i ordning (counting-sort)
        for (int i = arr.length - 1; i >= 0; i--) {
            int index = (arr[i] / exponent) % 10;
            out[--counter[index]] = arr[i];
        }

        // Kopiera resultatet av vår temporära resultat-array
        // till den faktiskta arrayen som ska sorteras
        System.arraycopy(out, 0, arr, 0, arr.length);
    }

    private int highestValue(int[] arr) {
        int highest = arr[0];
        for (var number : arr) {
            if (number > highest) {
                highest = number;
            }
        }
        return highest;
    }
}
