package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.NotificationProvider;
import com.mbrite.patrol.model.Notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private static final int[] EMAIL_DRAWABLE_OLD_NOTIFICATION =
            new int[]{R.drawable.email_info_icon_black_white,
                    R.drawable.email_delete_icon_black_white, R.drawable.email_alert_icon_black_white};
    private static final int[] EMAIL_DRAWABLE_NEW_NOTIFICATION =
            new int[]{R.drawable.email_info_icon,
                    R.drawable.email_delete_icon, R.drawable.email_alert_icon};

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

        // Sample notification.getContent():

        // left side icon
        int emailDrawableResId;
        if (mSelectedItemsIds.get(position)) {
            emailDrawableResId = R.drawable.email_trash_icon;
        } else {
            int category = Integer.parseInt(notification.getContent().substring(1, 2));
            emailDrawableResId = notification.isOld() ? EMAIL_DRAWABLE_OLD_NOTIFICATION[category] :
                    EMAIL_DRAWABLE_NEW_NOTIFICATION[category];
        }

        notificationView.setCompoundDrawablesWithIntrinsicBounds(
                context.getResources().getDrawable(emailDrawableResId),
                null, null, null);

        // background
        if (mSelectedItemsIds.get(position)) {
            rowView.setBackgroundResource(R.drawable.alterselector4);
        } else {
            rowView.setBackgroundResource(notification.isOld() ?
                    R.drawable.alterselector1 : R.drawable.alterselector2);
        }

        // text
        String content = notification.getContent().substring(3);
        notificationView.setText(content);

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
