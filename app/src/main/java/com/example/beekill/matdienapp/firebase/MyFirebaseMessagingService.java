package com.example.beekill.matdienapp.firebase;

import android.content.Intent;
import android.util.Log;

import com.example.beekill.matdienapp.MatDienService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by beekill on 5/2/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Received message
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // check if the message contains any data
        if (remoteMessage.getData().size() > 0) {
            sendMessageToMatDienService(remoteMessage.getData());
        } else {
            Log.d(TAG, "Received remote message but it doesn't contain any data");
        }
    }

    private void sendMessageToMatDienService(Map<String, String> data) {
        Intent startServiceIntent = new Intent(this, MatDienService.class);
        startServiceIntent.putExtra("fromAddress", data.get("fromAddress"));
        startServiceIntent.putExtra("message", data.get("message"));

        Log.d(TAG, data.get("fromAddress"));
        Log.d(TAG, data.get("message"));

        startService(startServiceIntent);
    }
}
