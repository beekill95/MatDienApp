package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public class Protocol
    implements AdminProtocol, SubscriberProtocol, NotificationProtocol, ResponseProtocol
{
    @Override
    public String addSubscriberMessage(String adminPass, String subscriberPhoneNumber) {
        return null;
    }

    @Override
    public String changeAdminPasswordMessage(String username, String oldPass, String newPass) {
        return null;
    }

    @Override
    public String changeSubscriberPasswordMessage(String username, String oldPass, String newPass, String adminPass) {
        return null;
    }

    @Override
    public String getSubscriberListMessage(String adminPass) {
        return null;
    }

    @Override
    public String removeSubscriberMessage(String adminPass, String subscriberPhoneNumber) {
        return null;
    }

    @Override
    public String getAccountCreditMessage(String adminPass) {
        return null;
    }

    @Override
    public String rechargeAccountCreditMessage(String adminPass, String creditCardId) {
        return null;
    }

    @Override
    public SubscriptionType[] getNotifiedTypes(String notificationMessage) {
        return new SubscriptionType[0];
    }

    @Override
    public Response getResponse(String message) {
        return null;
    }

    @Override
    public String addSubscriberMessage(SubscriptionType type, String subscriberPass) {
        return null;
    }

    @Override
    public String removeSubscriberMessage(SubscriptionType type, String subscriberPass) {
        return null;
    }
}
