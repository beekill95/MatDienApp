package com.example.beekill.matdienapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Hashing hashing = new HashingPBKDF2();

        String hashedPassword = hashing.hash("password", "salt");
        System.out.println("Hashed password: " + hashedPassword);
    }
}