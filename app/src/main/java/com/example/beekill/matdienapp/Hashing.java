package com.example.beekill.matdienapp;

/**
 * Created by beekill on 7/21/16.
 */
// Abstract class to hash password
public abstract class Hashing {
    // to get hashed password and salt
    public abstract void hash(String password, String[] hashedPassword);

    // to get hashed password for a given password and salt
    public abstract String hash(String password, String salt);
}
