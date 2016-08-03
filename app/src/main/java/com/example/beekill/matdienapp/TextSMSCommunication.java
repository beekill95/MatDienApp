package com.example.beekill.matdienapp;

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
    static public class TextSMSReceiverHandler extends BroadcastReceiver
    {
        TextSMSReceiverHandler() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // extract sms message
            Bundle bundle = intent.getExtras();
            String message = getMessage(bundle);

            // start the service to handle sms message
            Intent serviceIntent = new Intent(context, MatDienService.class);
            serviceIntent.putExtra("message", message);
            context.startService(serviceIntent);
        }
    }

    private BroadcastReceiver smsReceiver;

    TextSMSCommunication() {
        // define our smsReceiver
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                String message = getMessage(bundle);

                // passing to the handler
                receiveIncomingData(message);
            }
        };
    }

    static private String getMessage(Bundle bundle)
    {
        SmsMessage smsMessage;

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

        return message;
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
