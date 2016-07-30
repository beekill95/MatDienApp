package com.example.beekill.matdienapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by beekill on 7/29/16.
 */
public class MatDienService extends Service implements DeviceCommunication.ReceivedDataHandler {
    private MatDienBinder binder = new MatDienBinder();
    private DeviceCommunication deviceCommunication;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize sms communication
        deviceCommunication = new TextSMSCommunication();

        // register onDataReceived handler
        deviceCommunication.registerHandler(this);

        // register sms receiver to android system
        deviceCommunication.registerDataReceiverToAndroid(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MatDienBinder extends Binder {
        MatDienService getService() {
            return MatDienService.this;
        }
    }

    @Override
    public void handle(String data) {
        Log.i("MatDienApp", data);
    }
}
