package com.example.beekill.matdienapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.beekill.matdienapp.activities.NotificationActivity;
import com.example.beekill.matdienapp.protocol.Notification;
import com.example.beekill.matdienapp.protocol.Protocol;

/**
 * Created by beekill on 7/29/16.
 */
public class MatDienService extends IntentService {
    final static String devicePhoneNumbers[] = {
            "+841237231353",
            "01237231353",
            "6505551212",
    //        "0988814686",
    //        "+84988814686",
    //        "01695544864",
    //        "+841695544864"
    };

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
        if (isKnownDevices(devicePhoneNumber)) {
            Log.i("MatDienApp", "Service received message " + message + " from device address " + fromAddress);

            // get notification from the message
            // but first, we have to check whether it is a notification
            Protocol protocol = new Protocol();
            Protocol.DeviceResponseMessageType messageType = protocol.getDeviceResponseType(message);

            if (messageType == Protocol.DeviceResponseMessageType.Notification) {
                // only process notification
                Notification notification = protocol.getNotification(message);

                // pass the notification to notification activity
                Intent startNotificationActivityIntent = new Intent(this, NotificationActivity.class);
                startNotificationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startNotificationActivityIntent.putExtra("notification", notification);
                startActivity(startNotificationActivityIntent);
            }
        }
    }

    private boolean isKnownDevices(String phoneNumber) {
        for (String devicePhoneNumber : devicePhoneNumbers) {
            if (devicePhoneNumber.equals(phoneNumber))
                return true;
        }

        return false;
    }
}
