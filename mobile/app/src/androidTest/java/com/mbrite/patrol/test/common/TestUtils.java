package com.mbrite.patrol.test.common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mbrite.patrol.common.Constants;

public class TestUtils {

    public static void setupFakePreferences(Activity activity) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        editor.putString(Constants.SITE_URL, "http://warm-depths-4825.herokuapp.com");
        editor.commit();
    }
}
