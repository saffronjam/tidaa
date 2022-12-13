package com.example.sensor;

import static java.lang.Math.atan;
import static java.lang.Math.sqrt;

import java.time.LocalDateTime;

public class DataPoint {
    private final float accPitch;
    private final float comPitch;
    private final LocalDateTime timestamp;
    public static final float RAD_TO_DEG = 57.2957795130f;
    public static final float DEG_TO_RAD = 1.0f / RAD_TO_DEG;
    private static final float filterFactor = 0.6f;

    public DataPoint(Measurement accData, Measurement gyroData, float comPitchOld, float dt) {
        this.timestamp = accData.getTimestamp();

        // accPitch
        float ax = ((float) accData.getValues()[0]);
        float ay = ((float) accData.getValues()[1]);
        float az = ((float) accData.getValues()[2]);
        this.accPitch = -1.0f * RAD_TO_DEG * (float) ((atan(ay / sqrt(ax * ax + az * az))));

        // comPitch
        this.comPitch = (1.0f - filterFactor)
                * (comPitchOld + dt * RAD_TO_DEG * gyroData.getValues()[1])
                + filterFactor * this.accPitch;
    }

    public float getComPitch() {
        return comPitch;
    }

    public float getAccPitch() {
        return accPitch;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
