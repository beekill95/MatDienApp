package com.example.beekill.matdienapp.protocol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by beekill on 8/23/16.
 */
public class Notification implements Parcelable {
    private boolean powerOn;
    private boolean cameraOn;
    private boolean haveTheif;

    public Notification() {
        powerOn = false;
        cameraOn = false;
        haveTheif = false;
    }

    public Notification(boolean powerOn, boolean cameraOn, boolean haveTheif)
    {
        this.powerOn = powerOn;
        this.haveTheif = haveTheif;
        this.cameraOn = cameraOn;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public boolean isCameraOn() {
        return cameraOn;
    }

    public boolean isHaveTheif() {
        return haveTheif;
    }

    private Notification(Parcel in) {
        boolean values[] = new boolean[3];
        in.readBooleanArray(values);

        powerOn = values[0];
        cameraOn = values[1];
        haveTheif = values[2];
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }

    public void setCameraOn(boolean cameraOn) {
        this.cameraOn = cameraOn;
    }

    public void setHaveTheif(boolean haveTheif) {
        this.haveTheif = haveTheif;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        boolean values[] = {powerOn, cameraOn, haveTheif};
        out.writeBooleanArray(values);
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
