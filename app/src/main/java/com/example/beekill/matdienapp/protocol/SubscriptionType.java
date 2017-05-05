package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public enum SubscriptionType {
    None("None"), Power("Power"), Camera("Camera"), Thief("Thief"), All("All"), Temp("Temp");

    private final String value;

    SubscriptionType(String value)
    {
        this.value = value;
    }

    final public String getValue()
    {
        return value;
    }
}
