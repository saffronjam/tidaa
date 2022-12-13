package com.example.sensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BtSensorCollector extends SensorCollector {
    // Movesense 2.0 UUIDs (should be placed in resources file)
    public static final UUID MOVESENSE_2_0_SERVICE = UUID.fromString("34802252-7185-4d5d-b431-630e7050e8f0");
    public static final UUID MOVESENSE_2_0_COMMAND_CHARACTERISTIC = UUID.fromString("34800001-7185-4d5d-b431-630e7050e8f0");
    public static final UUID MOVESENSE_2_0_DATA_CHARACTERISTIC = UUID.fromString("34800002-7185-4d5d-b431-630e7050e8f0");
    // UUID for the client characteristic, which is necessary for notifications
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final String IMU_COMMAND_ACC = "Meas/Acc/13";
    private static final String IMU_COMMAND_IMU6 = "Meas/IMU6/13";
    private static final byte MOVESENSE_REQUEST = 1, MOVESENSE_RESPONSE = 2, REQUEST_ID = 99, REQUEST_GET_ID = 98;
    private static final String LOG_TAG = "BtSensorDataCollector";

    private BluetoothDevice selectedDevice;
    private BluetoothGatt selectedDeviceGatt;
    private final Handler handler = new Handler();
    private Deque<String> commandQueue = new ArrayDeque<>();
    private boolean runningCommand = false;

    private int lastTimepoint = -1;

    private ConnectionListener connListener;

    public BtSensorCollector(Context context) {
        super(context);
    }

    public interface ConnectionListener {
        void onConnectionStateChange(boolean connected);
    }

    public void setDeviceConnectionListener(ConnectionListener listener) {
        this.connListener = listener;
    }

    public boolean isConnected() {
        return selectedDeviceGatt != null;
    }

    public String getDeviceName() {
        return selectedDevice.getName();
    }

    public void setDevice(BluetoothDevice device) {
        this.selectedDevice = device;
        if (this.selectedDevice == null) {
            if (selectedDeviceGatt != null) {
                this.selectedDeviceGatt.disconnect();
                this.selectedDeviceGatt = null;
            }
        } else {
            this.selectedDeviceGatt = this.selectedDevice.connectGatt(this.context, false, this.gattCallback);
        }
    }

    @Override
    public void setFrequency(SamplerFrequency frequency) {
        if (this.frequency == frequency) {
            return;
        }

        super.setFrequency(frequency);
        if (selectedDeviceGatt != null) {
            BluetoothGattService service = selectedDeviceGatt.getService(MOVESENSE_2_0_SERVICE);
            if (service != null) {
//                issueResetCommand(REQUEST_ID, service);
//                issueCommand(getImuCommand(), REQUEST_ID, service);
            }
        }
    }

    @Override
    public void setMeasureType(MeasureType measureType) {
        if (this.measureType == measureType) {
            return;
        }

        super.setMeasureType(measureType);
        if (selectedDeviceGatt != null) {
            BluetoothGattService service = selectedDeviceGatt.getService(MOVESENSE_2_0_SERVICE);
            if (service != null) {
//                issueResetCommand(REQUEST_ID, service);
//                issueCommand(getImuCommand(), REQUEST_ID, service);
            }
        }
    }

    public void disconnectDevice() {
        if (this.selectedDevice != null && this.selectedDeviceGatt != null) {
            this.selectedDeviceGatt.disconnect();
        }
    }

    public void connectDevice() {
        if (this.selectedDevice != null && selectedDeviceGatt == null) {
            this.selectedDeviceGatt = this.selectedDevice.connectGatt(this.context, false, this.gattCallback);
        }
    }

    /***
     * @author Anders Lindström anderslm@kth.se
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                selectedDeviceGatt = gatt;
                if (connListener != null) {
                    connListener.onConnectionStateChange(true);
                }
                // Discover services
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Close connection and display info in ui
                if (connListener != null) {
                    connListener.onConnectionStateChange(false);
                }
                selectedDeviceGatt = null;
                lastTimepoint = -1;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Debug: list discovered services
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    Log.i(LOG_TAG, service.getUuid().toString());
                }

                // Get the Movesense 2.0 IMU service
                BluetoothGattService movesenseService = gatt.getService(MOVESENSE_2_0_SERVICE);
                if (movesenseService != null) {
                    runResetCommand();
                    runCommand(getImuCommand());
                } else {
                    handler.post(() -> Utils.createDialog("Alert!",
                            context.getString(R.string.bt_service_not_found),
                            context)
                            .show());
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(LOG_TAG, "onCharacteristicWrite " + characteristic.getUuid().toString());

            // Enable notifications on data from the sensor. First: Enable receiving
            // notifications on the client side, i.e. on this Android device.
            BluetoothGattService movesenseService = gatt.getService(MOVESENSE_2_0_SERVICE);

            if (!commandQueue.isEmpty()) {
                String command = commandQueue.pollFirst();
                assert command != null;
                if (command.equals("RESET")) {
                    issueResetCommand(REQUEST_ID, movesenseService);
                } else {
                    issueCommand(command, REQUEST_ID, movesenseService);
                }
            } else {
                runningCommand = false;
            }


            BluetoothGattCharacteristic dataCharacteristic =
                    movesenseService.getCharacteristic(MOVESENSE_2_0_DATA_CHARACTERISTIC);
            // second arg: true, notification; false, indication
            boolean success = gatt.setCharacteristicNotification(dataCharacteristic, true);
            if (success) {
                Log.i(LOG_TAG, "setCharactNotification success");
                // Second: set enable notification server side (sensor). Why isn't
                // this done by setCharacteristicNotification - a flaw in the API?
                BluetoothGattDescriptor descriptor =
                        dataCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor); // callback: onDescriptorWrite
            } else {
                Log.i(LOG_TAG, "setCharacteristicNotification failed");
            }
        }


        /**
         * Callback called on characteristic changes, e.g. when a sensor data value is changed.
         * This is where we receive notifications on new sensor data.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] data = characteristic.getValue();

            if (MOVESENSE_2_0_DATA_CHARACTERISTIC.equals(characteristic.getUuid())) {

                if (data[0] == MOVESENSE_RESPONSE && data[1] == REQUEST_ID) {
                    // NB! use length of the array to determine the number of values in this
                    // "packet", the number of values in the packet depends on the frequency set(!)
                    int len = data.length;
                    // ...

                    int dtInt = TypeConverter.fourBytesToInt(data, 2);
                    if (lastTimepoint == -1) {
                        lastTimepoint = dtInt;
                    }
                    float dt = (float) (dtInt - lastTimepoint) / 1000.0f;
                    lastTimepoint = dtInt;


                    int bytesPerLoop = measureType == MeasureType.Accelerometer || measureType == MeasureType.Gyroscope ? 12 : 24;

                    for (int offset = 6; offset < len; offset += bytesPerLoop) {
                        LocalDateTime now = LocalDateTime.now();

                        int byteOffset = 0;
                        if (measureType == MeasureType.Accelerometer || measureType == MeasureType.IMU6) {

                            float accX = TypeConverter.fourBytesToFloat(data, offset);
                            float accY = TypeConverter.fourBytesToFloat(data, offset + 4);
                            float accZ = TypeConverter.fourBytesToFloat(data, offset + 8);
                            float[] accValues = accFilter.filter(new float[]{accX, accY, accZ});
                            Measurement accMeasurement = new Measurement(Measurement.Type.Accelerometer, Arrays.copyOf(accValues, accValues.length), now, dt);
                            addMeasurement(accMeasurement);

                            byteOffset += 12;
                        }
                        if (measureType == MeasureType.Gyroscope || measureType == MeasureType.IMU6) {
                            float gyroX = DataPoint.DEG_TO_RAD * TypeConverter.fourBytesToFloat(data, offset + byteOffset);
                            float gyroY = DataPoint.DEG_TO_RAD * TypeConverter.fourBytesToFloat(data, offset + byteOffset + 4);
                            float gyroZ = DataPoint.DEG_TO_RAD * TypeConverter.fourBytesToFloat(data, offset + byteOffset + 8);
                            float[] gyroValues = gyroFilter.filter(new float[]{gyroX, gyroY, gyroZ});
                            Measurement gyroMeasuremnet = new Measurement(Measurement.Type.Gyroscope, Arrays.copyOf(gyroValues, gyroValues.length), now, dt);
                            addMeasurement(gyroMeasuremnet);
                        }
                    }

                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            Log.i(LOG_TAG, "onCharacteristicRead " + characteristic.getUuid().toString());
        }
    };

    private void runCommand(String command) {
        if (runningCommand) {
            commandQueue.add(command);
        } else {
            runningCommand = true;
            BluetoothGattService movesenseService = selectedDeviceGatt.getService(MOVESENSE_2_0_SERVICE);
            issueCommand(command, REQUEST_ID, movesenseService);
        }
    }

    private void runResetCommand() {
        if (runningCommand) {
            commandQueue.add("RESET");
        } else {
            runningCommand = true;
            BluetoothGattService movesenseService = selectedDeviceGatt.getService(MOVESENSE_2_0_SERVICE);
            issueResetCommand(REQUEST_ID, movesenseService);
        }
    }

    private boolean issueResetCommand(byte requestId, BluetoothGattService movesenseService) {
        BluetoothGattCharacteristic commandChar = movesenseService.getCharacteristic(MOVESENSE_2_0_COMMAND_CHARACTERISTIC);
        commandChar.setValue(new byte[]{2, requestId});
        return selectedDeviceGatt.writeCharacteristic(commandChar);
    }

    private boolean issueCommand(String command, byte requestId, BluetoothGattService movesenseService) {
        BluetoothGattCharacteristic commandChar = movesenseService.getCharacteristic(MOVESENSE_2_0_COMMAND_CHARACTERISTIC);
        byte[] commandInBytes = TypeConverter.stringToAsciiArray(requestId, command);
        commandChar.setValue(commandInBytes);
        return selectedDeviceGatt.writeCharacteristic(commandChar);
    }


    private String getImuCommand() {
        String cmd = "Meas/";

        switch (this.measureType) {
            case Accelerometer:
                cmd += "Acc/";
                break;
            case Gyroscope:
                cmd += "Gyro/";
                break;
            case IMU6:
                cmd += "IMU6/";
                break;
        }

        cmd += this.frequency.toString().substring(2);

        return cmd;
    }
}
