package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.model.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private final Activity context;
    private final ArrayList<Notification> itemsArrayList;

    public NotificationAdapter(Activity context, ArrayList<Notification> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = itemsArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_notification, parent, false);
        if (notification.isOld()) {
            rowView.setBackgroundResource(R.drawable.alterselector1);
        } else {
            rowView.setBackgroundResource(R.drawable.alterselector2);
        }
        TextView notificationView = (TextView) rowView.findViewById(R.id.label);
        notificationView.setText(notification.getContent());
        return rowView;
    }
}
