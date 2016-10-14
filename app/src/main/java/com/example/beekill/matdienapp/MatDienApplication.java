package com.example.beekill.matdienapp;

import android.app.Application;

import com.example.beekill.matdienapp.communication.DeviceCommunication;
import com.example.beekill.matdienapp.protocol.AdminProtocol;
import com.example.beekill.matdienapp.protocol.Protocol;
import com.example.beekill.matdienapp.protocol.SubscriberProtocol;

/**
 * Created by beekill on 9/30/16.
 */
public class MatDienApplication extends Application  {
    private DeviceCommunication deviceCommunication;
    private Protocol protocol;

    @Override
    public void onCreate() {
        protocol = new Protocol();
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

    public AdminProtocol getAdminProtocol() {
        return protocol;
    }

    public SubscriberProtocol getSubscriberProtocol() {
        return protocol;
    }
}
