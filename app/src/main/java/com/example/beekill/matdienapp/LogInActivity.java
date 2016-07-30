package com.example.beekill.matdienapp;

/**
 * Created by beekill on 7/19/16.
 * Log In activity
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

public class LogInActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        login(username, password);
                    }
                }
        );
    }

    private void login(String username, final String password)
    {
        if (checkLoginInformation(username, password)) {
            // login successfully
            // write back to shared preferences to tell that a user has signed in
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                    getApplicationContext()
            ).edit();
            editor.putBoolean("isLogin", true);
            editor.commit();

            // turn back to main activity
            Intent i = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            // login failed
            // show dialog
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Login")
                    .setMessage("Wrong user name or password")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete all username and password
                                    usernameEditText.getText().clear();
                                    passwordEditText.getText().clear();
                                }
                            })
                    .show();
        }
    }

    // return true if username and password are correct
    // return false otherwise
    private boolean checkLoginInformation(String username, String password)
    {
        String[] storedPassword = UserInformation.getInstance().getInformationOf(username);

        if (storedPassword == null)
            // cannot find the given user name in our database
            return false;
        else {
            // we found the given user name
            // check if the user can login
            if (storedPassword[0].equals(""))
                // login with admin account
                return storedPassword[1].equals(password);
            else
                // login with other account
                return storedPassword[1].equals(hashPassword(storedPassword[0], password));
        }
    }

    private String hashPassword(String salt, String password)
    {
        Hashing hashing = new HashingPBKDF2();
        return hashing.hash(password, salt);
    }
}
