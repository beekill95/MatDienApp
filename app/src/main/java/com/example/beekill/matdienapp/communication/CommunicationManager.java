package com.example.beekill.matdienapp.communication;

/**
 * Created by beekill on 8/24/16.
 */
public abstract class CommunicationManager implements DeviceCommunication.ReceivedDataHandler
{
    protected DeviceCommunication deviceCommunication;
    protected ResultReceivedHandler handler;

    public CommunicationManager(DeviceCommunication deviceCommunication)
    {
        this.deviceCommunication = deviceCommunication;
        deviceCommunication.registerHandler(this);
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

    public void removeHandler(ResultReceivedHandler handler)
    {
        if (this.handler == handler)
            this.handler = null;
    }
}
