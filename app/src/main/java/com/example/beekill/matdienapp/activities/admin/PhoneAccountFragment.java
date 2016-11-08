package com.example.beekill.matdienapp.activities.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beekill.matdienapp.R;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhoneAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhoneAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhoneAccountFragment extends Fragment implements AdminFragmentCommonInterface {
    public interface OnFragmentInteractionListener {
        void onFragmentActionPerform(AdminAction action, Bundle args);
    }

    private Button refreshButton;
    private Button refillButton;

    private TextView deviceCreditTextView;
    private TextView updatedDateTextView;

    // Not a very good solution
    // will try to refactor this
    private AdminData adminData;

    private OnFragmentInteractionListener mListener;

    public PhoneAccountFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PhoneAccountFragment newInstance() {
        PhoneAccountFragment fragment = new PhoneAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_account, container, false);

        // get reference to all our widgets
        refreshButton = (Button) view.findViewById(R.id.refreshButton);
        refillButton = (Button) view.findViewById(R.id.refillButton);

        deviceCreditTextView = (TextView) view.findViewById(R.id.deviceCreditTextView);
        updatedDateTextView = (TextView) view.findViewById(R.id.updatedDateTextView);

        // register callbacks
        refreshButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGetDeviceAccount();
                    }
                }
        );

        refillButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRefillAccountDialog();
                    }
                }
        );

        if (adminData.getDeviceAccountUpdateDate() != null) {
            deviceCreditTextView.setText(String.valueOf(adminData.getDeviceAccount()));
            updatedDateTextView.setText(adminData.getDeviceAccountUpdateDate().toString());
        }

        return view;
    }

    private void sendGetDeviceAccount()
    {
        if (mListener != null) {
            mListener.onFragmentActionPerform(AdminAction.GET_DEVICE_ACCOUNT, null);
        }
    }

    private void showRefillAccountDialog()
    {

    }

    private void sendRefillDeviceCredit()
    {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleResult(boolean result, AdminData adminData, AdminAction action) {
        switch (action) {
            case RECHARGE_DEVICE_ACCOUNT:
                break;
            case GET_DEVICE_ACCOUNT:
                handleReceivedGetDeviceAccount(result, adminData);
                break;
            default:
                Log.i("MatDienApp", "The result was transfer to the wrong fragment");
        }
    }

    private void handleReceivedGetDeviceAccount(boolean result, AdminData data) {
        if (result) {
            // received a successful response
            double deviceCredit = adminData.getDeviceAccount();
            Date updateDate = adminData.getDeviceAccountUpdateDate();

            // display it to the users
            deviceCreditTextView.setText(String.valueOf(deviceCredit));
            updatedDateTextView.setText(updateDate.toString());
        } else {
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayData(AdminData data) {
        adminData = data;
    }
}
