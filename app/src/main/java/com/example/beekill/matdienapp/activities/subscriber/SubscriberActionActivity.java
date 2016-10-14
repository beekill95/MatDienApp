package com.example.beekill.matdienapp.activities.subscriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.beekill.matdienapp.MatDienApplication;
import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.communication.BluetoothCommunication;
import com.example.beekill.matdienapp.communication.CommunicationManager;
import com.example.beekill.matdienapp.communication.DeviceCommunication;
import com.example.beekill.matdienapp.communication.QueueManager;

import java.util.List;

public class SubscriberActionActivity extends AppCompatActivity
    implements CommunicationManager.ResultReceivedHandler,
        BluetoothCommunication.BluetoothStatusHandler
{

    // the device bluetooth address and subscriber's password
    private String deviceBluetoothAddress;
    private String subscriberPassword;

    // bluetooth connection
    private BluetoothCommunication communication;
    private CommunicationManager queueManager;
    private List<Pair<Integer, String>> pendingActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // get the data in the intent
        Intent startIntent = getIntent();
        deviceBluetoothAddress = startIntent.getStringExtra("deviceBluetoothAddress");
        subscriberPassword = startIntent.getStringExtra("userPassword");

        // get reference to bluetooth communication
        communication = (BluetoothCommunication) ((MatDienApplication) getApplication()).getDeviceCommunication();
        communication.registerBluetoothStatusHandler(this);

        queueManager = new QueueManager(this, communication);
        queueManager.setHandler(this);
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

        } else if (id == R.id.action_sign_out) {

        }

        return super.onOptionsItemSelected(item);
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

        // TODO: what to do here
    }
}
