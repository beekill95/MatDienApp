package com.example.beekill.matdienapp.activities.Admin;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.UserInformation;
import com.example.beekill.matdienapp.activities.ChangePasswordActivity;
import com.example.beekill.matdienapp.communication.BluetoothCommunication;
import com.example.beekill.matdienapp.communication.CommunicationManager;
import com.example.beekill.matdienapp.communication.QueueManager;
import com.example.beekill.matdienapp.communication.TextSMSCommunication;
import com.example.beekill.matdienapp.hash.Hashing;
import com.example.beekill.matdienapp.hash.HashingPBKDF2;
import com.example.beekill.matdienapp.protocol.AdminProtocol;
import com.example.beekill.matdienapp.protocol.Protocol;
import com.example.beekill.matdienapp.protocol.Response;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminActionActivity extends AppCompatActivity
    implements SubscriberFragment.OnFragmentInteractionListener,
        PhoneAccountFragment.OnFragmentInteractionListener,
        CommunicationManager.ResultReceivedHandler,
        BluetoothCommunication.BluetoothStatusHandler
{
    static final int CHANGE_PASSWORD_REQUEST = 7777;
    static final int ENABLE_BLUETOOTH_REQUEST = 7788;

    private AdminData adminData;
    private CommunicationManager queueManager;

    private List<Pair<Integer, Pair<AdminAction, Bundle>>> pendingActions;
    private AdminProtocol adminProtocol;

    private static final String DataFileName = "AdminActionData.data";

    //private final String deviceAddress = "6505551212";
    private String deviceAddress;
    private AdminFragmentCommonInterface subscriberFragmentHandler;
    private AdminFragmentCommonInterface phoneAccountFragmentHandler;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    // for bluetooth
    //private static final String deviceBluetoothAddress = "18:CF:5E:CB:96:5C";
    private static final String deviceBluetoothAddress = "98:4F:EE:04:3E:28";
    private BluetoothCommunication bluetoothCommunication;

    // password
    private String adminPassword = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_action);

        // check whether there is a phone number in the intent

        if (savedInstanceState != null) {
            // restored from previous state
            deviceAddress = savedInstanceState.getString("deviceAddress");
        } else {
            // newly created
            if (getIntent() != null) {
                Intent intent = getIntent();

                deviceAddress = intent.getStringExtra("deviceAddress");
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // set up pending actions
        pendingActions = new ArrayList<>();

        // set up admin protocol
        adminProtocol = new Protocol();

        // start bluetooth
        bluetoothCommunication = new BluetoothCommunication();
        bluetoothCommunication.registerBluetoothStatusHandler(this);
        deviceAddress = deviceBluetoothAddress;

        // initiate bluetooth connection
        initializeBluetoothConnection();

        // message manager
        queueManager = new QueueManager(this, bluetoothCommunication);
        queueManager.setHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // read the previously stored data (if possible)
        loadAdminData();

        // set up sending/receiving manager
        //queueManager = new QueueManager(this, new TextSMSCommunication());
        //queueManager.setHandler(this);
    }

    @Override
    protected void onPause() {
        saveAdminData();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // stop bluetooth
        bluetoothCommunication.terminateConnection();

        // remove register
        bluetoothCommunication.unregisterBluetoothStatusHandler(this);

        // remove queue manager
        queueManager.removeHandler(this, this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_password) {
            // Start change password activity
            Intent startChangePasswordActivityIntent = new Intent(this, ChangePasswordActivity.class);

            startActivityForResult(startChangePasswordActivityIntent, CHANGE_PASSWORD_REQUEST);
            return true;
        } else if (id == R.id.action_sign_out) {
            //signout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle when we received the result from change password activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // responding to change password request
        if (requestCode == CHANGE_PASSWORD_REQUEST) {
            // only process successful request
            if (resultCode == RESULT_OK) {
                String oldPass = data.getStringExtra("oldPass");
                String newPass = data.getStringExtra("newPass");

                Bundle args = new Bundle();
                args.putString("oldPass", oldPass);
                args.putString("newPass", newPass);

                sendChangePasswordMessage(args);
            }
        } else if (requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK)
                // start intialize bluetooth connection
                bluetoothCommunication.initiateConnection(deviceBluetoothAddress);
            else
                Toast.makeText(this, "Cannot initialize connection to the device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("deviceAddress", deviceAddress);

        super.onSaveInstanceState(outState);
    }

    private void loadAdminData()
    {
        try {
            FileInputStream fin = openFileInput(DataFileName);

            // read the object
            ObjectInputStream objIn = new ObjectInputStream(fin);
            adminData = (AdminData) objIn.readObject();

            // close file after finish reading
            objIn.close();
            fin.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            adminData = new AdminData();
        } catch (IOException e) {
            e.printStackTrace();

            adminData = new AdminData();
        }
    }

    private void saveAdminData()
    {
        try {
            FileOutputStream fout = openFileOutput(DataFileName, Context.MODE_PRIVATE);

            // write the object
            ObjectOutputStream objOut = new ObjectOutputStream(fout);
            objOut.writeObject(adminData);

            // close the file
            objOut.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signout()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        );
    }

    private void initializeBluetoothConnection()
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                SubscriberFragment subscriberFragment = SubscriberFragment.newInstance();
                subscriberFragment.displayData(adminData);
                subscriberFragmentHandler = subscriberFragment;

                return subscriberFragment;
            } else if (position == 1) {
                PhoneAccountFragment phoneAccountFragment = PhoneAccountFragment.newInstance();
                phoneAccountFragment.displayData(adminData);
                phoneAccountFragmentHandler = phoneAccountFragment;

                return phoneAccountFragment;
            } else
                return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Subscriber";
                case 1:
                    return "Device Account";
            }
            return null;
        }
    }

    @Override
    public void onFragmentActionPerform(AdminAction action, Bundle args) {
        switch (action) {
            case ADD_SUBSCRIBER:
                sendAddSubscriberMessage(args);
                break;
            case DEL_SUBSCRIBER:
                sendDelSubscriberMessage(args);
                break;
            case RECHARGE_DEVICE_ACCOUNT:
                sendRechargeDeviceAccountMessage(args);
                break;
            case GET_DEVICE_ACCOUNT:
                sendGetDeviceAccountMessage(args);
                break;
            case LIST_SUBSCRIBER:
                sendListSubscriberMessage(args);
                break;
            default:
                break;
        }
    }

    private void sendAddSubscriberMessage(Bundle args)
    {
        String phoneNumber = args.getString("phoneNumber");
        String subscriptionType = args.getString("subscriptionType");

        String message = adminProtocol.addSubscriberMessage(adminPassword, phoneNumber, subscriptionType);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.ADD_SUBSCRIBER, args)));
    }

    private void sendDelSubscriberMessage(Bundle args)
    {

    }

    private void sendRechargeDeviceAccountMessage(Bundle args)
    {
        String refillCode = args.getString("refillCode");
        String message = adminProtocol.rechargeAccountCreditMessage(adminPassword, refillCode);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.RECHARGE_DEVICE_ACCOUNT, args)));
    }

    private void sendGetDeviceAccountMessage(Bundle args)
    {
        String message = adminProtocol.getAccountCreditMessage(UserInformation.getInstance().getInformationOf("admin")[1]);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.GET_DEVICE_ACCOUNT, args)));
    }

    private void sendChangePasswordMessage(Bundle args)
    {
        String oldPass = args.getString("oldPass");
        String newPass = args.getString("newPass");
        Hashing hashing = new HashingPBKDF2();

        // hash the old pass
        String oldPassSalt = UserInformation.getInstance().getInformationOf("admin")[0];
        oldPass = hashing.hash(oldPass, oldPassSalt);

        // hash the new pass
        String newPassHashed[] = new String[2];
        hashing.hash(newPass, newPassHashed);

        String message = adminProtocol.changeAdminPasswordMessage("admin", oldPass, newPassHashed[1]);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);

        // put everything to args
        args.putString("oldPass", oldPass);
        args.putString("newPassSalt", newPassHashed[0]);
        args.putString("newPass", newPassHashed[1]);

        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.CHANGE_PASSWORD, args)));
    }

    private void sendListSubscriberMessage(Bundle args)
    {
        String message = adminProtocol.getSubscriberListMessage(UserInformation.getInstance().getInformationOf("admin")[1]);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.LIST_SUBSCRIBER, args)));
    }

    private void sendInitiationMessage()
    {
        String message = adminProtocol.sessionInitialization("admin", adminPassword);
        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(AdminAction.SESSION_INITIALIZATION, new Bundle())));
    }

    @Override
    public void handleMessageReceived(String message, String fromAddress, int messageId) {


        // checking whether the message if from the device
        if (!deviceAddress.equals(fromAddress))
            throw new RuntimeException("Received message not from expected device");

        // search for pair that match messageId
        Pair<Integer, Pair<AdminAction, Bundle>> actionPair = null;
        for (Pair<Integer, Pair<AdminAction, Bundle>> pair: pendingActions)
            if (pair.first == messageId) {
                actionPair = pair;
                break;
            }

        // handle the corresponding action
        switch(actionPair.second.first) {
            case ADD_SUBSCRIBER:
                handleReceivedAddSubscriber(message, actionPair.second.second);
                break;
            case DEL_SUBSCRIBER:
                break;
            case LIST_SUBSCRIBER:
                handleReceiveListSubscriber(message, actionPair.second.second);
                break;
            case CHANGE_PASSWORD:
                handleReceiveChangePassword(message, actionPair.second.second);
                break;
            case GET_DEVICE_ACCOUNT:
                handleReceivedDeviceAccount(message, actionPair.second.second);
                break;
            case RECHARGE_DEVICE_ACCOUNT:
                handleReceivedRechargeDeviceAccount(message, actionPair.second.second);
                break;
            case SESSION_INITIALIZATION:
                handleReceivedSessionInitiation(message);
                break;
        }

        // remove the pair
        pendingActions.remove(actionPair);
    }

    private void handleReceivedAddSubscriber(String message, Bundle args)
    {
        Response response = adminProtocol.getResponse(message);
        Toast.makeText(this, response.getDescription(), Toast.LENGTH_LONG).show();
    }

    private void handleReceivedDeviceAccount(String message, Bundle args)
    {
        // get the message
        Response response = adminProtocol.getResponse(message);

        if (response.getResult()) {
            // update admin data
            String accountCreditString = response.getDescription();
            double accountCredit = Double.parseDouble(accountCreditString);

            adminData.setDeviceAccount(accountCredit);
        }

        phoneAccountFragmentHandler.handleResult(response.getResult(), adminData, AdminAction.GET_DEVICE_ACCOUNT);
    }

    private void handleReceiveListSubscriber(String message, Bundle args)
    {
        // get the message
        Response response = adminProtocol.getResponse(message);

        if (response.getResult()) {
            // update admin data
            adminData.setSubscriberList(response.getList());
        }

        subscriberFragmentHandler.handleResult(response.getResult(), adminData, AdminAction.LIST_SUBSCRIBER);
    }

    private void handleReceivedRechargeDeviceAccount(String message, Bundle args)
    {
        // get the message
        Response response = adminProtocol.getResponse(message);

        if (response.getResult()) {
            // TODO: how to handle the result??
        }

        phoneAccountFragmentHandler.handleResult(response.getResult(), adminData, AdminAction.RECHARGE_DEVICE_ACCOUNT);
    }

    private void handleReceiveChangePassword(String message, Bundle args)
    {
        // get the response
        Response response = adminProtocol.getResponse(message);

        if (response.getResult()) {
            // change password in our database
            String newPass[] = new String[2];
            newPass[0] = args.getString("newPassSalt");
            newPass[1] = args.getString("newPass");

            UserInformation.getInstance().changeInformationOf("admin", newPass);
            new AlertDialog.Builder(this)
                    .setTitle("Successful")
                    .setMessage("Changed password")
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Failed")
                    .setMessage("Cannot change password in device")
                    .show();
        }
    }

    private void handleReceivedSessionInitiation(String message) {
        Response response = adminProtocol.getResponse(message);

        if (response.getResult())
            Toast.makeText(this, "Session Initiated", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, response.getDescription(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleDeviceConnectionResult(boolean isConnected) {
        // TODO: do something like disable gui or enable
        if (isConnected) {
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();

            // send session initiation message
            sendInitiationMessage();
        }
        else
            Toast.makeText(this, "Cannot connect", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleDeviceConnectionLost() {
        // TODO: do something about this
        Toast.makeText(this, "Connection lost", Toast.LENGTH_LONG).show();
    }
}
