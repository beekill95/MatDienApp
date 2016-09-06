package com.example.beekill.matdienapp.communication;

import android.content.Context;

/**
 * Created by beekill on 8/24/16.
 */
public abstract class CommunicationManager implements DeviceCommunication.ReceivedDataHandler
{
    protected DeviceCommunication deviceCommunication;
    protected ResultReceivedHandler handler;

    public CommunicationManager(Context context, DeviceCommunication deviceCommunication)
    {
        // register broadcast receiver to android system
        deviceCommunication.registerDataReceiverToAndroid(context);

        deviceCommunication.registerHandler(this);
        this.deviceCommunication = deviceCommunication;
    }

    public interface ResultReceivedHandler
    {
        void handleMessageReceived(String message, String fromAddress, int messageId);
    }

    public abstract int enqueueMessageToSend(String message, String toAddress);

    public abstract boolean abortMessage(int messageId);

    public void setHandler(ResultReceivedHandler handler)
    {
        this.handler = handler;
    }

    public void removeHandler(Context context, ResultReceivedHandler handler)
    {
        if (this.handler == handler)
            this.handler = null;

        // unregister broadcast receiver to android system
        deviceCommunication.unregisterDataReceiverToAndroid(context);
    }
}
