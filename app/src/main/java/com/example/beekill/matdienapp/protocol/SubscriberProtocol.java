package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public interface SubscriberProtocol {
    String addSubscriberMessage(SubscriptionType type, String subscriberPass);
    String removeSubscriberMessage(SubscriptionType type, String subscriberPass);
}
