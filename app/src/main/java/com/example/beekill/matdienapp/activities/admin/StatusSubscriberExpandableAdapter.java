package com.example.beekill.matdienapp.activities.admin;

/**
 * Created by beekill on 10/13/16.
 */

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.beekill.matdienapp.R;

class StatusSubscriberExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private String[] statusHeader;
    private HashMap<String, String[]> subscriberPhoneNumbers;

    public StatusSubscriberExpandableAdapter(Context context, String[] statusHeader, HashMap<String, String[]> subscriberPhoneNumbers) {
        this.context = context;
        this.statusHeader = statusHeader;
        this.subscriberPhoneNumbers = subscriberPhoneNumbers;
    }

    @Override
    public int getGroupCount() {
        return statusHeader.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (subscriberPhoneNumbers.get(statusHeader[groupPosition]) == null)
            return 0;
        else
            return subscriberPhoneNumbers.get(statusHeader[groupPosition]).length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return statusHeader[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (subscriberPhoneNumbers.get(statusHeader[groupPosition]) == null)
            return "";
        else
            return subscriberPhoneNumbers.get(statusHeader[groupPosition])[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int i, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String statusTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_status_list_group, null);
        }

        TextView statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);
        statusTextView.setTypeface(null, Typeface.BOLD);
        statusTextView.setText(statusTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_subscriber_list_item, null);
        }

        TextView subscriberPhoneNumberTextView = (TextView) view.findViewById(R.id.subscriberPhoneNumberTextView);
        subscriberPhoneNumberTextView.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
