package com.example.beekill.matdienapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by beekill on 7/27/16.
 */
public class TextSMSCommunication extends DeviceCommunication {
    private BroadcastReceiver smsReceiver;

    TextSMSCommunication() {
        // define our smsReceiver
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("MatDienApp", "on sms receive called");
                Bundle bundle = intent.getExtras();

                SmsMessage smsMessage = null;

                String message = "";

                if (bundle != null) {
                    // retrieve the sms messages received
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdus.length; ++i) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                        message += "SMS from " + smsMessage.getOriginatingAddress() + ": ";
                        message += smsMessage.getMessageBody();
                        message += "\n";
                    }
                }

                // passing to the handler
                receiveIncomingData(message);
            }
        };
    }

    @Override
    public void send(String data, String toAddress) {
        SmsManager smsManager = SmsManager.getDefault();

        // send a text based sms
        smsManager.sendTextMessage(toAddress, null, data, null, null);
    }

    @Override
    protected void receiveIncomingData(String data) {
        // passing the data to the handler without additional processing
        handleReceivedData(data);
    }

    @Override
    public void registerDataReceiverToAndroid(Context context) {
        Log.i("MatDienApp", "Begin register");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        // register the data receiver to android system
        context.getApplicationContext().registerReceiver(smsReceiver, intentFilter);

    }
}
