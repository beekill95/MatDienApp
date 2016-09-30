package com.example.beekill.matdienapp;

import android.app.Application;

import com.example.beekill.matdienapp.communication.DeviceCommunication;

/**
 * Created by beekill on 9/30/16.
 */
public class MatDienApplication extends Application  {
    private DeviceCommunication deviceCommunication;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void storeConnection(DeviceCommunication deviceCommunication)
    {
        this.deviceCommunication = deviceCommunication;
    }

    public void removeConnection() {
        this.deviceCommunication = null;
    }

    public DeviceCommunication getDeviceCommunication() {
        return deviceCommunication;
    }
}
