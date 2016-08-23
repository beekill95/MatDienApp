package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/23/16.
 */
public class Notification {
    private boolean powerOn;
    private boolean cameraOn;
    private boolean haveTheif;

    Notification(boolean powerOn, boolean cameraOn, boolean haveTheif)
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
}
