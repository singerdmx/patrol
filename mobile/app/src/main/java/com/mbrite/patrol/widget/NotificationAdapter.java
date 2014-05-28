package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.NotificationProvider;
import com.mbrite.patrol.model.Notification;

import java.util.*;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private final Activity context;
    private final ArrayList<Notification> itemsArrayList;
    private SparseBooleanArray mSelectedItemsIds;

    public NotificationAdapter(Activity context, ArrayList<Notification> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.mSelectedItemsIds = new SparseBooleanArray();
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
        TextView notificationView = (TextView) rowView.findViewById(R.id.label);
        if (notification.isOld()) {
            notificationView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            rowView.setBackgroundResource(R.drawable.alterselector1);
        } else {
            notificationView
                    .setCompoundDrawablesWithIntrinsicBounds(
                            context.getResources().getDrawable(R.drawable.new_mail),
                            null, null, null);
            rowView.setBackgroundResource(R.drawable.alterselector2);
        }

        if (mSelectedItemsIds.get(position)) {
            rowView.setBackgroundResource(R.drawable.alterselector3);
        }
        notificationView.setText(notification.getContent());
        return rowView;
    }

    public void removeSelection() {
        if (mSelectedItemsIds.size() > 0) {
            try {
                Set<Integer> indices = new HashSet<>();
                for (int i = 0; i < mSelectedItemsIds.size(); i++) {
                    indices.add(mSelectedItemsIds.keyAt(i));
                }
                ArrayList<Notification> notifications = NotificationProvider.INSTANCE.getOldNotifications(context);
                notifications = Utils.removeElements(notifications, indices);
                NotificationProvider.INSTANCE.saveOldNotifications(context, notifications);
            } catch (Exception ex) {
                Toast.makeText(
                        context,
                        String.format(context.getString(R.string.error_of), ex.getLocalizedMessage()),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
