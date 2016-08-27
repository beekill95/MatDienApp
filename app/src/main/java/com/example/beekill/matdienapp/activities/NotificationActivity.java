package com.example.beekill.matdienapp.activities;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.protocol.Notification;

public class NotificationActivity extends AppCompatActivity {
    private TextView notificationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Debug.waitForDebugger();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // get notification
        Notification notification = (Notification) getIntent().getParcelableExtra("notification");

        notificationTextView = (TextView) findViewById(R.id.notificationTextView);

        // display the notification
        displayNotification(notification);
    }

    private void displayNotification(Notification notification)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Power is: ");
        builder.append(notification.isPowerOn());

        builder.append("\nCamera is: ");
        builder.append(notification.isCameraOn());

        builder.append("\nWe are being robbed: ");
        builder.append(notification.isHaveTheif());

        notificationTextView.setText(builder.toString());
    }
}
