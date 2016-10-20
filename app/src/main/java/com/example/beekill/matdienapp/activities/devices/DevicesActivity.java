package com.example.beekill.matdienapp.activities.devices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.beekill.matdienapp.DeviceInformation;
import com.example.beekill.matdienapp.activities.LogInActivity;
import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.activities.AddDeviceDialog;

import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity {

    private final static int ADD_DEVICE_REQUEST = 1;

    private ArrayList<DeviceInformation> deviceInformations;
    GridView gridView;
    DevicesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // load devices information
        deviceInformations = new ArrayList<>();
        deviceInformations.add(new DeviceInformation("bluetoothAddress1", "phoneNum1"));
        deviceInformations.add(new DeviceInformation("bluetoothAddress2", "phoneNum2"));

        // display grid view
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new DevicesAdapter(this, deviceInformations);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == deviceInformations.size()) {
                            // users press add new device item
                            startAddDeviceDialog();
                        } else {
                            // users touch a device
                            logInDevice(deviceInformations.get(i));
                        }
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
        DeviceInformation info = new DeviceInformation(bluetoothAddress, phoneNumber);
        deviceInformations.add(info);

        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    private void logInDevice(DeviceInformation device) {
        String bluetoothAddress = device.getBluetoothAddress();

        Intent logInDeviceIntent = new Intent(this, LogInActivity.class);
        logInDeviceIntent.putExtra("bluetoothAddress", bluetoothAddress);
        startActivity(logInDeviceIntent);
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
}
