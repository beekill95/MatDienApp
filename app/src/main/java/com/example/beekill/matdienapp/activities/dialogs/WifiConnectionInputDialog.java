package com.example.beekill.matdienapp.activities.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.beekill.matdienapp.R;

/**
 * Created by beekill on 12/2/16.
 */
public class WifiConnectionInputDialog extends AppCompatActivity {

    public static final String ACCESS_POINT = "accessPoint";
    public static final String PASSWORD = "accessPointPassword";

    private EditText accessPointEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi_connection);

        accessPointEditText = (EditText) findViewById(R.id.accessPointEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

    public void onOkButtonClicked(View view) {
        String accessPoint = accessPointEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (accessPoint.isEmpty() || password.isEmpty())
            return;

        Intent result = new Intent();
        result.putExtra(ACCESS_POINT, accessPoint);
        result.putExtra(PASSWORD, password);
        setResult(RESULT_OK, result);
        finish();
    }

    public void onCancelButtonClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
