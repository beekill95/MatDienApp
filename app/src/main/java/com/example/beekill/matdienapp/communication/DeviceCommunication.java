package com.example.beekill.matdienapp.communication;

import android.content.Context;

/**
 * Created by beekill on 7/23/16.
 * This is an abstract class
 * Store all the functions needed to send send and handle incoming data
 */
public abstract class DeviceCommunication {
    public static final int MESSAGE_INDEX = 0;
    public static final int ADDRESS_INDEX = 1;

    /*
    * The interface that a class will need to implement in order to handle the incoming data
    * */
    public interface ReceivedDataHandler {
        void handle(String data, String fromAddress);
    }

    public boolean initiateConnection(String deviceAddress) {return true;}
    public void terminateConnection() {}

    /*
    * The functions to send and receive the data
    * The inherited class will need to implement the functions
    * For the receivedData function, after the concrete class process the received data,
    * it will call the handleReceivedData function to process the received data
    * */
    public abstract void send(String data, String toAddress);
    protected abstract void receiveIncomingData(String data, String fromAddress);


    /*
    * Private member to store a reference to a class
    * to handle the incoming data
    * */
    private ReceivedDataHandler handler;

    /*
    * Function to register the handler
    * */
    public void registerHandler(ReceivedDataHandler handler)
    {
        this.handler = handler;
    }

    /*
    * Function to remove the handler
    * */
    public void unregisterHandler(ReceivedDataHandler handler)
    {
        if (handler == this.handler)
            this.handler = null;
    }

    /*
    * Function to call the handler
    * */
    protected void handleReceivedData(String data, String fromAddress)
    {
        handler.handle(data, fromAddress);
    }

    /*
    * Function to register the receiver to the android system dynamically
    * */
    public abstract void registerDataReceiverToAndroid(Context context);

    /*
    * Function to unregister the receiver to the android system dynamically
    * */
    public abstract void unregisterDataReceiverToAndroid(Context context);
}
