package com.example.beekill.matdienapp.activities.devices;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beekill.matdienapp.DeviceInformation;
import com.example.beekill.matdienapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by beekill on 10/1/16.
 */
public class DevicesAdapter extends BaseAdapter {

    private ArrayList<DeviceInformation> devices;
    private Context context;

    public DevicesAdapter(Context context, ArrayList<DeviceInformation> devices) {

        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i < devices.size()) {
            DeviceInformation deviceInfo = devices.get(i);

            if (view == null || view.getId() != R.id.fragment_device_info) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.fragment_device_info, null);
            }

            TextView deviceNameTextView = (TextView) view.findViewById(R.id.deviceNameTextView);
            TextView bluetoothAddressTextView = (TextView) view.findViewById(R.id.bluetoothAddressTextView);
            TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumberTextView);

            deviceNameTextView.setText("Device #" + String.valueOf(i + 1));
            bluetoothAddressTextView.setText(deviceInfo.getBluetoothAddress());
            phoneNumberTextView.setText(deviceInfo.getPhoneAddress());

            return view;
        } else {
            // add icon
            if (view == null || view.getId() != R.id.fragment_add_device) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.fragment_add_device, null);
            }

            ImageView addIconImageView = (ImageView) view.findViewById(R.id.addIconImageView);
            addIconImageView.setImageResource(R.drawable.ic_add_black);

            return view;
        }
    }
}
