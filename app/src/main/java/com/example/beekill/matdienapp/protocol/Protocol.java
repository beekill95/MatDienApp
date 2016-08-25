package com.example.beekill.matdienapp.protocol;
import android.bluetooth.BluetoothClass;

import org.json.*;

/**
 * Created by beekill on 8/8/16.
 */
public class Protocol
    implements AdminProtocol, SubscriberProtocol, NotificationProtocol, ResponseProtocol
{
    public enum DeviceResponseMessageType {
        Unknown, Response, Notification
    }

    private static class ProtocolString
    {
        // json fields
        public static final String ACTION_FIELD_STRING = "action";
        public static final String USER_FIELD_STRING = "user";
        public static final String NEWPASS_FIELD_STRING = "newpass";
        public static final String PASS_FIELD_STRING = "pass";
        public static final String STATUS_FIELD_STRING = "status";
        public  static final String PHONE_NUMBER_FIELD_STRING = "phone";
        public static final String RECHARGE_CREDIT_CODE_FIELD_STRING = "code";
        public static final String STATUS_VALUE_FIELD_STRING = "values";

        public static final String RESULT_FIELD_STRING = "result";
        public static final String DESCRIPTION_FIELD_STRING = "desc";
        public static final String LIST_FIELD_STRING = "list";

        // json user values
        public static final String ADMIN_USER_STRING = "admin";

        // json action values
        public static final String CHANGE_PASS_ACTION_STRING = "ChangePassword";
        public static final String GET_SUBSCRIBER_LIST_ACTION_STRING = "GetSubscriber";
        public static final String DELETE_SUBSCRIBER_ACTION_STRING = "DelSubscriber";
        public static final String ADD_SUBSCRIBER_ACTION_STRING = "AddSubscriber";
        public static final String CHECK_PHONE_ACCOUNT_ACTION_STRING = "CheckPhoneAcc";
        public static final String REFILL_PHONE_ACCOUNT_ACTION_STRING = "RefillPhoneAcc";
        public static final String UPDATE_STATUS_ACTION_STRING = "UpdateStatus";

        public static final String SUBSCRIBE_ACTION_STRING = "Subscribe";
        public static final String UNSUBSCRIBE_ACTION_STRING = "Unsubscribe";

        public static final String NOTIFICATION_ACTION_STRING = "Notification";

        // json result values
        public static final String RESULT_SUCCESS_STRING = "SUCCESS";
        public static final String RESULST_FAILED_STRING = "FAILED";

        public static final String STATUS_VALUE_OK_STRING = "on";
        public static final String STATUS_VALUE_NOT_OK_STRING = "off";
    }

    public DeviceResponseMessageType getDeviceResponseType(String messageString)
    {
        DeviceResponseMessageType type = null;
        try {
            JSONObject message = new JSONObject(messageString);

            if (message.has(ProtocolString.RESULT_FIELD_STRING))
                type = DeviceResponseMessageType.Response;
            else if (message.has(ProtocolString.STATUS_VALUE_FIELD_STRING))
                type = DeviceResponseMessageType.Notification;
            else
                type = DeviceResponseMessageType.Unknown;
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return type;
    }

    @Override
    public String addSubscriberMessage(String adminPass, String subscriberPhoneNumber) {
        JSONObject message = new JSONObject();
        try {
            // create message
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.ADD_SUBSCRIBER_ACTION_STRING);
            message.accumulate(ProtocolString.PHONE_NUMBER_FIELD_STRING, subscriberPhoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String changeAdminPasswordMessage(String username, String oldPass, String newPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.USER_FIELD_STRING, "admin");
            message.accumulate(ProtocolString.PASS_FIELD_STRING, oldPass);
            message.accumulate(ProtocolString.NEWPASS_FIELD_STRING, newPass);
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.CHANGE_PASS_ACTION_STRING);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String changeSubscriberPasswordMessage(String username, String oldPass, String newPass, String adminPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.USER_FIELD_STRING, "subscriber");
            message.accumulate(ProtocolString.NEWPASS_FIELD_STRING, newPass);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.CHANGE_PASS_ACTION_STRING);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String getSubscriberListMessage(String adminPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.GET_SUBSCRIBER_LIST_ACTION_STRING);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String removeSubscriberMessage(String adminPass, String subscriberPhoneNumber) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.DELETE_SUBSCRIBER_ACTION_STRING);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
            message.accumulate(ProtocolString.PHONE_NUMBER_FIELD_STRING, subscriberPhoneNumber);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String getAccountCreditMessage(String adminPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.CHECK_PHONE_ACCOUNT_ACTION_STRING);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String rechargeAccountCreditMessage(String adminPass, String creditCardId) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.REFILL_PHONE_ACCOUNT_ACTION_STRING);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, adminPass);
            message.accumulate(ProtocolString.RECHARGE_CREDIT_CODE_FIELD_STRING, creditCardId);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public Notification getNotification(String notificationMessage) {
        Notification notification = null;
        try {
            JSONObject message = new JSONObject(notificationMessage);

            if (message.has(ProtocolString.STATUS_VALUE_FIELD_STRING)) {
                JSONObject valuesObject = message.getJSONObject(ProtocolString.STATUS_VALUE_FIELD_STRING);

                boolean cameraOn = valuesObject.getBoolean(SubscriptionType.Camera.getValue());
                boolean powerOn = valuesObject.getBoolean(SubscriptionType.Power.getValue());
                boolean haveTheif = valuesObject.getBoolean(SubscriptionType.Thief.getValue());

                notification = new Notification(powerOn, cameraOn, haveTheif);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return notification;
    }

    @Override
    public Response getResponse(String responseMesage) {
        Response response = null;
        try {
            JSONObject message = new JSONObject(responseMesage);

            if (message.has(ProtocolString.RESULT_FIELD_STRING)) {
                response = new Response();

                // get result
                String result = message.getString(ProtocolString.RESULT_FIELD_STRING);
                if (result.equals(ProtocolString.RESULT_SUCCESS_STRING))
                    response.setResult(true);
                else
                    response.setResult(false);

                // get description
                response.setDescription(message.getString(ProtocolString.DESCRIPTION_FIELD_STRING));

                // get list, if any
                if (message.has(ProtocolString.LIST_FIELD_STRING))
                    response.setList(null);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    private String[] getSubscriberList(JSONArray subscriberList)
    {
        String[] subscribers = null;
        try {
            subscribers = new String[subscriberList.length()];

            for (int i = 0; i < subscriberList.length(); ++i)
                subscribers[i] = subscriberList.getString(i);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return subscribers;
    }

    @Override
    public String addSubscription(String subscriptionType, String subscriberPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.STATUS_FIELD_STRING, subscriptionType);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, subscriberPass);
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.SUBSCRIBE_ACTION_STRING);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String removeSubscription(String subscriptionType, String subscriberPass) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.UNSUBSCRIBE_ACTION_STRING);
            message.accumulate(ProtocolString.STATUS_FIELD_STRING, subscriptionType);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, subscriberPass);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String notificationMessage(String password) {
        JSONObject message = new JSONObject();
        try {
            message.accumulate(ProtocolString.ACTION_FIELD_STRING, ProtocolString.UPDATE_STATUS_ACTION_STRING);
            message.accumulate(ProtocolString.PASS_FIELD_STRING, password);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }
}
