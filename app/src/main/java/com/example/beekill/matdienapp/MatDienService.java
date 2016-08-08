package com.example.beekill.matdienapp;

import android.app.IntentService;
import android.content.Intent;
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
        android.os.Debug.waitForDebugger();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra("message");
        Log.i("MatDienApp", "Service received message " + message);

        //MessageHandler.handleInService(this, message);
    }
}
