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

    public DeviceResponseMessageType getDeviceResponseType(String messageString)
    {
        DeviceResponseMessageType type = null;
        try {
            JSONObject message = new JSONObject(messageString);

            if (message.has("result"))
                type = DeviceResponseMessageType.Response;
            else if (message.has("values"))
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
            message.accumulate("pass", adminPass);
            message.accumulate("action", "add phone");
            message.accumulate("phone", subscriberPhoneNumber);
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
            message.accumulate("user", "admin");
            message.accumulate("oldpass", oldPass);
            message.accumulate("newpass", newPass);
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
            message.accumulate("user", "subscriber");
            message.accumulate("oldpass", oldPass);
            message.accumulate("newpass", newPass);
            message.accumulate("pass", adminPass);
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
            message.accumulate("action", "get list");
            message.accumulate("pass", adminPass);
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
            message.accumulate("action", "del phone");
            message.accumulate("pass", adminPass);
            message.accumulate("phone", subscriberPhoneNumber);
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
            message.accumulate("action", "check account");
            message.accumulate("pass", adminPass);
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
            message.accumulate("action", "recharge");
            message.accumulate("pass", adminPass);
            message.accumulate("id", creditCardId);
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

            if (message.has("values")) {
                JSONObject valuesObject = message.getJSONObject("values");

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

            if (message.has("result")) {
                response = new Response();

                // get result
                String result = message.getString("result");
                if (result.equals("true"))
                    response.setResult(true);
                else
                    response.setResult(false);

                // get description
                response.setDescription(message.getString("desc"));

                // get list, if any
                if (message.has("list"))
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
            message.accumulate("sub", subscriptionType);
            message.accumulate("pass", subscriberPass);
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
            message.accumulate("unsub", subscriptionType);
            message.accumulate("pass", subscriberPass);
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            return message.toString();
        }
    }

    @Override
    public String notificationMessage(String password) {
        return null;
    }
}
