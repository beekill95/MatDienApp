package com.example.beekill.matdienapp.activities;

/**
 * Created by beekill on 7/19/16.
 * Log In activity
 */
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beekill.matdienapp.MatDienApplication;
import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.activities.admin.AdminActionActivity;
import com.example.beekill.matdienapp.activities.subscriber.SubscriberActionActivity;
import com.example.beekill.matdienapp.communication.BluetoothCommunication;
import com.example.beekill.matdienapp.communication.DeviceCommunication;
import com.example.beekill.matdienapp.hash.Hashing;
import com.example.beekill.matdienapp.hash.HashingPBKDF2;
import com.example.beekill.matdienapp.protocol.Protocol;
import com.example.beekill.matdienapp.protocol.Response;
import com.example.beekill.matdienapp.protocol.SubscriberProtocol;

public class LogInActivity extends AppCompatActivity
    implements BluetoothCommunication.BluetoothStatusHandler,
        DeviceCommunication.ReceivedDataHandler
{
    private static final int ENABLE_BLUETOOTH_REQUEST = 1;
    private static final String[] defaultPasswords = {
            "admin", "subscriber"
    };
    public static final String IS_LOGGED_IN_STR = "isLoggedIn";
    public static final String USER_LOGGED_IN_STR = "userLoggedIn";
    public static final String USER_PASSWORD_STR = "userPassword";

    private boolean isLoggedIn;
    private String userPassword;
    private String deviceBluetoothAddress;
    BluetoothCommunication bluetoothCommunication;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // GUI stuffs
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        if (isDefaultPassword(password))
                            login(username, password, false);
                        else
                            login(username, password, true);
                    }
                }
        );

        // get bluetooth device address
        if (getIntent() != null) {
            deviceBluetoothAddress = getIntent().getStringExtra("bluetoothAddress");
        } else {
            //deviceBluetoothAddress = "98:4F:EE:04:3E:28";
            deviceBluetoothAddress = "18:CF:5E:CB:96:5C";
        }
        // initiate bluetooth communication
        bluetoothCommunication = new BluetoothCommunication();
        bluetoothCommunication.registerBluetoothStatusHandler(this);
        bluetoothCommunication.registerHandler(this);
        initBluetooth();

        // check whether we are logged in
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void initBluetooth()
    {
        // initialize bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Your phone doesn't have bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        // enable bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_REQUEST);
        } else
            // initialize bluetooth connection
            bluetoothCommunication.initiateConnection(deviceBluetoothAddress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK)
                // start initialize bluetooth connection
                bluetoothCommunication.initiateConnection(deviceBluetoothAddress);
            else
                Toast.makeText(this, "Cannot initialize connection to the device", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isDefaultPassword(String password) {
        for (String pass : defaultPasswords)
            if (pass.equals(password))
                return true;

        return false;
    }

    private void login(String username, final String password, boolean shouldBeEncrypt)
    {
        if (!shouldBeEncrypt)
            userPassword = password;
        else
            userPassword = password;

        // Compose message to send
        SubscriberProtocol protocol = new Protocol();
        String message = protocol.sessionInitialization(username, userPassword);

        // send message
        bluetoothCommunication.send(message, deviceBluetoothAddress);

        // change the button to disable
        loginButton.setEnabled(false);
        loginButton.setText(R.string.logging_in);
    }

    private String hashPassword(String salt, String password)
    {
        Hashing hashing = new HashingPBKDF2();
        return hashing.hash(password, salt);
    }

    @Override
    public void handleDeviceConnectionResult(boolean isConnected) {
        if (isConnected) {
            loginButton.setEnabled(true);
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();

            if (isLoggedIn) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                // we are currently logged in
                String username = sharedPreferences.getString("userLoggedIn", "subscriber");
                String password = sharedPreferences.getString("userPassword", "subscriber");
                usernameEditText.setText(username);

                login(username, password, false);
            }

        }
        else
            Toast.makeText(this, "Cannot connect", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleDeviceConnectionLost() {
        loginButton.setEnabled(false);
        Toast.makeText(this, "Connection lost", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handle(String data, String fromAddress) {
        SubscriberProtocol protocol = ((MatDienApplication) getApplication()).getSubscriberProtocol();

        if (fromAddress.equals(deviceBluetoothAddress)) {
            // only process data received from known device
            Response response = protocol.getResponse(data);

            // ignore null pointer
            if (response == null)
                return;

            if (response.getResult())
                onSessionInitiated();
            else {
                Toast.makeText(this, response.getDescription(), Toast.LENGTH_LONG).show();

                loginButton.setEnabled(true);
                loginButton.setText(R.string.log_in);
            }
        }
    }

    private void onSessionInitiated()
    {
        // remove the handler from bluetooth communication
        bluetoothCommunication.unregisterBluetoothStatusHandler(this);
        bluetoothCommunication.unregisterHandler(this);

        // store bluetooth communication object to application
        MatDienApplication app = (MatDienApplication) getApplication();
        app.storeConnection(bluetoothCommunication);

        // start admin activity or subscriber activity accordingly
        if (usernameEditText.getText().toString().equals("admin")) {
            Intent startAdminActivityIntent = new Intent(LogInActivity.this, AdminActionActivity.class);

            putBluetoothAddressAndPassword(startAdminActivityIntent, deviceBluetoothAddress, userPassword);
            startActivity(startAdminActivityIntent);
        } else {
            // subscriber activity
            Intent startSubscriberActivityIntent = new Intent(LogInActivity.this, SubscriberActionActivity.class);

            putBluetoothAddressAndPassword(startSubscriberActivityIntent, deviceBluetoothAddress, userPassword);
            startActivity(startSubscriberActivityIntent);
        }

        // store logged int state
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN_STR, true);
        editor.putString(USER_LOGGED_IN_STR, usernameEditText.getText().toString());
        editor.putString(USER_PASSWORD_STR, userPassword);
        editor.apply();

        // end this activity
        finish();
    }

    private static void putBluetoothAddressAndPassword(Intent intent, String bluetoothAddress, String userPassword) {
        intent.putExtra("deviceBluetoothAddress", bluetoothAddress);
        intent.putExtra("userPassword", userPassword);
    }

    public static void signout(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext()
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LogInActivity.IS_LOGGED_IN_STR, false);
        editor.putString(LogInActivity.USER_LOGGED_IN_STR, "");
        editor.putString(LogInActivity.USER_PASSWORD_STR, "");
        editor.apply();
    }
}
