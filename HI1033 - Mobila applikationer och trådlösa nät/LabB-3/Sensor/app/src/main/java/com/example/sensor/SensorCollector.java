package com.example.sensor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SensorCollector {

    enum MeasureType {
        Accelerometer,
        Gyroscope,
        IMU6
    }

    enum SamplerFrequency {
        Hz13,
        Hz26,
        Hz52
    }

    protected MeasureType measureType;
    protected SamplerFrequency frequency;

    protected MeasurementListener measurementListener;
    private TimeEndListener timeEndListener;

    public interface MeasurementListener {
        void onMeasurement(Measurement measurement);
    }

    public interface TimeEndListener {
        void onStop();
    }

    protected final SimpleFilter accFilter = new SimpleFilter();
    protected final SimpleFilter gyroFilter = new SimpleFilter();

    protected boolean measuring = false;

    private final ArrayList<Measurement> accData = new ArrayList<>();
    private final ArrayList<Measurement> gyroData = new ArrayList<>();

    private Handler measureDelayHandler;
    private Runnable measureDelayRunnable;

    protected final Context context;

    public static final int MEASURE_TIMELIMIT = 10000;

    public SensorCollector(Context context) {
        this.context = context;
    }

    public void start() {
        // Start timer to prevent more than 10 sec
        this.measureDelayHandler = new Handler(Looper.getMainLooper());
        this.measureDelayRunnable = () -> {
            stop();
            if (timeEndListener != null) {
                timeEndListener.onStop();
            }
        };
        this.measureDelayHandler.postDelayed(this.measureDelayRunnable, MEASURE_TIMELIMIT);
        measuring = true;
    }

    public void stop() {
        if (measureDelayHandler != null) {
            this.measureDelayHandler.removeCallbacks(this.measureDelayRunnable);
        }
        this.measureDelayRunnable = null;
        measuring = false;
    }

    public void reset() {
        accData.clear();
        gyroData.clear();
    }

    public void setOnMeasurementListener(MeasurementListener listener) {
        this.measurementListener = listener;
    }

    public void setOnTimerEndListener(TimeEndListener listener) {
        this.timeEndListener = listener;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public void setFrequency(SamplerFrequency frequency) {
        this.frequency = frequency;
    }

    public void save() {
        LocalDateTime now = LocalDateTime.now();
        String firstMethodFilename = "firstmethod:" + now.getHour() + ':' + now.getMinute();
        String secondMethdFilename = "secondmethod:" + now.getHour() + ':' + now.getMinute();

        final ArrayList<DataPoint> data = new ArrayList<>();

        // Prepare datapoint from measurements
        float oldComPitch = 0;
        for (int i = 0; i < Math.min(this.accData.size(), this.gyroData.size()); i++) {
            boolean wasNull = false;
            if (this.accData.get(i) == null) {
                Log.e("INVALID STATE", "Acc element " + i + " was null");
                wasNull = true;
            }
            if (this.gyroData.get(i) == null) {
                Log.e("INVALID STATE", "Gyro element " + i + " was null");
                wasNull = true;
            }
            if (wasNull) {
                continue;
            }
            DataPoint dataPoint = new DataPoint(this.accData.get(i), this.gyroData.get(i), oldComPitch, this.accData.get(i).getDt());
            oldComPitch = dataPoint.getComPitch();
            data.add(dataPoint);
        }

        // First method, only accelerometer
        StringBuilder builder = new StringBuilder();
        for (DataPoint point : data) {
            builder.append((int) point.getAccPitch()).append(' ').append(point.getTimestamp()).append('\n');
        }
        Utils.writeToFile(this.context, firstMethodFilename, builder.toString());

        // Second method, combintation
        builder = new StringBuilder();
        for (DataPoint point : data) {
            builder.append((int) point.getComPitch()).append(' ').append(point.getTimestamp()).append('\n');
        }
        Utils.writeToFile(this.context, secondMethdFilename, builder.toString());
    }

    public void addMeasurement(Measurement measurement) {
        switch (measurement.getType()) {
            case Accelerometer:
                if (measureType == MeasureType.Accelerometer || measureType == MeasureType.IMU6) {
                    if (measuring) {
                        accData.add(measurement);
                    }
                    measurementListener.onMeasurement(measurement);
                }
                break;
            case Gyroscope:
                if (measureType == MeasureType.Gyroscope || measureType == MeasureType.IMU6) {
                    if (measuring) {
                        gyroData.add(measurement);
                    }
                    measurementListener.onMeasurement(measurement);
                }
                break;
        }
    }


}
