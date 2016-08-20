package com.example.beekill.matdienapp;

import com.example.beekill.matdienapp.hash.Hashing;
import com.example.beekill.matdienapp.hash.HashingPBKDF2;

import org.junit.Test;

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