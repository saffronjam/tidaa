package com.example.sensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Ägare: Anders Lindström anderslm@kth.se
// Ändrad av: Emil Karlsosn emilk2@kth.se
// Hämtat: 2021-12-12


public class ScanActivity extends AppCompatActivity {

    public static final String MOVESENSE = "Movesense";
    public static String SELECTED_DEVICE = "Selected device";
    private static final long SCAN_PERIOD = 5000; // milliseconds
    public static final int REQUEST_ENABLE_BT = 1000;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    private Handler worker;

    private ArrayList<BluetoothDevice> devices;
    private BtDeviceAdapter deviceAdapter;
    private TextView scanInfoTV;

    private static final String LOG_TAG = "ScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        devices = new ArrayList<>();

        worker = new Handler();

        scanInfoTV = findViewById(R.id.scan_info);

        Button startScanBtn = findViewById(R.id.startScanBtn);
        startScanBtn.setOnClickListener(v -> {
            devices.clear();
            scanForDevices(true);
        });

        RecyclerView deviceRV = findViewById(R.id.scanListRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        deviceRV.setLayoutManager(layoutManager);
        deviceAdapter = new BtDeviceAdapter(devices, this::onDeviceSelected);
        deviceRV.setAdapter(deviceAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        scanInfoTV.setText(R.string.no_devices_found);
        initBLE();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        scanForDevices(false);
        int size = devices.size();
        devices.clear();
        deviceAdapter.notifyItemRangeRemoved(0, size);
    }

    // Check BLE permissions and turn on BT (if turned off) - user interaction(s)
    private void initBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on BT
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void onDeviceSelected(int position) {
        BluetoothDevice selectedDevice = devices.get(position);

        // Send select bluetooth device back to MainActivity
        Intent intent = new Intent(ScanActivity.this, MainActivity.class);
        intent.putExtra(SELECTED_DEVICE, selectedDevice);
        startActivity(intent);
    }

    private void scanForDevices(final boolean enable) {
        final BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            if (!scanning) {
                // stop scanning after a pre-defined scan period, SCAN_PERIOD
                worker.postDelayed(() -> {
                    if (scanning) {
                        scanning = false;
                        scanner.stopScan(mScanCallback);
                        Log.i(LOG_TAG, "BLE scan stopped");
                    }
                }, SCAN_PERIOD);

                scanning = true;
                // TODO: Add a filter, e.g. for heart rate service, scan settings
                scanner.startScan(mScanCallback);
                scanInfoTV.setText(R.string.no_devices_found);
                Log.i(LOG_TAG, "BLE scan started");
            }
        } else {
            if (scanning) {
                scanning = false;
                scanner.stopScan(mScanCallback);
                Log.i(LOG_TAG, "BLE scan stopped");
            }
        }
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothDevice device = result.getDevice();

            worker.post(() -> {
                if (isMoveSenseDevice(device)) {
                    devices.add(device);
                    deviceAdapter.notifyItemChanged(devices.size() - 1);
                    String info = "Found " + devices.size() + " device " + (devices.size() > 1 ? "s" : "") + "\n"
                            + "Touch to connect";
                    scanInfoTV.setText(info);
                }
            });
        }

        private boolean isMoveSenseDevice(BluetoothDevice device) {
            return device.getName() != null
                    && device.getName().contains(MOVESENSE)
                    && !devices.contains(device);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(LOG_TAG, "onBatchScanResult");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(LOG_TAG, "onScanFailed");
        }
    };

    // callback for request to turn on BT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if user chooses not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}