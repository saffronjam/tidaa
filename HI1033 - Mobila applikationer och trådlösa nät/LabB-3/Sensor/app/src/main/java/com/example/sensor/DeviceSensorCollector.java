package com.example.sensor;

import static android.hardware.SensorManager.SENSOR_DELAY_UI;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DeviceSensorCollector extends SensorCollector {
    public DeviceSensorCollector(Context context) {
        super(context);
    }

    @Override
    public void start() {
        super.start();
        setupSensorListeners(this.context);
    }

    private void setupSensorListeners(Context context) {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        manager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = accFilter.filter(Arrays.copyOf(event.values, 3));
                if (measuring) {
                    addMeasurement(new Measurement(Measurement.Type.Accelerometer, Arrays.copyOf(values, values.length), LocalDateTime.now(), getDt()));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, accelerometer, SENSOR_DELAY_UI);

        manager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = gyroFilter.filter(event.values);
                if (measuring) {
                    addMeasurement(new Measurement(Measurement.Type.Gyroscope, Arrays.copyOf(values, values.length), LocalDateTime.now(), getDt()));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, gyroscope, SENSOR_DELAY_UI);
    }

    private float getDt() {
        return 1.0f / 17.0f;
    }
}
