package com.example.sensor;

import java.time.LocalDateTime;

public class Measurement {
    enum Type {
        Accelerometer,
        Gyroscope
    }

    private final Type type;
    private final float[] values;
    private final LocalDateTime timestamp;
    private final float dt;

    public Measurement(Type type, float[] values, LocalDateTime timestamp, float dt) {
        this.type = type;
        this.values = values;
        this.timestamp = timestamp;
        this.dt = dt;
    }

    public Type getType() {
        return type;
    }

    public float[] getValues() {
        return values;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public float getDt() {
        return dt;
    }
}
