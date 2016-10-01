package com.example.beekill.matdienapp;

/**
 * Created by beekill on 10/1/16.
 */
public class DeviceInformation {
    private String bluetoothAddress;
    private String phoneAddress;

    public DeviceInformation(String bluetoothAddress, String phoneAddress) {
        this.bluetoothAddress = bluetoothAddress;
        this.phoneAddress = phoneAddress;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getPhoneAddress() {
        return phoneAddress;
    }

    public void setPhoneAddress(String phoneAddress) {
        this.phoneAddress = phoneAddress;
    }
}
