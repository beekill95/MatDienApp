package com.example.beekill.matdienapp.activities.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.activities.dialogs.WifiConnectionInputDialog;
import com.example.beekill.matdienapp.protocol.Response;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WifiCommandFragment extends Fragment implements AdminFragmentCommonInterface {

    private OnListFragmentInteractionListener mListener;

    public WifiCommandFragment() {

    }

    public static WifiCommandFragment newInstance() {
        WifiCommandFragment fragment = new WifiCommandFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wificommand, container, false);

        ListView wifiCommandsListView = (ListView) view.findViewById(R.id.wifiCommandsListView);
        wifiCommandsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleResult(Response response, AdminData adminData, AdminAction action) {
        if (action == AdminAction.WIFI_AP_INQUIRY) {
            // show the result in admin activity
        }

        // if we receive wifi connection result
        // we do nothing
        // because admin activity already show the result
    }

    @Override
    public void displayData(AdminData data) {
        // do nothing
    }

    public interface OnListFragmentInteractionListener {
        void onFragmentActionPerform(AdminAction action, Bundle args);
    }

    private void onItemClicked(int index) {
        final int WIFI_INQUIRY_IDX = 0;
        final int WIFI_CONN_IDX = 1;

        switch (index) {
            case WIFI_INQUIRY_IDX:
                onWifiInquiryOptionSelected();
            case WIFI_CONN_IDX:
                onWifiConnectionOptionSelected();
        }
    }

    private void onWifiInquiryOptionSelected() {
        if (mListener != null) {
            mListener.onFragmentActionPerform(AdminAction.WIFI_AP_INQUIRY, null);
        }
    }

    private static final int REQUEST_ACCESS_POINT_INPUT = 777;

    private void onWifiConnectionOptionSelected() {
        // display a dialog for users to input access point and password
        Intent inputAccessPointIntent = new Intent(getActivity(), WifiConnectionInputDialog.class);
        startActivityForResult(inputAccessPointIntent, REQUEST_ACCESS_POINT_INPUT, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACCESS_POINT_INPUT && resultCode == 1/*RESULT_OK*/) {
            String accessPoint = data.getStringExtra(WifiConnectionInputDialog.ACCESS_POINT);
            String apPassword = data.getStringExtra(WifiConnectionInputDialog.PASSWORD);

            Bundle args = new Bundle();
            args.putString("accessPoint", accessPoint);
            args.putString("password", apPassword);

            if (mListener != null)
                mListener.onFragmentActionPerform(AdminAction.WIFI_CONNECTION, args);
        }
    }
}
