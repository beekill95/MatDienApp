package com.example.beekill.matdienapp.activities;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.protocol.Notification;

public class NotificationActivity extends AppCompatActivity {
    private TextView notificationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Debug.waitForDebugger();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // get notification
        Notification notification = (Notification) getIntent().getParcelableExtra("notification");

        notificationTextView = (TextView) findViewById(R.id.notificationTextView);

        // display the notification
        displayNotification(notification);

        // play sound
        Uri noti = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = RingtoneManager.getRingtone(this, noti);
        ringtone.play();
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
