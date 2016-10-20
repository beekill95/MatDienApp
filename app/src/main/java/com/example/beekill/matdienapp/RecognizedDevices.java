package com.example.beekill.matdienapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by beekill on 9/29/16.
 */
public class RecognizedDevices implements Serializable {

    private ArrayList<DeviceInformation> devices;

    public RecognizedDevices() {
        devices = new ArrayList<>();
    }

    public void addNewDevice(String bluetoothAddress, String phoneNumber)
    {
        devices.add(devices.size(), new DeviceInformation(bluetoothAddress, phoneNumber));
    }

    public DeviceInformation getDevice(int index) {
        if (index >= devices.size())
            return null;

        return devices.get(index);
    }

    public DeviceInformation removeDevice(int index) {
        if (index >= devices.size())
            return null;

        return devices.remove(index);
    }

    public ArrayList<DeviceInformation> getDevices() {
        return devices;
    }

    public int numOfDevices() {
        return devices.size();
    }
}
