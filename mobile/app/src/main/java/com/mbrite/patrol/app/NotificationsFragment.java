package com.mbrite.patrol.app;

import android.os.Bundle;
import android.widget.Toast;

import com.mbrite.patrol.widget.*;

import java.util.ArrayList;

public class NotificationsFragment extends ParentFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<String> notifications = new ArrayList<>();
            notifications.add("1");
            notifications.add("2");
            notifications.add("3");
            NotificationAdapter adapter = new NotificationAdapter(
                    getActivity(),
                    notifications);
            setListAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

}
