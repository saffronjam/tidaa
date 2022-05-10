package com.example.sensor;

public class SimpleFilter {
    private float[] currentValues = new float[3];
    private float[] previousValues = new float[3];
    private static final float filterFactor = 0.9f;

    public float[] filter(float[] sensorValues) {
        for (int i = 0; i < sensorValues.length; i++) {
            currentValues[i] = filterFactor * previousValues[i] + (1.0F - filterFactor) * sensorValues[i];
            previousValues[i] = currentValues[i];
        }
        return currentValues;
    }

    public float[] getValues() {
        return currentValues;
    }
}
