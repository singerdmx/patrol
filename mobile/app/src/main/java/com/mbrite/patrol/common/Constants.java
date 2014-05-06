package com.mbrite.patrol.common;

import java.util.*;

/**
 * Shared Constants
 */
public class Constants {

    public static final String APP_VERSION = "v1";
    public static final String APP_VERSION_FILE = "APP_VERSION";

    public static final String DEFAULT_LOCALE = "zh";

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String MAIN_ACTIVITY = "com.mbrite.patrol.app.action.main";
    public static final String PREFERENCE_FILE_KEY = "com.mbrite.patrol.preference_file_key";
    public static final String DEFAULT_SITE_URL = "http://patroldemo.herokuapp.com";
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
    public static final String AUTHENTICITY_TOKEN_HTML_ELEMENT= "<input name=\"authenticity_token\" type=\"hidden\"";
    public static final String AUTHENTICITY_TOKEN = "authenticity_token";
    public static final String USER_EMAIL = "user[email]";
    public static final String USER_PASSWORD = "user[password]";

    // Network related constants
    public static final String ETAG= "Etag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String IF_NONE_MATCH= "If-None-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int STATUS_CODE_ACCEPTED = 202;
    public static final int STATUS_CODE_NOT_MODIFIED = 304;

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

    // Asset related constants
    public static final String SERIAL_NUM = "serialnum";
    public static final String BARCODE = "barcode";

    // Point related constants
    public static final String CATEGORY = "category";
    public static final String CHOICE = "choice";
    public static final Set<Integer> CATEGORY_SCAN_ONLY;

    static {
        CATEGORY_SCAN_ONLY = new TreeSet<>();
        CATEGORY_SCAN_ONLY.add(10);
        CATEGORY_SCAN_ONLY.add(20);
    }

}
