package com.mbrite.patrol.common;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.mbrite.patrol.app.R;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Shared Constants
 */
public class Constants {

    public static final String APP_VERSION = "v1.3";
    public static final String APP_VERSION_FILE = "APP_VERSION";

    public static final String DEFAULT_LOCALE = "zh";

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String MAIN_ACTIVITY = "com.mbrite.patrol.app.action.main";
    public static final String PREFERENCE_FILE_KEY = "com.mbrite.patrol.preference_file_key";
    public static final String DEFAULT_SITE_URL = "";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String STATE = "state";
    public static final String STANDARD = "standard";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String OFFLINE = "OFFLINE";

    // settings related constants
    public static final String SITE_URL = "site_url";
    public static final String CONTINUOUS_SCAN_CHECKBOX = "continuous_scan_checkbox";
    public static final String CONTINUOUS_SCAN = "continuous_scan";

    // Login related constants
    public static final String LOGIN = "users/sign_in";
    public static final String COOKIES_HEADER = "Set-Cookie";
    public static final String COOKIE = "Cookie";
    public static final String X_CSRF_TOKEN = "X-CSRF-Token";
    public static final String USER_EMAIL = "user[email]";
    public static final String USER_PASSWORD = "user[password]";

    // Network related constants
    public static final HttpParams HTTP_PARAMS;

    static {
        HTTP_PARAMS = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        HttpConnectionParams.setConnectionTimeout(HTTP_PARAMS, 3000);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(HTTP_PARAMS, 30000);
    }

    public static final String ETAG = "Etag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int STATUS_CODE_ACCEPTED = 202;
    public static final int STATUS_CODE_NOT_MODIFIED = 304;
    public static final int STATUS_CODE_UNAUTHORIZED = 401;

    public static final Set<Integer> STATUS_CODE_UPLOAD_SUCCESS;

    static {
        STATUS_CODE_UPLOAD_SUCCESS = new TreeSet<>();
        STATUS_CODE_UPLOAD_SUCCESS.add(STATUS_CODE_CREATED);
        STATUS_CODE_UPLOAD_SUCCESS.add(STATUS_CODE_ACCEPTED);
    }

    // File name related constants
    public static final String ROUTES = "routes";
    public static final String ROUTES_FILE_NAME = "routes.json";
    public static final String ASSETS = "assets";
    public static final String ASSETS_FILE_NAME = "assets.json";
    public static final String POINTS = "points";
    public static final String POINTS_FILE_NAME = "points.json";
    public static final String RESULTS = "results.json";
    public static final String RECORD_FILE_NAME = "record.json";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_FILE_NAME = "notification.json";
    public static final String OLD_NOTIFICATION_FILE_NAME = "old_notification.json";

    // Asset related constants
    public static final String SERIAL_NUM = "serialnum";
    public static final String BARCODE = "barcode";

    // Point related constants
    public static final String CATEGORY = "category";
    public static final String CHOICE = "choice";
    public static final String DEFAULT_VALUE = "default_value";
    public static final Set<Integer> CATEGORY_SCAN_ONLY = ImmutableSortedSet.of(10);

    public static final Set<Integer> CATEGORY_SHOW_GRAPH = ImmutableSortedSet.of(40, 41);

    // Key is resId of String, value is url param
    public static Map<Integer, String> GRAPH_TYPES = ImmutableSortedMap.of(
            R.string.graph_1_pareto________, "pareto",
            R.string.graph_2_horizontal_bar, "horizontal%20bar",
            R.string.graph_3_pie___________, "pie",
            R.string.graph_4_exploded_pie__, "exploded%20pie",
            R.string.graph_5_doughnut______, "doughnut"
    );
}
