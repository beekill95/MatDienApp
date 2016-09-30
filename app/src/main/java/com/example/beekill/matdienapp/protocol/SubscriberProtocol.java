package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public interface SubscriberProtocol extends ResponseProtocol {
    String addSubscription(String subscriptionType, String subscriberPass);
    String removeSubscription(String subscriptionType, String subscriberPass);
    String sessionInitialization(String user, String password);
}
