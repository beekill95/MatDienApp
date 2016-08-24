package com.example.beekill.matdienapp.communication;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by beekill on 8/24/16.
 */
public class QueueManager extends CommunicationManager {
    Queue<String[]> messageQueue;
    int currentMessageInQueue;
    int currentMessageSending;

    public QueueManager(DeviceCommunication deviceCommunication) {
        super(deviceCommunication);

        messageQueue = new LinkedList<>();

        currentMessageInQueue = -1;
        currentMessageSending = -1;
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
        // pass message back to handler
        if (handler != null)
            handler.handleMessageReceived(data, fromAddress, currentMessageSending);

        // send next message
        sendNextMessage();
    }

    private void sendNextMessage()
    {
        // remove the previously sent message
        messageQueue.remove();

        // check if we have to send next message
        if (messageQueue.isEmpty())
            return;

        // if so, increment currentMessageSending
        currentMessageSending++;

        // send the next message
        String message[] = messageQueue.peek();
        deviceCommunication.send(message[0], message[1]);
    }

    /*private class Pair
    {
        Pair(int first, String[] second)
        {
            this.first = first;
            this.second = second;
        }

        public int first;
        public String[] second;
    }*/
}
