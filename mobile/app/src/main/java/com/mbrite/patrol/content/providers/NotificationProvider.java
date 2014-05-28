package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.Notification;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;

public enum NotificationProvider {

    INSTANCE;

    private int MAX_NUM_OLD_NOTIFICATIONS = 30;
    private int MAX_NUM_NEW_NOTIFICATIONS = 60;

    public ArrayList<Notification> getOldNotifications(Activity activity)
            throws JSONException, IOException {
        return getNotifications(activity, Constants.OLD_NOTIFICATION_FILE_NAME, true);
    }

    public void saveOldNotifications(Activity activity, ArrayList<Notification> notifications)
            throws JSONException, IOException {
        JSONObject data = new JSONObject();
        JSONArray contents = new JSONArray();
        for (int i = 0; i < MAX_NUM_OLD_NOTIFICATIONS && i < notifications.size(); i++) {
            contents.put(i, notifications.get(i).getContent());
        }
        data.put(Constants.NOTIFICATION, contents);
        FileMgr.write(activity, Constants.OLD_NOTIFICATION_FILE_NAME, data.toString());
    }

    public void addNewNotifications(Activity activity, JSONArray contents)
            throws JSONException, IOException {
        ArrayList<Notification> existingNewNotifications = getNewNotifications(activity);
        JSONObject data = new JSONObject();
        for (int i = contents.length(), j = 0; i < MAX_NUM_NEW_NOTIFICATIONS && j < existingNewNotifications.size(); i++, j++) {
            contents.put(i, existingNewNotifications.get(j).getContent());
        }
        data.put(Constants.NOTIFICATION, contents);
        FileMgr.write(activity, Constants.NOTIFICATION_FILE_NAME, data.toString());
    }

    public void clearNewNotifications(Activity activity)
        throws JSONException, IOException {
        JSONObject data = new JSONObject();
        JSONArray contents = new JSONArray();
        data.put(Constants.NOTIFICATION, contents); // empty array
        FileMgr.write(activity, Constants.NOTIFICATION_FILE_NAME, data.toString());
    }

    public ArrayList<Notification> getNewNotifications(Activity activity)
            throws JSONException, IOException {
        return getNotifications(activity, Constants.NOTIFICATION_FILE_NAME, false);
    }

    private ArrayList<Notification> getNotifications(Activity activity, String fileName, boolean isOld)
            throws JSONException, IOException {
        ArrayList<Notification> result = new ArrayList<>();
        if (!FileMgr.exists(activity, fileName)) {
            return result;
        }

        String data = FileMgr.read(activity, fileName);
        ArrayList<String> contents = Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.NOTIFICATION));
        for (String content : contents) {
            result.add(new Notification(content, isOld));
        }

        return result;
    }
}
