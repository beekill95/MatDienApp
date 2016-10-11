package com.example.beekill.matdienapp.activities.admin;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created by beekill on 9/5/16.
 */
public class AdminData implements Serializable
{
    private Date subscriberListUpdateDate;
    private String[] powerSubscribers;
    private String[] cameraSubscribers;
    private String[] thiefSubscribers;

    private Date deviceAccountUpdateDate;
    private double deviceAccount;

    public AdminData()
    {
        powerSubscribers = null;
        cameraSubscribers = null;
        thiefSubscribers = null;
        subscriberListUpdateDate = null;

        deviceAccountUpdateDate = null;
        deviceAccount = -1;
    }

    public void setPowerSubscribers(String[] powerSubscribers)
    {
        // update the date
        subscriberListUpdateDate = new Date();

        // update subscriber list
        this.powerSubscribers = powerSubscribers;
    }

    public void setCameraSubscribers(String[] cameraSubscribers) {
        // update the date
        subscriberListUpdateDate = new Date();

        this.cameraSubscribers = cameraSubscribers;
    }

    public void setThiefSubscribers(String[] thiefSubscribers) {
        // update the date
        subscriberListUpdateDate = new Date();

        this.thiefSubscribers = thiefSubscribers;
    }

    public String[] getPowerSubscribers() {
        return powerSubscribers;
    }

    public String[] getCameraSubscribers() {
        return cameraSubscribers;
    }

    public String[] getThiefSubscribers() {
        return thiefSubscribers;
    }

    private static String[] addSubscriber(String[] oldList, String newPhoneNumber) {
        if (isContain(oldList, newPhoneNumber))
            return oldList;

        if (oldList == null)
            return new String[] {newPhoneNumber};
        else {
            String[] newList = new String[oldList.length + 1];
            System.arraycopy(oldList, 0, newList, 0, oldList.length);
            newList[oldList.length] = newPhoneNumber;

            return newList;
        }
    }

    private static String[] removeSubscriber(String[] list, String phoneNumberToRemove) {
        int indexToRemove = indexOf(list, phoneNumberToRemove);

        if (indexToRemove == -1)
            return list;

        String[] newList = new String[list.length - 1];
        for (int i = 0; i < list.length; ++i) {
            if (i < indexToRemove)
                newList[i] = list[i];
            else if (i > indexToRemove)
                newList[i - 1] = list[i];
        }

        return newList;
    }

    public void addPowerSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        powerSubscribers = addSubscriber(powerSubscribers, phoneNumber);
    }

    public void addCameraSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        cameraSubscribers = addSubscriber(cameraSubscribers, phoneNumber);
    }

    public void addThiefSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        thiefSubscribers = addSubscriber(thiefSubscribers, phoneNumber);
    }

    public void removePowerSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        powerSubscribers = removeSubscriber(powerSubscribers, phoneNumber);
    }

    public void removeCameraSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        cameraSubscribers = removeSubscriber(cameraSubscribers, phoneNumber);
    }

    public void removeThiefSubscriber(String phoneNumber) {
        subscriberListUpdateDate = new Date();
        thiefSubscribers = removeSubscriber(thiefSubscribers, phoneNumber);
    }

    public Date getSubscriberListUpdateDate()
    {
        return subscriberListUpdateDate;
    }

    public void setDeviceAccount(double deviceAccount)
    {
        // update the date
        deviceAccountUpdateDate = new Date();

        // update device account
        this.deviceAccount = deviceAccount;
    }

    public double getDeviceAccount()
    {
        return deviceAccount;
    }

    public Date getDeviceAccountUpdateDate()
    {
        return deviceAccountUpdateDate;
    }

    private static boolean isContain(String[] list, String phoneNumber) {
        if (list == null)
            return false;

        for (int i = 0; i < list.length; ++i)
            if (list[i].equals(phoneNumber))
                return true;

        return false;
    }

    private static int indexOf(String[] list, String phoneNumber) {
        for (int i = 0; i < list.length; ++i)
            if (list[i].equals(phoneNumber))
                return i;

        return -1;
    }
}
