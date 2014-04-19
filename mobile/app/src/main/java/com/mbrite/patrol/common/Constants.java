package com.mbrite.patrol.common;

import java.util.*;

/**
 * Shared Constants
 */
public class Constants {

    public static final String DEFAULT_LOCALE = "zh";

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String MAIN_ACTIVITY = "com.mbrite.patrol.app.action.main";
    public static final String PREFERENCE_FILE_KEY = "com.mbrite.patrol.preference_file_key";
    public static final String DEFAULT_SITE_URL = "http://warm-depths-4825.herokuapp.com";
    public static final String SITE_URL = "site_url";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String STATE = "state";
    public static final String STANDARD = "standard";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String OFFLINE = "OFFLINE";

    // Network related constants
    public static final String ETAG= "Etag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String IF_NONE_MATCH= "If-None-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_CREATED = 201;
    public static final int STATUS_CODE_NOT_MODIFIED = 304;

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
    public static final String TPM_TYPE = "tpm_type";
    public static final String PERIOD_UNIT = "period_unit";
    public static final String CATEGORY = "category";
    public static final String CHOICE = "choice";
    public static final Set<Integer> CATEGORY_SCAN_ONLY;

    static {
        CATEGORY_SCAN_ONLY = new TreeSet<>();
        CATEGORY_SCAN_ONLY.add(10);
        CATEGORY_SCAN_ONLY.add(20);
    }

}
