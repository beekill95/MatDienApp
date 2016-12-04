package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public interface AdminProtocol extends ResponseProtocol {
    String changeAdminPasswordMessage(String username, String oldPass, String newPass);
    String changeSubscriberPasswordMessage(String username, String oldPass, String newPass, String adminPass);
    String getSubscriberListMessage(String status);
    String removeSubscriberMessage(String status, String subscriberPhoneNumber);
    String addSubscriberMessage(String subscriberPhoneNumber, String subscriptionType);
    String getAccountCreditMessage();
    String rechargeAccountCreditMessage(String creditCardId);
    String sessionInitialization(String user, String password);

    String getWifiAccessPointInquiryMessage();
    String getWifiConnectionMessage(String accessPoint, String password);
}
