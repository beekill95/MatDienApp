package com.example.beekill.matdienapp.activities.admin;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by beekill on 9/5/16.
 */
public class AdminData implements Serializable
{
    private Date subscriberListUpdateDate;
    private String[] subscriberList;

    private Date deviceAccountUpdateDate;
    private double deviceAccount;

    public AdminData()
    {
        subscriberList = null;
        subscriberListUpdateDate = null;

        deviceAccountUpdateDate = null;
        deviceAccount = -1;
    }

    public void setSubscriberList(String[] subscriberList)
    {
        // update the date
        subscriberListUpdateDate = new Date();

        // update subscriber list
        this.subscriberList = subscriberList;
    }

    public Date getSubscriberListUpdateDate()
    {
        return subscriberListUpdateDate;
    }

    public String[] getSubscriberList()
    {
        return subscriberList;
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
}
