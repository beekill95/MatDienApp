package com.example.beekill.matdienapp.activities.Admin;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.UserInformation;
import com.example.beekill.matdienapp.activities.Admin.*;
import com.example.beekill.matdienapp.communication.CommunicationManager;
import com.example.beekill.matdienapp.communication.QueueManager;
import com.example.beekill.matdienapp.communication.TextSMSCommunication;
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
        CommunicationManager.ResultReceivedHandler
{
    private AdminData adminData;
    private CommunicationManager queueManager;

    private List<Pair<Integer, AdminAction>> pendingActions;
    private AdminProtocol adminProtocol;

    private static final String DataFileName = "AdminActionData.data";

    private final String deviceAddress = "6505551212";
    private AdminActionResultReceivedHandler subscriberFragmentHandler;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_action);

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

        // read the previously stored data (if possible)
        loadAdminData();

        // set up sending/receiving manager
        queueManager = new QueueManager(this, new TextSMSCommunication());
        queueManager.setHandler(this);

        // set up pending actions
        pendingActions = new ArrayList<>();

        // set up admin protocol
        adminProtocol = new Protocol();
    }

    @Override
    protected void onDestroy() {
        saveAdminData();
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
            return true;
        } else if (id == R.id.action_sign_out) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                SubscriberFragment subscriberFragment = SubscriberFragment.newInstance("hello", "quan");
                subscriberFragmentHandler = (AdminActionResultReceivedHandler) subscriberFragment;

                return subscriberFragment;
            } else
                return PhoneAccountFragment.newInstance("hello", "quan");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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
            case CHANGE_PASSWORD:
                sendChangePasswordMessage(args);
                break;
            case LIST_SUBSCRIBER:
                sendListSubscriberMessage(args);
                break;
        }
    }

    private void sendAddSubscriberMessage(Bundle args)
    {

    }

    private void sendDelSubscriberMessage(Bundle args)
    {

    }

    private void sendRechargeDeviceAccountMessage(Bundle args)
    {

    }

    private void sendGetDeviceAccountMessage(Bundle args)
    {

    }

    private void sendChangePasswordMessage(Bundle args)
    {

    }

    private void sendListSubscriberMessage(Bundle args)
    {
        String message = adminProtocol.getSubscriberListMessage(UserInformation.getInstance().getInformationOf("admin")[1]);

        int messageId = queueManager.enqueueMessageToSend(message, deviceAddress);
        pendingActions.add(Pair.create(messageId, AdminAction.LIST_SUBSCRIBER));
    }

    @Override
    public void handleMessageReceived(String message, String fromAddress, int messageId) {
        // checking whether the message if from the device
        if (!deviceAddress.equals(fromAddress))
            throw new RuntimeException("Received message not from expected device");

        // search for pair that match messageId
        Pair<Integer, AdminAction> actionPair = null;
        for (Pair<Integer, AdminAction> pair: pendingActions)
            if (pair.first == messageId) {
                actionPair = pair;
                break;
            }

        // handle the corresponding action
        switch(actionPair.second) {
            case ADD_SUBSCRIBER:
                break;
            case DEL_SUBSCRIBER:
                break;
            case LIST_SUBSCRIBER:
                handleReceiveListSubscriber(message);
                break;
            case CHANGE_PASSWORD:
                break;
            case GET_DEVICE_ACCOUNT:
                break;
            case RECHARGE_DEVICE_ACCOUNT:
                break;
        }

        // remove the pair
        pendingActions.remove(actionPair);
    }

    private void handleReceiveListSubscriber(String message)
    {
        // get the message
        Response response = adminProtocol.getResponse(message);

        if (response.getResult()) {
            // update admin data
            adminData.setSubscriberList(response.getList());
        }

        subscriberFragmentHandler.handleResult(response.getResult(), adminData, AdminAction.LIST_SUBSCRIBER);
    }
}
