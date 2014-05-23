package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public enum NotificationProvider {

    INSTANCE;

    public ArrayList<String> getNotifications(Activity activity)
            throws JSONException, IOException {
        String data = FileMgr.read(activity, Constants.NOTIFICATION_FILE_NAME);
        return Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.NOTIFICATION));
    }
}
