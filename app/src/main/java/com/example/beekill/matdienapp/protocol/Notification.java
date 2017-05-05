package com.example.beekill.matdienapp.protocol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by beekill on 8/23/16.
 */
public class Notification implements Parcelable {

    private boolean powerOn;
    private boolean cameraOn;
    private boolean haveThief;
    private double temperature;

    public Notification() {
        powerOn = false;
        cameraOn = false;
        haveThief = false;
        temperature = -1;
    }

    public Notification(boolean powerOn, boolean cameraOn, boolean haveThief, double temperature)
    {
        this.powerOn = powerOn;
        this.haveThief = haveThief;
        this.cameraOn = cameraOn;
        this.temperature = temperature;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public boolean isCameraOn() {
        return cameraOn;
    }

    public boolean isHaveThief() {
        return haveThief;
    }

    public double getTemperature() {
        return temperature;
    }

    private Notification(Parcel in) {
        boolean values[] = new boolean[3];
        in.readBooleanArray(values);

        powerOn = values[0];
        cameraOn = values[1];
        haveThief = values[2];
        temperature = in.readDouble();
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }

    public void setCameraOn(boolean cameraOn) {
        this.cameraOn = cameraOn;
    }

    public void setHaveThief(boolean haveTheif) {
        this.haveThief = haveTheif;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        boolean values[] = {powerOn, cameraOn, haveThief};
        out.writeBooleanArray(values);
        out.writeDouble(temperature);
    }

    public static final Parcelable.Creator<Notification> CREATOR
            = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel parcel) {
            return new Notification(parcel);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
