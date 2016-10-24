package com.example.beekill.matdienapp.activities.subscriber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.beekill.matdienapp.activities.LogInActivity;
import com.example.beekill.matdienapp.MatDienApplication;
import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.communication.BluetoothCommunication;
import com.example.beekill.matdienapp.communication.CommunicationManager;
import com.example.beekill.matdienapp.communication.QueueManager;
import com.example.beekill.matdienapp.helper.ObjectSerializerHelper;
import com.example.beekill.matdienapp.protocol.Response;
import com.example.beekill.matdienapp.protocol.SubscriberProtocol;
import com.example.beekill.matdienapp.protocol.SubscriptionType;

import java.util.ArrayList;
import java.util.List;

public class SubscriberActionActivity extends AppCompatActivity
    implements CommunicationManager.ResultReceivedHandler,
        BluetoothCommunication.BluetoothStatusHandler
{

    private enum SubscriberAction {
        SUBSCRIBE, UNSUBSCRIBE, UPDATE_STATUS
    }

    // the device bluetooth address and subscriber's password
    private String deviceBluetoothAddress;
    private String subscriberPassword;
    private String thisPhoneNumber;

    // bluetooth connection
    private BluetoothCommunication communication;
    private CommunicationManager queueManager;
    private List<Pair<Integer, Pair<SubscriberAction, String>>> pendingActions;

    // subscriber protocol
    private SubscriberProtocol protocol;

    // subscriber data
    private ArrayList<String> statusSubscribed;
    private ArrayAdapter statusSubscribedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the data in the intent
        Intent startIntent = getIntent();
        deviceBluetoothAddress = startIntent.getStringExtra("deviceBluetoothAddress");
        subscriberPassword = startIntent.getStringExtra("userPassword");

        // get reference to bluetooth communication
        communication = (BluetoothCommunication) ((MatDienApplication) getApplication()).getDeviceCommunication();
        communication.registerBluetoothStatusHandler(this);

        queueManager = new QueueManager(this, communication);
        queueManager.setHandler(this);

        // get reference to subscriber protocol
        protocol = ((MatDienApplication) getApplication()).getSubscriberProtocol();

        // subscribe to a device's status
        FloatingActionButton subscribeStatusButton = (FloatingActionButton) findViewById(R.id.subscribeStatusButton);
        subscribeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubscribeStatusDialogForResult();
            }
        });

        pendingActions = new ArrayList<>();
        loadSubscriberData();

        // display status this subscriber subscribe to
        ListView listView = (ListView) findViewById(R.id.statusSubscribedListView);
        statusSubscribedAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, statusSubscribed);
        listView.setAdapter(statusSubscribedAdapter);

        // add unsubscribe command
        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final int statusIndexToDelete = i;
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SubscriberActionActivity.this);

                        alertDialogBuilder
                                .setMessage("Do you want to unsubscribe from " + statusSubscribed.get(statusIndexToDelete) + " ?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int c) {
                                        deleteStatus(statusIndexToDelete);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();

                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subscriber_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_update_status) {
            queryDeviceStatus();
        } else if (id == R.id.action_sign_out) {
            signout();
        }

        return true;
    }

    @Override
    public void handleDeviceConnectionResult(boolean isConnected) {
        // do nothing
    }

    @Override
    public void handleDeviceConnectionLost() {
        Toast.makeText(this, "Connection Lost", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleMessageReceived(String message, String fromAddress, int messageId) {
        // checking whether the message if from the device
        if (!deviceBluetoothAddress.equals(fromAddress))
            throw new RuntimeException("Received message not from expected device");


        // search for pair that match messageId
        Pair<Integer, Pair<SubscriberAction, String>> actionPair = null;
        for (Pair<Integer, Pair<SubscriberAction, String>> pair: pendingActions)
            if (pair.first == messageId) {
                actionPair = pair;
                break;
            }

        // handle the corresponding action
        switch(actionPair.second.first) {
            case SUBSCRIBE:
                handleReceivedSubscribeCommandResult(message, actionPair.second.second);
                break;
            case UNSUBSCRIBE:
                break;
            case UPDATE_STATUS:
                break;
        }

        // remove the pair
        pendingActions.remove(actionPair);
    }

    @Override
    protected void onPause() {
        // save subscriber data
        saveSubscriberData();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // stop bluetooth
        communication.terminateConnection();

        // remove register
        communication.unregisterBluetoothStatusHandler(this);

        // remove queue manager
        queueManager.removeHandler(this, this);

        super.onDestroy();
    }

    private void queryDeviceStatus() {

    }

    private void signout() {
        // sign out
        LogInActivity.signout(this);

        // finish this activity
        finish();
    }

    private void showSubscribeStatusDialogForResult() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View phoneNumberView = layoutInflater.inflate(R.layout.dialog_addsubscriber, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(phoneNumberView);

        final EditText phoneNumberEditText = (EditText) phoneNumberView.findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setText(thisPhoneNumber);
        final Spinner subscriptionTypeSpinner = (Spinner) phoneNumberView.findViewById(R.id.subscriptionTypeSpinner);

        // add items to spinner
        List<String> list = new ArrayList<>();
        list.add(SubscriptionType.Camera.getValue());
        list.add(SubscriptionType.Power.getValue());
        list.add(SubscriptionType.Thief.getValue());
        list.add(SubscriptionType.All.getValue());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subscriptionTypeSpinner.setAdapter(adapter);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String phoneNumber = phoneNumberEditText.getText().toString();
                                String subscriptionType = subscriptionTypeSpinner.getSelectedItem().toString();

                                sendSubscribeCommand(subscriptionType, phoneNumber);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void sendSubscribeCommand(String status, String thisPhoneNumber) {
        this.thisPhoneNumber = thisPhoneNumber;
        String message = protocol.addSubscription(status, thisPhoneNumber);

        int messageId = queueManager.enqueueMessageToSend(message, deviceBluetoothAddress);
        pendingActions.add(Pair.create(messageId, Pair.create(SubscriberAction.SUBSCRIBE, status)));
    }

    private void handleReceivedSubscribeCommandResult(String response, String status) {
        Response resp = protocol.getResponse(response);

        if (resp.getResult()) {
            statusSubscribed.add(status);
            statusSubscribedAdapter.notifyDataSetChanged();
        }

        Toast.makeText(this, resp.getDescription(), Toast.LENGTH_LONG).show();
    }

    private static final String STATUS_SUBSCRIBEB_STR = "statusSubscribed";
    private static final String THIS_PHONE_NUMBER_STR = "thisPhoneNumber";
    private void saveSubscriberData() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        String statusSubscribedEncoded = ObjectSerializerHelper.objectToString(statusSubscribed);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(deviceBluetoothAddress, statusSubscribedEncoded);
        editor.putString(THIS_PHONE_NUMBER_STR, thisPhoneNumber);
        editor.apply();
    }

    private void loadSubscriberData() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        if (sharedPreferences.contains(deviceBluetoothAddress)) {
            String encoded = sharedPreferences.getString(deviceBluetoothAddress, null);
            thisPhoneNumber = sharedPreferences.getString(THIS_PHONE_NUMBER_STR, null);

            statusSubscribed = (ArrayList<String>) ObjectSerializerHelper.stringToObject(encoded);
        } else {
            thisPhoneNumber = "";
            statusSubscribed = new ArrayList<>();
        }
    }

    private void deleteStatus(int statusIndex) {
        statusSubscribed.remove(statusIndex);

        statusSubscribedAdapter.notifyDataSetChanged();
    }
}
