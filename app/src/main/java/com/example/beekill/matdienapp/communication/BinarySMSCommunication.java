package com.example.beekill.matdienapp.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Created by beekill on 7/26/16.
 */
public class BinarySMSCommunication extends DeviceCommunication {
    private final short port = 6225;
    private BroadcastReceiver smsReceiver;

    BinarySMSCommunication() {
        // define our smsReceiver
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                SmsMessage[] messages = null;

                String message = "";

                // Retrieve sms messages
                if (bundle != null) {
                    // get the sms messages received
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < messages.length; ++i) {
                        byte[] data = null;

                        // need change in the future
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                        // get user data
                        data = messages[i].getUserData();

                        // convert binary data to string
                        for (int index = 0; index < data.length; ++index)
                            message += Character.toString((char) data[index]);
                    }
                }

                // passing the received message to the handler
                receiveIncomingData(message, "");
            }
        };
    }

    @Override
    protected void receiveIncomingData(String data, String fromAddress) {
        // sending the data to the handler to continue processing
        // the message
        handleReceivedData(data, fromAddress);
    }

    @Override
    public void send(String data, String toAddress) {
        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendDataMessage(toAddress, null, port, data.getBytes(), null, null);
    }

    @Override
    public void registerDataReceiverToAndroid(Context context) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
        intentFilter.setPriority(10);
        intentFilter.addDataScheme("sms");
        intentFilter.addDataAuthority("*", String.valueOf(port));

        context.registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void unregisterDataReceiverToAndroid(Context context) {
        context.unregisterReceiver(smsReceiver);
    }
}
