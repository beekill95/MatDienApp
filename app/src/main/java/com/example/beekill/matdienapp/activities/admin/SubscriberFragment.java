package com.example.beekill.matdienapp.activities.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;
import com.example.beekill.matdienapp.protocol.SubscriptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscriberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscriberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscriberFragment extends Fragment implements AdminFragmentCommonInterface {
    public interface OnFragmentInteractionListener {
        void onFragmentActionPerform(AdminAction action, Bundle args);
    }

    private FloatingActionButton refreshFloatingButton;
    private FloatingActionButton addSubscriberFloatingButton;
    private Button refreshButton;
    //private Button removeButton;
    private TextView dateUpdateTextView;
    private ListView subscriberListView;
    ArrayList<String> listSubscriber;
    ArrayAdapter<String> adapter;

    private AdminData adminData;

    private OnFragmentInteractionListener mListener;

    public SubscriberFragment() {
        // Required empty public constructor
    }

    public static SubscriberFragment newInstance() {
        SubscriberFragment fragment = new SubscriberFragment();

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
        View view = inflater.inflate(R.layout.fragment_subscriber, container, false);

        //refreshButton = (Button) view.findViewById(R.id.refreshSubscriberButton);
        //removeButton = (Button) view.findViewById(R.id.removeSubscriberButton);
        dateUpdateTextView = (TextView) view.findViewById(R.id.dataUpdateTextView);

        subscriberListView = (ListView) view.findViewById(R.id.subscriberListView);
        listSubscriber = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listSubscriber);
        subscriberListView.setAdapter(adapter);

        /*refreshButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGetSubscriberList();
                    }
                }
        );*/

        // add long click listener (delete a subscriber)
        subscriberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: get the phone number the admin want to delete from status
                final String phoneNumber = "12345";
                final String status = "Power";

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setMessage("Do you want to remove " + phoneNumber + " from " + status + "?")
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sendDelSubscriber(phoneNumber, status);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        refreshFloatingButton = (FloatingActionButton) view.findViewById(R.id.refreshFloatingButton);
        refreshFloatingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGetSubscriberList();
                    }
                }
        );

        addSubscriberFloatingButton = (FloatingActionButton) view.findViewById(R.id.addSubscriberFloatingButton);
        addSubscriberFloatingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPhoneNumberToSubscribe(view);
                    }
                }
        );

        if (adminData.getSubscriberListUpdateDate() != null) {
            listSubscriber.clear();
            listSubscriber.addAll(Arrays.asList(adminData.getPowerSubscribers()));
            dateUpdateTextView.setText(adminData.getSubscriberListUpdateDate().toString());

            adapter.notifyDataSetChanged();
        }

        return view;
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

    private void sendGetSubscriberList()
    {
        if (mListener != null)
            mListener.onFragmentActionPerform(AdminAction.LIST_SUBSCRIBER, null);
    }

    private void sendAddSubscriber(String phoneNumber, String subscriptionType)
    {
        if (mListener != null) {
            Bundle args = new Bundle();
            args.putString("phoneNumber", phoneNumber);
            args.putString("subscriptionType", subscriptionType);

            mListener.onFragmentActionPerform(AdminAction.ADD_SUBSCRIBER, args);
        }
    }

    private void sendDelSubscriber(String phoneNumber, String subscriptionType)
    {
        if (mListener != null) {
            Bundle args = new Bundle();
            args.putString("phoneNumber", phoneNumber);
            args.putString("status", subscriptionType);

            mListener.onFragmentActionPerform(AdminAction.DEL_SUBSCRIBER, args);
        }
    }

    private void getPhoneNumberToSubscribe(View view)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View phoneNumberView = layoutInflater.inflate(R.layout.dialog_addsubscriber, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(phoneNumberView);

        final EditText phoneNumberEditText = (EditText) phoneNumberView.findViewById(R.id.phoneNumberEditText);
        final Spinner subscriptionTypeSpinner = (Spinner) phoneNumberView.findViewById(R.id.subscriptionTypeSpinner);

        // add items to spinner
        List<String> list = new ArrayList<>();
        list.add(SubscriptionType.Camera.getValue());
        list.add(SubscriptionType.Power.getValue());
        list.add(SubscriptionType.Thief.getValue());
        list.add(SubscriptionType.All.getValue());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subscriptionTypeSpinner.setAdapter(adapter);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String phoneNumber = phoneNumberEditText.getText().toString();
                                String subscriptionType = subscriptionTypeSpinner.getSelectedItem().toString();

                                sendAddSubscriber(phoneNumber, subscriptionType);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void handleResult(boolean result, AdminData adminData, AdminAction adminAction) {
        if (result) {
            displaySubscriberList(adminData);
        }
    }

    @Override
    public void displayData(AdminData adminData)
    {
        displaySubscriberList(adminData);
    }

    private void displaySubscriberList(AdminData adminData)
    {
        this.adminData = adminData;
    }
}
