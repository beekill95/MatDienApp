package com.example.beekill.matdienapp;

/**
 * Created by beekill on 8/3/16.
 */
public abstract class AbstractMessagePackaging {
    public abstract String packSubscribeUserMessage(String username, String password);
    public abstract String packUnsubscribeUserMessage(String username, String password);
    public abstract String packControlMessage(String username, String password, String controlCode);
    public abstract String packControlMessage(String username, String password, String controlCode, String message);
}
