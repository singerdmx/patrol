package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemsArrayList;

    public NotificationAdapter(Activity context, ArrayList<String> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String notification = itemsArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_notification, parent, false);
        if (position % 2 == 0) {
            rowView.setBackgroundResource(R.drawable.alterselector2);
        } else {
            rowView.setBackgroundResource(R.drawable.alterselector1);
        }
        TextView notificationView = (TextView) rowView.findViewById(R.id.label);
        notificationView.setText(notification);
        return rowView;
    }
}
