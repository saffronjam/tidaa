package com.example.sensor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    public static final String LOG_TAG = "MainActivity";

    private final DeviceSensorCollector deviceCollector = new DeviceSensorCollector(MainActivity.this);
    private final BtSensorCollector btCollector = new BtSensorCollector(MainActivity.this);
    private SensorCollector currentCollector;

    // UI
    private Button actionBtn;
    private Button scanBtn;
    private ImageButton settingsBtn;
    private TextView selectedDeviceNameTv;

    private boolean measuring = false;
    private boolean usingDevice = false;
    private Handler handler = new Handler();

    private static final int ALL_PERMISSIONS = 100;

    // Chart
    private LineChart chart;
    private LineData chartData = new LineData();

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedDeviceNameTv = findViewById(R.id.selectedDeviceName);

        settingsBtn = findViewById(R.id.settingsBtn);
        actionBtn = findViewById(R.id.actionBtn);
        actionBtn.setOnClickListener(v -> onAction());
        scanBtn = findViewById(R.id.scanBtn);

        chart = findViewById(R.id.chart1);
        setupChart();


        settingsBtn.setOnClickListener(e -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        scanBtn.setOnClickListener(e -> {
            if (Utils.checkPermissionState(MainActivity.this, Manifest.permission.BLUETOOTH, ALL_PERMISSIONS) &&
                    Utils.checkPermissionState(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN, ALL_PERMISSIONS) &&
                    Utils.checkPermissionState(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, ALL_PERMISSIONS) &&
                    Utils.checkPermissionState(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, ALL_PERMISSIONS) &&
                    Utils.checkPermissionState(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN, ALL_PERMISSIONS) &&
                    Utils.checkPermissionState(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT, ALL_PERMISSIONS)) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        btCollector.setDeviceConnectionListener(connected -> {
            handler.post(this::syncUi);
        });

        btCollector.setOnTimerEndListener(() -> {
            handler.post(() -> {
                onSave();
                currentCollector.reset();
                measuring = false;
                syncUi();
            });
        });
        deviceCollector.setOnTimerEndListener(() -> {
            handler.post(() -> {
                onSave();
                currentCollector.reset();
                measuring = false;
                syncUi();
            });
        });

        btCollector.setOnMeasurementListener((measurement) -> handler.post(() -> onMeasurement(measurement)));
        deviceCollector.setOnMeasurementListener((measurement) -> handler.post(() -> onMeasurement(measurement)));

        setCollector(usingDevice ? deviceCollector : btCollector);

        syncUi();
        syncSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Parcelable selectedDevice = intent.getParcelableExtra(ScanActivity.SELECTED_DEVICE);
        if (selectedDevice != null) {
            btCollector.setDevice((BluetoothDevice) selectedDevice);
        }
        btCollector.connectDevice();
        setupChart();
        syncUi();
        syncSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopMeasurement(false);
        btCollector.disconnectDevice();
        chart.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ALL_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (hasScanPermissions()) {
                    new Thread(() -> {
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(intent);
                    }).start();
                }
            } else {
                Toast.makeText(MainActivity.this, "Access Denied ! Cannot proceed further ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onAction() {
        if (!measuring) {
            onStartMeasurement();
        } else {
            onStopMeasurement();
        }
        syncUi();
    }

    private void onStartMeasurement() {
        currentCollector.start();
        measuring = true;
    }

    private void onStopMeasurement() {
        onStopMeasurement(true);
    }

    private void onStopMeasurement(boolean withSave) {
        currentCollector.stop();
        if (withSave) {
            onSave();
        }
        currentCollector.reset();
        measuring = false;
    }

    private void onMeasurement(Measurement measurement) {
        if (measurement.getType() == Measurement.Type.Accelerometer) {
            float[] old = measurement.getValues();
            float[] values = new float[]{old[0] * 10.0f, old[1] * 10.0f, old[2] * 10.0f};
            addChartEntry(values);
        }
        syncUi();
    }

    private void onSave() {
        currentCollector.save();
        Toast.makeText(this, "Saved data", Toast.LENGTH_SHORT).show();
    }

    private void setCollector(SensorCollector collector) {
        currentCollector = collector;
    }


    private boolean hasScanPermissions() {
        boolean basicTypes = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            basicTypes = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }

        return basicTypes &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void syncUi() {
        scanBtn.setEnabled(!usingDevice);

        if (usingDevice) {
            selectedDeviceNameTv.setText(R.string.using_device_sensors);
        } else if (btCollector.isConnected()) {
            selectedDeviceNameTv.setText(btCollector.getDeviceName());
        } else {
            selectedDeviceNameTv.setText(R.string.no_device_selected);
        }

        if (measuring) {
            actionBtn.setText(R.string.stop_measuring);
        } else {
            actionBtn.setText(R.string.start_measuring);
        }
    }

    private void syncSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sensorString = sharedPref.getString("sensor", "IMU6");
        if (sensorString != null) {
            currentCollector.setMeasureType(SensorCollector.MeasureType.valueOf(sensorString));
        }

        String freqString = sharedPref.getString("freq", "Hz13");
        if (freqString != null) {
            currentCollector.setFrequency(SensorCollector.SamplerFrequency.valueOf(freqString));
        }
    }

    private void setupChart() {
        int textColor = Color.WHITE;

        LineData data = new LineData();

        chart.setOnChartValueSelectedListener(this);

        // add empty data
        chart.setData(data);
        chart.setDrawGridBackground(false);

        Description desc = new Description();
        desc.setText("Accelerometer");
        chart.setDescription(desc);

        Legend l = chart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(textColor);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(textColor);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setAxisMaximum(700.0f);
        leftAxis.setAxisMinimum(-700.0f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(true);

        data.addDataSet(createSet("X", Color.RED));
        data.addDataSet(createSet("Y", Color.GREEN));
        data.addDataSet(createSet("Z", Color.BLUE));
    }

    private void addChartEntry(float[] values) {

        LineData data = chart.getData();

        if (data != null) {
            for (int i = 0; i < 3; i++) {
                ILineDataSet accSet = data.getDataSetByIndex(i);
                data.addEntry(new Entry(accSet.getEntryCount(), values[i]), i);
            }
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(200);
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet(String name, int color) {
        int textColor = Color.WHITE;

        LineDataSet set = new LineDataSet(null, name);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(2);
        set.setFillColor(color);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setValueTextColor(textColor);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}