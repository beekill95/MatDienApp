package com.example.beekill.matdienapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by beekill on 7/29/16.
 */
public class MatDienService extends IntentService {
    public MatDienService() {
        super("MatDienService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra("message");
        String fromAddress = intent.getStringExtra("fromAddress");


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );
        String devicePhoneNumber = sharedPreferences.getString("devicePhoneNumber", null);

        // only process those messages sent from device phone number
        if (fromAddress.equals(devicePhoneNumber)) {
            Log.i("MatDienApp", "Service received message " + message + " from device address " + fromAddress);

            // get notification from the message
            // but first, we have to check whether it is a notification
        }
    }
}
