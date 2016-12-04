package com.example.beekill.matdienapp.activities.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;

import java.util.List;

/**
 * Created by beekill on 12/4/16.
 */
public class WifiAccessPointInquiryDialog extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi_accesspoint_result);

        Intent intent = getIntent();
        String status = intent.getStringExtra("status");
        String accessPoint = intent.getStringExtra("accessPoint");
        List<String> availableAccessPoints = intent.getStringArrayListExtra("availableAccessPoints");

        TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
        statusTextView.setText(status);

        TextView accessPointTextView = (TextView) findViewById(R.id.accessPointTextView);
        accessPointTextView.setText(accessPoint);

        ListView availableAccessPointListView = (ListView) findViewById(R.id.availableAccessPointListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, availableAccessPoints);
        availableAccessPointListView.setAdapter(adapter);
    }

    public void onOkButtonClicked(View view) {
        finish();
    }
}
