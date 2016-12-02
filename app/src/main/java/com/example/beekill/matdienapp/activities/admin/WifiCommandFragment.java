package com.example.beekill.matdienapp.activities.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.beekill.matdienapp.R;

import java.util.List;

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
    public void handleResult(boolean result, AdminData adminData, AdminAction action) {
        if (action == AdminAction.WIFI_AP_INQUIRY) {

        } else if (action == AdminAction.WIFI_CONNECTION) {

        }
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

    private void onWifiConnectionOptionSelected() {
        // display a dialog for users to input access point and password


        if (mListener != null) {

        }
    }
}
