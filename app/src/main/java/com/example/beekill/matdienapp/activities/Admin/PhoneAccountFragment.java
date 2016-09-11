package com.example.beekill.matdienapp.activities.Admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button refreshButton;
    private Button refillButton;

    private TextView deviceCreditTextView;
    private TextView updatedDateTextView;
    private TextView refillCodeTextView;

    // Not a very good solution
    // will try to refactor this
    private AdminData adminData;

    private OnFragmentInteractionListener mListener;

    public PhoneAccountFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PhoneAccountFragment newInstance(String param1, String param2) {
        PhoneAccountFragment fragment = new PhoneAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        refillCodeTextView = (TextView) view.findViewById(R.id.refillCodeTextView);

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
                        sendRefillDeviceCredit();
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

    private void sendRefillDeviceCredit()
    {
        if (mListener != null) {
            Bundle args = new Bundle();

            String refillCode = refillCodeTextView.getText().toString();
            args.putString("refillCode", refillCode);

            mListener.onFragmentActionPerform(AdminAction.RECHARGE_DEVICE_ACCOUNT, args);
        }
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
        if (result) {
            // received a successful response
            double deviceCredit = adminData.getDeviceAccount();
            Date updateDate = adminData.getDeviceAccountUpdateDate();

            // display it to the users
            deviceCreditTextView.setText("50000d");
            updatedDateTextView.setText(updateDate.toString());

            if (action == AdminAction.RECHARGE_DEVICE_ACCOUNT)
                refillCodeTextView.setText("");

        } else {
            // TODO: Doing something to inform the users
        }
    }

    @Override
    public void displayData(AdminData data) {
        adminData = data;
    }
}
