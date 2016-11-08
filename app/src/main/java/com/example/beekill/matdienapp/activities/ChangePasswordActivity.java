package com.example.beekill.matdienapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.beekill.matdienapp.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private Spinner userSpinner;
    private EditText adminPassEditText;
    private EditText newPassEditText;
    private EditText passRetypeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userSpinner = (Spinner) findViewById(R.id.userSpinner);
        adminPassEditText = (EditText) findViewById(R.id.adminPassEditText);
        newPassEditText = (EditText) findViewById(R.id.newPassEditText);
        passRetypeEditText = (EditText) findViewById(R.id.retypeNewPassEditText);

        Button changePassButton = (Button) findViewById(R.id.changePassButton);
        changePassButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onChangePasswordButtonClicked();
                    }
                }
        );
    }

    private void onChangePasswordButtonClicked()
    {
        String user = userSpinner.getSelectedItem().toString();
        String adminPass = adminPassEditText.getText().toString();
        final String newPass = newPassEditText.getText().toString();
        String retypePass = passRetypeEditText.getText().toString();

        boolean isUserInputOk = checkUserInput(adminPass, newPass, retypePass);

        if (isUserInputOk) {
            // every thing is ok
            Intent resultIntent = getIntent();
            Bundle args = new Bundle();

            args.putString("user", user);
            args.putString("adminPass", adminPass);
            args.putString("newPass", newPass);

            resultIntent.putExtra("args", args);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please re-type everything")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete all
                                    adminPassEditText.getText().clear();
                                    newPassEditText.getText().clear();
                                    passRetypeEditText.getText().clear();
                                }
                            })
                    .show();
        }
    }

    private boolean checkUserInput(String oldPass, String newPass, String retypePass)
    {
        boolean hasNoEmptyEditTexts = checkHasNoEmptyEditTexts(oldPass, newPass, retypePass);
        boolean isNewPasswordOK = checkIsNewPasswordOK(newPass, retypePass);

        return hasNoEmptyEditTexts && isNewPasswordOK;
    }

    private boolean checkHasNoEmptyEditTexts(String oldPass, String newPass, String retypePass)
    {
        if (oldPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty())
            return false;
        else
            return true;
    }

    private boolean checkIsNewPasswordOK(String newPass, String retypePass)
    {
        if (newPass.equals(retypePass))
            return true;
        else
            return false;
    }
}
