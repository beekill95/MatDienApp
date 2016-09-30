package com.example.beekill.matdienapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import com.example.beekill.matdienapp.communication.TextSMSCommunication;
import com.example.beekill.matdienapp.communication.DeviceCommunication;
import com.example.beekill.matdienapp.protocol.Protocol;
import com.example.beekill.matdienapp.protocol.Response;
import com.example.beekill.matdienapp.activities.Admin.AdminActionActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    private ThisClassData classData;

    //private static final String devicePhoneNumber = "+841237231353";
    private static final String devicePhoneNumber = "6505551212";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load users' login information
        loadUserLoginInformation();

        // check if user is login
        //SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("devicePhoneNumber", devicePhoneNumber);
        editor.commit();
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        if (!isLogin) {
            // show login activity
            Intent i = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(i);

            finish(); // prevent user from using back button to return to this activity
            return;
        }

        /* For testing tabbeb activity purpose */
        if (true) {
            Intent i = new Intent(MainActivity.this, AdminActionActivity.class);
            i.putExtra("deviceAddress", devicePhoneNumber);
            startActivity(i);

            finish(); // prevent user from using back button to return to this activity
            return;
        }
        /* End testing */

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
        //deviceCommunication = new TextSMSCommunication();
        //deviceCommunication.registerHandler(this);
        //deviceCommunication.registerDataReceiverToAndroid(this);

        // initialize protocol
        protocol = new Protocol();

        // class data
        classData = new ThisClassData();
    }

    @Override
    protected void onPause() {
        // saving the users login information
        saveUserLoginInformation();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
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
        // admin password
        String[] password = {"", "password"};
        UserInformation.getInstance().setInformationOf("admin", password);

        // subscriber password
        UserInformation.getInstance().setInformationOf("subscriber", password);
    }

    /*private void addUser(String username, String password)
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
    }*/

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
                phoneNumber, "Power");

        Log.i(PREF_NAME, "The message is " + message);

        // send the message to device
        deviceCommunication.send(message, "6505551212");

        // set waiting state
        isWaitingReponse = true;

        // grey out ui components
        signOutButton.setEnabled(false);
        addSubscriberButton.setEnabled(false);
        phoneEditText.setEnabled(false);
    }

    @Override
    public void handle(String data, String fromAddress) {
        if (!fromAddress.equals(devicePhoneNumber))
            return;

        Log.i(PREF_NAME, "Receive in activity " + data);

        if (isWaitingReponse) {
            isWaitingReponse = false;

            signOutButton.setEnabled(true);
            addSubscriberButton.setEnabled(true);
            phoneEditText.setEnabled(true);

            // check whether it is an expecting response
            if (protocol.getDeviceResponseType(data) == Protocol.DeviceResponseMessageType.Unknown)
                return;

            // get result
            Response response = protocol.getResponse(data);
            if (response.getResult()) {
                // received an successful message
                // edit the data
                classData.addSubscriber("012934");

                // update
                receivedResponseFromDevice(true, classData);
            } else {
                // update
                receivedResponseFromDevice(false, classData);
            }
        }
    }

    ////////////////// Testing purpose ///////////////////////
    private class ThisClassData
    {
        List<String> subscriberList;

        ThisClassData()
        {
            subscriberList = new ArrayList<>();
        }

        public boolean addSubscriber(String subscriberPhone)
        {
            if (subscriberList.contains(subscriberPhone))
                return false;

            subscriberList.add(subscriberPhone);
            return true;
        }

        public boolean removeSubscriber(String subscriberPhone)
        {
            if (!subscriberList.contains(subscriberPhone))
                return false;

            subscriberList.remove(subscriberPhone);
            return true;
        }

        public List<String> getSubscriberList()
        {
            return subscriberList;
        }
    }

    private void receivedResponseFromDevice(boolean result, ThisClassData thisClassData)
    {
        if (result) {
            // the action is success
            // update the fragment
            new AlertDialog.Builder(this)
                    .setTitle("Received Success Message")
                    .setMessage(thisClassData.toString())
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing
                                }
                            })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Received Failed Message")
                    .setMessage(thisClassData.toString())
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing
                                }
                            })
                    .show();
        }
    }
}
