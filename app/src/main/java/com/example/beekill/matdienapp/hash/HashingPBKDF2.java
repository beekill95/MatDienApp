package com.example.beekill.matdienapp.hash;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by beekill on 7/21/16.
 */
public class HashingPBKDF2 extends Hashing {
    final int iterations = 4096;
    final int saltLength = 128;
    final int keyLength = 256;

    @Override
    public void hash(String password, String[] hashedPassword) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(secureRandom.generateSeed(1));

        byte[] saltBytes = new byte[saltLength / 8];
        secureRandom.nextBytes(saltBytes);

        // store the salt value
        hashedPassword[0] = new String(saltBytes);
        hashedPassword[1] = hash(password, hashedPassword[0]);
    }

    @Override
    public String hash(String password, String salt) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, keyLength);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);

            return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }


}
