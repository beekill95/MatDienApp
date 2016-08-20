package com.example.beekill.matdienapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import com.example.beekill.matdienapp.communication.TextSMSCommunication;
import com.example.beekill.matdienapp.hash.Hashing;
import com.example.beekill.matdienapp.hash.HashingPBKDF2;
import com.example.beekill.matdienapp.communication.DeviceCommunication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements DeviceCommunication.ReceivedDataHandler{
    private static final String USER_LOGIN_FILENAME = "users_login_info";
    private static final String PREF_NAME = "MatDienApp";
    private SharedPreferences sharedPreferences;
    private DeviceCommunication deviceCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load users' login information
        loadUserLoginInformation();

        // add some users to our database
        addUser("quan", "51303225");
        addUser("tung", "NguyenMaiThanh");

        // check if user is login
        //SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        if (!isLogin) {
            // show login activity
            Intent i = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(i);

            finish(); // prevent user from using back button to return to this activity
            return;
        }

        setContentView(R.layout.activity_main);

        // log out button
        Button signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSignOutButtonClicked(v);
                    }
                }
        );

        // to communicate with devices
        deviceCommunication = new TextSMSCommunication();
        deviceCommunication.registerHandler(this);
        deviceCommunication.registerDataReceiverToAndroid(this);
    }

    @Override
    protected void onDestroy() {
        // saving the users login information
        saveUserLoginInformation();

        // remove device communicate with devices
        if (deviceCommunication != null) {
            deviceCommunication.unregisterHandler(this);
            deviceCommunication.unregisterDataReceiverToAndroid(this);
        }

        super.onDestroy();
    }

    private void loadUserLoginInformation()
    {
        FileInputStream fin = null;
        try {
            // open file
            fin = openFileInput(USER_LOGIN_FILENAME);

            // read previous users' login information
            Log.i(PREF_NAME, "Reading users login information");
            UserInformation.getInstance().loadUserInformation(fin);

        } catch (FileNotFoundException e) {
            // the first time user launch the application
            Log.i(PREF_NAME, "Initialize default login information");
            initializeDefaultLoginInformation();

        } catch (IOException e) {
            Log.i(PREF_NAME, "Error: Cannot initialize object input stream!");

        } catch (ClassNotFoundException e) {
            Log.i(PREF_NAME, "Error: Wrong users login information file");
        }

        // close file if necessary
        Log.i(PREF_NAME, "Reading finish, Closing file");
        try {
           if (null != fin)
               //Log.i(PREF_NAME, "Checking fin khac null dung");
               fin.close();

            //Log.i(PREF_NAME, "Checking fin khac null hoan tat");

        } catch (IOException e) {
            Log.i(PREF_NAME, "Error: Cannot close file after reading!");
        }
    }

    private void saveUserLoginInformation()
    {
        try {
            // open file to write
            FileOutputStream fout = openFileOutput(USER_LOGIN_FILENAME, Context.MODE_PRIVATE);

            // save users login information
            Log.i(PREF_NAME, "Saving users information");
            UserInformation.getInstance().saveUserInformation(fout);

            // close file
            Log.i(PREF_NAME, "Writing finish, Closing file");
            fout.close();
        } catch (FileNotFoundException e) {
            Log.i(PREF_NAME, "Error: Cannot open file to write users login information");
        } catch (IOException e) {
            Log.i(PREF_NAME, "Error: Cannot close file after writing");
        }
    }

    private void initializeDefaultLoginInformation()
    {
        String[] password = {"", "password"};
        UserInformation.getInstance().setInformationOf("admin", password);
    }

    private void addUser(String username, String password)
    {
        String[] hashedPassword;
        // check whether the user has been registered
        hashedPassword = UserInformation.getInstance().getInformationOf(username);
        if (hashedPassword != null)
            return;

        // if not, store that user into our database
        Hashing hashing = new HashingPBKDF2();

        hashedPassword = new String[2];
        hashing.hash(password, hashedPassword);

        UserInformation.getInstance().setInformationOf(username, hashedPassword);
    }

    private void onSignOutButtonClicked(View v)
    {
        // change the isLogin value to false
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLogin", false);
        editor.commit();

        // change to log in activity
        Intent i = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(i);

        // finish this activity
        finish();
    }

    @Override
    public void handle(String data) {
        Log.i(PREF_NAME, "Receive in activity " + data);
    }
}
