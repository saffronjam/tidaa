package F14;

import java.util.Arrays;

public class UniquePaths {

    int size;

    public UniquePaths(int size) {
        this.size = size;
    }

    public int count() {
        // OBS! Detta sker topleft -> bottomright, då det var
        // enklare att tänka så med arrayer

        // En 2D-array som håller antal sätt för vägar att
        // gå i en nod. Den är 2D då vi har size*size koordinater
        var countingBuffer = new int[size][size];

        // Första kollumn och rad kommer alltid endast ha en väg till sig
        for (var row : countingBuffer) {
            row[0] = 1;
        }
        Arrays.fill(countingBuffer[0], 1);

        // Fyll upp med alla olika möjliga vägar (Detta är bottom-up delen)
        for (int i = 1; i < size; i++) {
            for (int j = 1; j < size; j++)
                countingBuffer[i][j] = countingBuffer[i - 1][j] + countingBuffer[i][j - 1];
        }
        return countingBuffer[size - 1][size - 1];

    }

}
