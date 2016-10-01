package com.example.beekill.matdienapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.qrreader.QRActivity;

public class AddDeviceDialog extends AppCompatActivity {

    private static final int QR_SCAN_REQUEST = 1;
    public static final String BLUETOOTH_ADDRESS = "bluetoothAddress";
    public static final String PHONE_NUMBER = "phoneNumber";

    private EditText bluetoothAddressEditText;
    private EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_device);

        bluetoothAddressEditText = (EditText) findViewById(R.id.bluetoothAddressEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
    }

    public void onOkButtonClicked(View view) {
        String bluetoothAddress = bluetoothAddressEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        if (bluetoothAddress.isEmpty() || phoneNumber.isEmpty())
            return;

        Intent result = new Intent();
        result.putExtra(BLUETOOTH_ADDRESS, bluetoothAddress);
        result.putExtra(PHONE_NUMBER, phoneNumber);
        setResult(RESULT_OK, result);
        finish();
    }

    public void onCancelButtonClicked(View view) {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onQrDetectButtonClicked(View view) {
        Intent qrScanIntent = new Intent(this, QRActivity.class);
        startActivityForResult(qrScanIntent, QR_SCAN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == QR_SCAN_REQUEST) {
            String qrData = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);

            bluetoothAddressEditText.setText(qrData);
        }
    }
}
