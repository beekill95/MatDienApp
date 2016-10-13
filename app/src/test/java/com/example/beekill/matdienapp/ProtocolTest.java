package com.example.beekill.matdienapp;

/**
 * Created by beekill on 10/11/16.
 */

import com.example.beekill.matdienapp.protocol.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ProtocolTest {

    private Protocol protocol;

    private static final String PASSWORD = "password";

    public ProtocolTest() {
        protocol = new Protocol();
    }

    @Test
    public void testGetSubscriberListMessage() {
        final String STATUS = "Power";

        final String expResult = "{\"action\":\"GetSubscriberList\",\"status\":\"Power\"}";
        String result = protocol.getSubscriberListMessage(STATUS);

        assertEquals(expResult, result);
    }
}
