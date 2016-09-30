package com.example.beekill.matdienapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by beekill on 9/29/16.
 */
public class RecognizedDevices implements Serializable {
    public static final int BLUETOOTH_ADDRESS = 0;
    public static final int PHONE_NUMBER = 1;

    private ArrayList<String[]> devices;

    public RecognizedDevices() {
        devices = new ArrayList<>();
    }

    public void addNewDevice(String bluetoothAddress, String phoneNumber)
    {
        String device[] = {bluetoothAddress, phoneNumber};

        devices.add(devices.size(), device);
    }

    public String[] getDevice(int index) {
        if (index >= devices.size())
            return null;

        return devices.get(index);
    }

    public String[] removeDevice(int index) {
        if (index >= devices.size())
            return null;

        return devices.remove(index);
    }
}
