package com.mbrite.patrol.app;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.mbrite.patrol.content.providers.NotificationProvider;
import com.mbrite.patrol.model.Notification;
import com.mbrite.patrol.widget.*;

import java.util.ArrayList;
import android.view.*;
import android.widget.AdapterView;
import android.util.SparseBooleanArray;

public class NotificationsFragment extends ParentFragment {

    private NotificationAdapter notificationAdapter;
    private ActionMode mActionMode;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<Notification> notifications = NotificationProvider.INSTANCE.getNewNotifications(getActivity());
            notifications.addAll(NotificationProvider.INSTANCE.getOldNotifications(getActivity()));
            NotificationProvider.INSTANCE.saveOldNotifications(getActivity(), notifications);
            NotificationProvider.INSTANCE.clearNewNotifications(getActivity());
            notificationAdapter = new NotificationAdapter(
                    getActivity(),
                    notifications);
            setListAdapter(notificationAdapter);
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        onListItemSelect(position);
    }

    private void onListItemSelect(int position) {
        notificationAdapter.toggleSelection(position);
        boolean hasCheckedItems = notificationAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = getActivity().startActionMode(mActionModeCallback);
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(
                    String.format(getActivity().getString(R.string.n_items_selected)
                            , notificationAdapter.getSelectedCount()));
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.notification_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = notificationAdapter
                            .getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Notification selectedItem = notificationAdapter
                                    .getItem(selected.keyAt(i));
                            notificationAdapter.remove(selectedItem);
                        }
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            notificationAdapter.removeSelection();
            mActionMode = null;
        }
    };

}
