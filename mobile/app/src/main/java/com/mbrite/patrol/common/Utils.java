package com.mbrite.patrol.common;

import android.content.res.Configuration;
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
}
