package com.example.beekill.matdienapp.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by beekill on 9/29/16.
 */
public class BluetoothCommunication extends DeviceCommunication {
    private static final int BLUETOOTH_CONNECTION_RESULT = 0;
    private static final int BLUETOOTH_CONNECTION_LOST = 1;
    private static final int BLUETOOTH_MESSAGE_RECEIVED = 2;

    private static final int SUCCESSFUL = 1;
    private static final int FAILED = 0;

    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    /////////////////////////////////////////// BluetoothStatusHandler Interface //
    public interface BluetoothStatusHandler {
        void handleDeviceConnectionResult(boolean isConnected);
        void handleDeviceConnectionLost();
    }

    private BluetoothStatusHandler bluetoothStatusHandler;

    void registerBluetoothStatusHandler(BluetoothStatusHandler handler) {
        bluetoothStatusHandler = handler;
    }

    void unregisterBluetoothStatusHandler(BluetoothStatusHandler handler) {
        if (handler == bluetoothStatusHandler)
            bluetoothStatusHandler = null;
    }

    ///////////////////////////////////////////////////////////////////////////////

    private Handler threadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BLUETOOTH_CONNECTION_RESULT:
                    boolean result = false;
                    if (msg.arg1 == SUCCESSFUL) {
                        // start io thread
                        createSocketIO();

                        result = true;
                    }

                    if (bluetoothStatusHandler != null)
                        bluetoothStatusHandler.handleDeviceConnectionResult(result);
                    break;
                case BLUETOOTH_CONNECTION_LOST:
                    if (bluetoothStatusHandler != null)
                        bluetoothStatusHandler.handleDeviceConnectionLost();
                    break;
                case BLUETOOTH_MESSAGE_RECEIVED:
                    String message = (String) msg.obj;
                    handleReceivedData(message, bluetoothDevice.getAddress());
                    break;
            }
        }
    };

    @Override
    public boolean initiateConnection(String deviceAddress) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // create a socket to device
        try {
            socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // initialize a new thread to connect to
        // the bluetooth device
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Method m = bluetoothDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});;

                    socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                    Thread.sleep(500);
                    socket.connect();

                    // send successful message
                    Message mainThreadMessage = Message.obtain();
                    mainThreadMessage.what = BLUETOOTH_CONNECTION_RESULT;
                    mainThreadMessage.arg1 = SUCCESSFUL;
                    threadHandler.sendMessage(mainThreadMessage);
                } catch (Exception e) {
                    // send failed message
                    Message mainThreadMessage = Message.obtain();
                    mainThreadMessage.what = BLUETOOTH_CONNECTION_RESULT;
                    mainThreadMessage.arg1 = FAILED;
                    threadHandler.sendMessage(mainThreadMessage);

                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        return true;
    }

    private void createSocketIO() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (Exception e) { }

        Runnable listening = new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (true) {
                    try {
                        bytes = inputStream.read(buffer);

                        // pass message to the main thread handler
                        Message mainThreadMessage = Message.obtain();
                        mainThreadMessage.what = BLUETOOTH_MESSAGE_RECEIVED;
                        mainThreadMessage.obj = buffer.toString();
                        threadHandler.sendMessage(mainThreadMessage);
                    } catch (Exception e) {
                        // pass lost connection to device
                        Message mainThreadMessage = Message.obtain();
                        mainThreadMessage.what = BLUETOOTH_CONNECTION_LOST;
                        threadHandler.sendMessage(mainThreadMessage);

                        break;
                    }
                }
            }
        };

        Thread t = new Thread(listening);
        t.start();
    }

    @Override
    public void terminateConnection() {
        try {
            socket.close();
        } catch (Exception e) { }
    }

    @Override
    public void send(String data, String toAddress) {
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void receiveIncomingData(String data, String fromAddress) {
        handleReceivedData(data, fromAddress);
    }

    @Override
    public void registerDataReceiverToAndroid(Context context) {
        // do nothing
    }

    @Override
    public void unregisterDataReceiverToAndroid(Context context) {
        // do nothing
    }
}
