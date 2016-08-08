package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public interface AdminProtocol {
    String changeAdminPasswordMessage(String username, String oldPass, String newPass);
    String changeSubscriberPasswordMessage(String username, String oldPass, String newPass, String adminPass);
    String getSubscriberListMessage(String adminPass);
    String removeSubscriberMessage(String adminPass, String subscriberPhoneNumber);
    String addSubscriberMessage(String adminPass, String subscriberPhoneNumber);
    String getAccountCreditMessage(String adminPass);
    String rechargeAccountCreditMessage(String adminPass, String creditCardId);
}
