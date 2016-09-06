package com.example.beekill.matdienapp.communication;

import android.content.Context;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by beekill on 8/24/16.
 */
public class QueueManager extends CommunicationManager {
    private static final int MESSAGE = 0;
    private static final int TO_ADDRESS = 1;

    Queue<String[]> messageQueue;
    int currentMessageInQueue;
    int currentMessageSending;
    boolean isSendingMessage;

    public QueueManager(Context context, DeviceCommunication deviceCommunication) {
        super(context, deviceCommunication);

        messageQueue = new LinkedList<>();

        currentMessageInQueue = -1;
        currentMessageSending = -1;
        isSendingMessage = false;
    }

    @Override
    public int enqueueMessageToSend(String message, String toAddress) {
        // enqueue message
        String messageToSend[] = {message, toAddress};
        messageQueue.add(messageToSend);

        currentMessageInQueue++;

        // send next message
        sendNextMessage();

        return currentMessageInQueue;
    }

    @Override
    public boolean abortMessage(int messageId) {
        return false;
    }

    @Override
    public void handle(String data, String fromAddress) {
        // check whether it is from our previously send address
        // or we didn't send any message
        if (messageQueue.isEmpty() || !messageQueue.peek()[TO_ADDRESS].equals(fromAddress))
            // not from our expected sender
            return;

        // pass message back to handler
        if (handler != null)
            handler.handleMessageReceived(data, fromAddress, currentMessageSending);

        // remove the previously sent message
        messageQueue.remove();
        isSendingMessage = false;

        // send next message
        sendNextMessage();
    }

    private void sendNextMessage()
    {
        // check whether we are sending something
        // or there is nothing to send
        //if (currentMessageInQueue == currentMessageSending)
            //return;
        if (isSendingMessage)
            return;

        // check if we have to send next message
        if (messageQueue.isEmpty())
            return;

        // send the next message
        String message[] = messageQueue.peek();
        deviceCommunication.send(message[MESSAGE], message[TO_ADDRESS]);

        // increment currentMessageSending
        currentMessageSending++;
        isSendingMessage = true;
    }
}
