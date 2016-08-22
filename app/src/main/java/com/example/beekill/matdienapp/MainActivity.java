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
import android.widget.EditText;

import com.example.beekill.matdienapp.communication.TextSMSCommunication;
import com.example.beekill.matdienapp.hash.Hashing;
import com.example.beekill.matdienapp.hash.HashingPBKDF2;
import com.example.beekill.matdienapp.communication.DeviceCommunication;
import com.example.beekill.matdienapp.protocol.Protocol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements DeviceCommunication.ReceivedDataHandler{
    private static final String USER_LOGIN_FILENAME = "users_login_info";
    private static final String PREF_NAME = "MatDienApp";
    private SharedPreferences sharedPreferences;
    private DeviceCommunication deviceCommunication;
    private Protocol protocol;

    private Boolean isWaitingReponse = false;
    private Button signOutButton;
    private Button addSubscriberButton;
    private EditText phoneEditText;

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Test", "I am testing something here");
        editor.commit();
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
        signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSignOutButtonClicked(v);
                    }
                }
        );

        // add subcriber button
        addSubscriberButton = (Button) findViewById(R.id.addSubscriberButton);
        addSubscriberButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onAddSubscriberButtonClicked(view);
                    }
                }
        );

        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        // to communicate with devices
        deviceCommunication = new TextSMSCommunication();
        deviceCommunication.registerHandler(this);
        deviceCommunication.registerDataReceiverToAndroid(this);

        // initialize protocol
        protocol = new Protocol();
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

    private void onAddSubscriberButtonClicked(View v)
    {
        // Get phone number
        String phoneNumber = phoneEditText.getText().toString();

        // check whether phone number is empty
        if (phoneNumber.equals(""))
            return;

        // if not, send the message
        sendAddSubscriberMessage(phoneNumber);
    }

    private void sendAddSubscriberMessage(String phoneNumber)
    {
        // produce an add subcriber message
        // temporarily method
        String message = protocol.addSubscriberMessage(
                UserInformation.getInstance().getInformationOf("admin")[1],
                phoneNumber);

        Log.i(PREF_NAME, "The message is " + message);

        // send the message to device
        //deviceCommunication.send(message, "so cua device cua Truc");

        // set waiting state
        isWaitingReponse = true;

        // grey out ui components
        signOutButton.setEnabled(false);
        addSubscriberButton.setEnabled(false);
        phoneEditText.setEnabled(false);
    }

    @Override
    public void handle(String data) {
        Log.i(PREF_NAME, "Receive in activity " + data);

        if (isWaitingReponse) {
            isWaitingReponse = false;

            signOutButton.setEnabled(true);
            addSubscriberButton.setEnabled(true);
            phoneEditText.setEnabled(true);
        }
    }
}
