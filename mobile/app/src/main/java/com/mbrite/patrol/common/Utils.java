package com.mbrite.patrol.common;

import android.content.res.*;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.net.*;

import com.mbrite.patrol.app.LoginActivity;
import com.mbrite.patrol.app.R;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.AssetGroup;
import com.mbrite.patrol.model.PointGroup;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Utility class
 */
public class Utils {
    private final static double EPSILON = 0.00001;

    static {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static void setDefaultLocale(android.content.Context context) {
        Locale locale = new Locale(Constants.DEFAULT_LOCALE);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                                                   context.getResources().getDisplayMetrics());
    }

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean isValidUsernameAndPassword(Activity activity, String username, String password)
            throws URISyntaxException, JSONException, IOException {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        List<BasicNameValuePair> payload = new ArrayList<BasicNameValuePair>(4);
        payload.add(new BasicNameValuePair(Constants.USER_EMAIL, username));
        payload.add(new BasicNameValuePair(Constants.USER_PASSWORD, password));
        payload.add(new BasicNameValuePair("user[remember_me]", "1"));
        payload.add(new BasicNameValuePair(Constants.AUTHENTICITY_TOKEN, getAuthenticityToken(activity)));
        HttpResponse response = RestClient.INSTANCE.post(activity, Constants.LOGIN, payload);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                break;
            case 302:

                break;
            case 401:
                break;
            default:
                throw new HttpResponseException(statusCode, "Error occurred for Login request");
        }
        return true;
    }

    public static void saveUsernameAndPassword(Activity activity, String username, String password) {
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

    public static void logout(final Activity activity) {
        try {
            if (Tracker.INSTANCE.isRecordComplete()) {
                logoutUser(activity);
                return;
            }

            new AlertDialog.Builder(activity, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setTitle(R.string.confirm_logout)
                    .setMessage(R.string.logout_warning)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                logoutUser(activity);
                            } catch (Exception ex) {
                                Toast.makeText(
                                        activity,
                                        String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do nothing.
                }
            }).setIcon(R.drawable.warning).show();
        } catch (Exception ex) {
            Toast.makeText(
                    activity,
                    String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
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

    public static boolean getContinuousScanMode(Activity activity) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getBoolean(Constants.CONTINUOUS_SCAN_CHECKBOX, false);
    }

    public static String getSiteURI(Activity activity) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getString(Constants.SITE_URL, Constants.DEFAULT_SITE_URL);
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            char[] buf = new char[1024];
            int numRead = 0;

            while((numRead=reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                sb.append(readData);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return sb.toString();
    }

    /**
     * @param activity
     * @param type
     * @param fileName
     * @param param
     * @return true if any file is updated, otherwise false
     * @throws JSONException
     * @throws URISyntaxException
     * @throws IOException
     */
    public static boolean updateDataFiles(Activity activity)
            throws JSONException, URISyntaxException, IOException {
        boolean updated = false;
        updated = updateSavedFile(activity, Constants.ROUTES, Constants.ROUTES_FILE_NAME, "?group_by_asset=true") || updated;
        updated = updateSavedFile(activity, Constants.ASSETS, Constants.ASSETS_FILE_NAME, null) || updated;
        updated = updateSavedFile(activity, Constants.POINTS, Constants.POINTS_FILE_NAME, null) || updated;
        return updated;
    }

    /**
     * @param activity
     * @param type
     * @param fileName
     * @return true if file is updated, otherwise false
     * @throws JSONException
     * @throws URISyntaxException
     * @throws IOException
     */
    private static boolean updateSavedFile (Activity activity, String type, String fileName, String param)
            throws JSONException, URISyntaxException, IOException {
        Map<String, String> headers = null;
        if (FileMgr.exists(activity, fileName)) {
            JSONObject savedRoutes = new JSONObject(FileMgr.read(activity, fileName));
            if (savedRoutes.has(Constants.IF_MODIFIED_SINCE) && savedRoutes.has(Constants.IF_NONE_MATCH)) {
                headers = new HashMap<String, String>();
                headers.put(Constants.IF_NONE_MATCH, savedRoutes.getString(Constants.IF_NONE_MATCH));
                headers.put(Constants.IF_MODIFIED_SINCE, savedRoutes.getString(Constants.IF_MODIFIED_SINCE));
            }
        }
        String url = String.format("%s.json", type);
        if (StringUtils.isNoneBlank(param)) {
            url += param;
        }
        HttpResponse response = RestClient.INSTANCE
                .get(activity,
                        url,
                        headers);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case Constants.STATUS_CODE_OK:
                // update file
                String responseContent = Utils.convertStreamToString(response.getEntity().getContent());
                JSONArray responseData = new JSONArray(responseContent);
                JSONObject data = new JSONObject();
                data.put(type, responseData);
                Header ifNoneMatch = response.getFirstHeader(Constants.ETAG);
                Header ifModifiedSince= response.getFirstHeader(Constants.LAST_MODIFIED);
                if (ifNoneMatch != null && ifModifiedSince != null) {
                    data.put(Constants.IF_NONE_MATCH, ifNoneMatch.getValue());
                    data.put(Constants.IF_MODIFIED_SINCE, ifModifiedSince.getValue());
                }
                FileMgr.write(activity, fileName, data.toString());
                return true;
            case Constants.STATUS_CODE_NOT_MODIFIED:
                // Not Modified
                return false;
            default:
                throw new HttpResponseException(statusCode,
                        "Error occurred for GET request: " + type);
        }
    }

    public static ColorStateList getColorStateList(Activity activity, int drawableId) {
        try {
            Resources res = activity.getResources();
            XmlResourceParser parser = res.getXml(drawableId);
            return ColorStateList.createFromXml(res, parser);
        } catch (Exception ex) {
            Toast.makeText(
                    activity,
                    String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return null;
    }

    public static void updateRecordFiles(Activity activity)
            throws JSONException, URISyntaxException, IOException {
        boolean updated = Utils.updateDataFiles(activity);

        if (updated) {
            Toast.makeText(
                    activity,
                    R.string.update_complete,
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(
                    activity,
                    R.string.no_update,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static <T> ArrayList<T> convertJSONArrayToList(JSONArray array)
        throws JSONException {
       ArrayList<T> result = new ArrayList<>(array.length());
       for (int i = 0; i < array.length(); i++) {
           result.add((T) array.get(i));
       }

       return result;
    }

    public static String getString(JSONObject jsonObject, String key)
        throws JSONException {
        if (jsonObject.has(key)) {
            String value = jsonObject.getString(key);
            if (!"null".equals(value)) {
                return value;
            }
        }

        return null;
    }

    public static Double getDouble(String s) {
        if (StringUtils.isNoneBlank(s)) {
            return Double.parseDouble(s);
        }

        return null;
    }

    public static boolean areEqualDouble(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    /*
     * If pointId is not null, check if it's scanOnly
     * Otherwise assetId should not be null, check if the asset only has one point that is scan only
     * Record will add this point if it is scan only
     * Tracker pointGroups is updated as well
     */
    public static boolean isScanOnly(Integer pointId, Integer assetId, AssetGroup targetAsset, Activity activity)
            throws IOException {
        PointGroup point = null;
        if (pointId != null) {
            point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        } else {
            List<PointGroup> points = new ArrayList<>();
            if (targetAsset != null) {
                points = targetAsset.pointList;
            } else {
                for (AssetGroup a : Tracker.INSTANCE.getAssetDuplicates().get(assetId)) {
                    for (PointGroup pt : a.pointList) {
                        boolean exists = false;
                        for (PointGroup p : points) {
                            if (p.id == pt.id) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            points.add(pt);
                        }
                    }
                }
            }

            if (points.size() == 1) {
                point = points.get(0);
            }
        }

        if (point != null && Constants.CATEGORY_SCAN_ONLY.contains(point.category)) {
            // If there is only one point and that is scan only
            RecordProvider.INSTANCE.addOrUpdatePointRecord(point, "", 0, "", activity);
            // Update Tracker pointGroups as well
            Tracker.INSTANCE.pointGroups = new TreeSet<>();
            Tracker.INSTANCE.pointGroups.add(pointId);
            return true;
        }

        return false;
    }

    private static void logoutUser(Activity activity)
            throws IOException {
        RecordProvider.INSTANCE.completeCurrentRecord(activity);
        Utils.clearUsernameAndPassword(activity);
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    private static String getAuthenticityToken(Activity activity)
            throws URISyntaxException, IOException {
        HttpResponse response = RestClient.INSTANCE.get(activity, Constants.LOGIN);
        String signInHtmlContent = Utils.convertStreamToString(response.getEntity().getContent());
        int authenticityTokenIndex = signInHtmlContent.indexOf(Constants.AUTHENTICITY_TOKEN_HTML_ELEMENT);
        return signInHtmlContent.substring(authenticityTokenIndex + 54, authenticityTokenIndex + 98);
    }
}
