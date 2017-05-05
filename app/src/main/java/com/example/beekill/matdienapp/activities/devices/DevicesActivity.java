package com.example.beekill.matdienapp.activities.devices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.beekill.matdienapp.DeviceInformation;
import com.example.beekill.matdienapp.RecognizedDevices;
import com.example.beekill.matdienapp.activities.LogInActivity;
import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.activities.dialogs.AddDeviceDialog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DevicesActivity extends AppCompatActivity {

    private final static int ADD_DEVICE_REQUEST = 1;
    private static final String DEVICES_DATA_FILE = "devices.data";

    private RecognizedDevices devices;
    GridView gridView;
    DevicesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // load devices information
        loadDevices();

        // display grid view
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new DevicesAdapter(this, devices.getDevices());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == devices.numOfDevices()) {
                            // users press add new device item
                            startAddDeviceDialog();
                        } else {
                            // users touch a device
                            logInDevice(devices.getDevice(i));
                        }
                    }
                }
        );

        gridView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i < devices.numOfDevices()) {
                            deleteDevice(i);
                        }

                        return true;
                    }
                }
        );
    }

    private void startAddDeviceDialog() {
        /*LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialog = layoutInflater.inflate(R.layout.dialog_add_device, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialog);

        final EditText bluetoothAddressEditText = (EditText) dialog.findViewById(R.id.bluetoothAddressEditText);
        final EditText phoneNumberEditText = (EditText) dialog.findViewById(R.id.phoneNumberEditText);
        ImageButton qrDetectButton = (ImageButton) dialog.findViewById(R.id.qrDetectButton);
        qrDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DevicesActivity.this, "You wanna detect qr code", Toast.LENGTH_LONG).show();
            }
        });

        dialogBuilder
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String bluetoothAddress = bluetoothAddressEditText.getText().toString();
                        String phoneNumber = phoneNumberEditText.getText().toString();

                        addNewDevice(bluetoothAddress, phoneNumber);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();*/

        Intent addDeviceIntent = new Intent(this, AddDeviceDialog.class);
        startActivityForResult(addDeviceIntent, ADD_DEVICE_REQUEST, null);
    }

    private void addNewDevice(String bluetoothAddress, String phoneNumber) {
        devices.addNewDevice(bluetoothAddress, phoneNumber);

        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    private void logInDevice(DeviceInformation device) {
        String bluetoothAddress = device.getBluetoothAddress();

        Intent logInDeviceIntent = new Intent(this, LogInActivity.class);
        logInDeviceIntent.putExtra("bluetoothAddress", bluetoothAddress);
        startActivity(logInDeviceIntent);
    }

    private void deleteDevice(int i) {
        final int deviceIndex = i;
        DeviceInformation info = devices.getDevice(i);

        // ask for confirmation
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you want to delete device " + info.getBluetoothAddress())
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // actually delete device
                                devices.removeDevice(deviceIndex);
                                adapter.notifyDataSetChanged();
                            }
                        })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_DEVICE_REQUEST && resultCode == RESULT_OK) {
            String bluetoothAddress = data.getStringExtra(AddDeviceDialog.BLUETOOTH_ADDRESS);
            String phoneNumber = data.getStringExtra(AddDeviceDialog.PHONE_NUMBER);

            addNewDevice(bluetoothAddress, phoneNumber);
        }
    }

    @Override
    protected void onPause() {
        saveDevices();

        super.onPause();
    }

    private void saveDevices() {
        try {
            FileOutputStream fos = openFileOutput(DEVICES_DATA_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(devices);

            objectOutputStream.close();
            fos.close();
        } catch (IOException e) { }
    }

    private void loadDevices() {
        try {
            FileInputStream fin = openFileInput(DEVICES_DATA_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fin);

            devices = (RecognizedDevices) objectInputStream.readObject();

            objectInputStream.close();
            fin.close();
        } catch (FileNotFoundException e) {
            // no file found
            devices = new RecognizedDevices();
            devices.addNewDevice("something cool", "12345");
        } catch (IOException e) {
            devices = new RecognizedDevices();
        }
        catch (ClassNotFoundException e) {
            devices = new RecognizedDevices();
        }
    }
}
