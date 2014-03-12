package com.mbrite.patrol.common;

import android.content.res.Configuration;
import android.text.TextUtils;

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
}
