package com.example.beekill.matdienapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by beekill on 7/19/16.
 * Store user information
 */

// Singleton class
public class UserInformation {
    private static UserInformation userInformation = null;

    public static UserInformation getInstance()
    {
        if (userInformation == null)
            userInformation = new UserInformation();

        return userInformation;
    }

    private HashMap<String, String[]> usersLoginInformation = null;

    private UserInformation()
    {
        usersLoginInformation = new HashMap<>();
    }

    public void loadUserInformation(FileInputStream fin) throws IOException, ClassNotFoundException
    {
        ObjectInputStream input = new ObjectInputStream(fin);
        usersLoginInformation = (HashMap<String, String[]>) input.readObject();
        input.close();
    }

    public void saveUserInformation(FileOutputStream fout) throws IOException
    {
        ObjectOutputStream output = new ObjectOutputStream(fout);
        output.writeObject(usersLoginInformation);

        output.close();
    }

    public String[] getInformationOf(String username)
    {
        return usersLoginInformation.get(username);
    }

    public boolean setInformationOf(String username, String[] password)
    {
        if (usersLoginInformation.containsKey(username))
            return false;

        usersLoginInformation.put(username, password);
        return true;
    }

    public boolean changeInformationOf(String username, String[] password)
    {
        if (usersLoginInformation.containsKey(username)) {
            usersLoginInformation.put(username, password);
            return true;
        }

        return false;
    }
}
