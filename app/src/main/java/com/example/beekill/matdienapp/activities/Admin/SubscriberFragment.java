package com.example.beekill.matdienapp.activities.Admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;

import java.util.ArrayList;
import java.util.Arrays;

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

    private Button refreshButton;
    private Button removeButton;
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

        refreshButton = (Button) view.findViewById(R.id.refreshSubscriberButton);
        removeButton = (Button) view.findViewById(R.id.removeSubscriberButton);
        dateUpdateTextView = (TextView) view.findViewById(R.id.dataUpdateTextView);

        subscriberListView = (ListView) view.findViewById(R.id.subscriberListView);
        listSubscriber = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listSubscriber);
        subscriberListView.setAdapter(adapter);

        refreshButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGetSubscriberList();
                    }
                }
        );

        if (adminData.getSubscriberListUpdateDate() != null) {
            listSubscriber.clear();
            listSubscriber.addAll(Arrays.asList(adminData.getSubscriberList()));
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
