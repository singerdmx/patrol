package com.mbrite.patrol.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.mbrite.patrol.app.LoginActivity;
import com.mbrite.patrol.app.R;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    public static void setDefaultLocale(Context context) {
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
        if (StringUtils.isBlank(password)) {
            return false;
        }

        RestClient.INSTANCE.getAuthenticityToken(activity);
        List<BasicNameValuePair> payload = new ArrayList<BasicNameValuePair>(3);
        payload.add(new BasicNameValuePair(Constants.USER_EMAIL, username));
        payload.add(new BasicNameValuePair(Constants.USER_PASSWORD, password));
        payload.add(new BasicNameValuePair("user[remember_me]", "1"));
        HttpResponse response = RestClient.INSTANCE.post(activity, Constants.LOGIN + ".json", payload);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case Constants.STATUS_CODE_OK:
                return true;
            case Constants.STATUS_CODE_CREATED:
                return true;
            case Constants.STATUS_CODE_UNAUTHORIZED:
                return false;
            default:
                throw new HttpResponseException(statusCode,
                        String.format("Error occurred for Login request. Status code: %d", statusCode));
        }
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
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String username = sharedPref.getString(Constants.USER_NAME, null);
        String password = sharedPref.getString(Constants.PASSWORD, null);
        if (username == null || password == null) {
            return null;
        }

        return new String[]{username, password};
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

            while ((numRead = reader.read(buf)) != -1) {
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
     * @return true if any file is updated, otherwise false
     * @throws org.json.JSONException
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public static void updateDataFiles(Activity activity) {
        try {
            boolean updated = false;
            updated = updateSavedFile(activity, Constants.ASSETS, Constants.ASSETS_FILE_NAME, null);
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
        } catch (JSONException ex) {
            Toast.makeText(
                    activity,
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (URISyntaxException | IllegalStateException ex) {
            Toast.makeText(
                    activity,
                    String.format(activity.getString(R.string.error_site_url_invalid),
                            RestClient.INSTANCE.getSite()),
                    Toast.LENGTH_LONG
            )
                    .show();
        } catch (IOException ex) {
            Toast.makeText(
                    activity,
                    String.format(activity.getString(R.string.error_network_connection_failure),
                            RestClient.INSTANCE.getSite()),
                    Toast.LENGTH_LONG
            )
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    activity,
                    String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * @param activity
     * @param type
     * @param fileName
     * @return true if file is updated, otherwise false
     * @throws org.json.JSONException
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public static boolean updateSavedFile(Activity activity, String type, String fileName, String param)
            throws JSONException, URISyntaxException, IOException {
        Map<String, String> headers = null;
        if (FileMgr.exists(activity, fileName)) {
            JSONObject savedData = new JSONObject(FileMgr.read(activity, fileName));
            if (savedData.has(Constants.IF_MODIFIED_SINCE) && savedData.has(Constants.IF_NONE_MATCH)) {
                headers = new HashMap<String, String>();
                headers.put(Constants.IF_NONE_MATCH, savedData.getString(Constants.IF_NONE_MATCH));
                headers.put(Constants.IF_MODIFIED_SINCE, savedData.getString(Constants.IF_MODIFIED_SINCE));
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
                Header ifModifiedSince = response.getFirstHeader(Constants.LAST_MODIFIED);
                if (ifNoneMatch != null && ifModifiedSince != null) {
                    data.put(Constants.IF_NONE_MATCH, ifNoneMatch.getValue());
                    data.put(Constants.IF_MODIFIED_SINCE, ifModifiedSince.getValue());
                }
                FileMgr.write(activity, fileName, data.toString());
                return true;
            case Constants.STATUS_CODE_NOT_MODIFIED:
                // Not Modified
                return false;
            case Constants.STATUS_CODE_UNAUTHORIZED:
                Toast.makeText(
                        activity,
                        R.string.error_incorrect_password_please_login,
                        Toast.LENGTH_LONG)
                        .show();
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

    public static <T> ArrayList<T> convertJSONArrayToList(JSONArray array)
            throws JSONException {
        ArrayList<T> result = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            result.add((T) array.get(i));
        }

        return result;
    }
//
//    public static <T> JSONArray convertListToJSONArray(ArrayList<T> l)
//            throws JSONException {
//        JSONArray result = new JSONArray();
//        for (int i = 0; i < l.size(); i++) {
//            result.put(i, l.get(i));
//        }
//
//        return result;
//    }

    public static <T> ArrayList<T> removeElements(ArrayList<T> l, Set<Integer> indices) {
        ArrayList<T> result = new ArrayList<T>(l.size());
        for (int i = 0; i < l.size(); i++) {
            if (!indices.contains(i)) {
                result.add(l.get(i));
            }
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

    private static void logoutUser(Activity activity)
            throws IOException {
        RecordProvider.INSTANCE.completeCurrentRecord(activity);
        Utils.clearUsernameAndPassword(activity);
        RestClient.INSTANCE.clearSession();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
