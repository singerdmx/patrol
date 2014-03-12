package com.mbrite.patrol.common;

import android.content.res.Configuration;
import android.text.TextUtils;
import android.app.*;
import android.content.*;

import java.util.Locale;

/**
 * Utility class
 */
public class Utils {
    public static void setDefaultLocale(android.content.Context context) {
        Locale locale = new Locale(Constants.DEFAULT_LOCALE);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                                                   context.getResources().getDisplayMetrics());
    }

    public static boolean isValidUsernameAndPassword(String username, String password) {
        // TODO: attempt authentication against a network service
        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            return false;
        }

        return true;
    }

    public static void savedUsernameAndPassword(Activity activity, String username, String password) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor
          .putString(Constants.USER_NAME, username)
          .putString(Constants.PASSWORD, password);
        editor.commit();
    }

    public static void clearUsernameAndPassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor
          .remove(Constants.USER_NAME)
          .remove(Constants.PASSWORD);
        editor.commit();
    }

    /**
     * @return String array of two elements representing username and password.
     * If either username or password is not found, null is returned.
     */
    public static String[] getSavedUsernameAndPassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        String username = sharedPref.getString(Constants.USER_NAME, null);
        String password = sharedPref.getString(Constants.PASSWORD, null);
        if (username == null || password == null) {
            return null;
        }

        return new String[] { username, password };
    }
}
