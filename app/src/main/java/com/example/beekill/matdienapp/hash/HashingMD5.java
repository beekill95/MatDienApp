package com.example.beekill.matdienapp.hash;

/**
 * Created by beekill on 10/2/16.
 */
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingMD5 extends Hashing {
    @Override
    public void hash(String password, String[] hashedPassword) {
        hashedPassword[0] = "";
        hashedPassword[1] = hash(password, "");
    }

    @Override
    public String hash(String password, String salt) {
        try {
            byte[] bytesOfPassword = password.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(bytesOfPassword);

            return Base64.encodeToString(hashedBytes, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) { }
        catch (NoSuchAlgorithmException e) { }

        return null;
    }
}
