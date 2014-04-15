package com.mbrite.patrol.common;

import android.content.res.*;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.net.*;

import com.mbrite.patrol.app.LoginActivity;
import com.mbrite.patrol.app.MainActivity;
import com.mbrite.patrol.app.R;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean isValidUsernameAndPassword(String username, String password) {
        // TODO: attempt authentication against a network service
        if (TextUtils.isEmpty(password)) {
            return false;
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
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.logout))
                .setMessage(activity.getString(R.string.confirm_logout))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            RecordProvider.INSTANCE.reset(activity);
                            Utils.clearUsernameAndPassword(activity);
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
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
        }).show();
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
     * @return true if file is updated, otherwise false
     * @throws JSONException
     * @throws URISyntaxException
     * @throws IOException
     */
    public static boolean updateSavedFile (Activity activity, String type, String fileName)
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
        HttpResponse response = RestClient.INSTANCE
                .get(activity,
                        String.format("%s.json", type),
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
        boolean updated = Utils.updateSavedFile(activity, Constants.ROUTES, Constants.ROUTES_FILE_NAME) ||
                Utils.updateSavedFile(activity, Constants.ASSETS, Constants.ASSETS_FILE_NAME) ||
                Utils.updateSavedFile(activity, Constants.POINTS, Constants.POINTS_FILE_NAME);

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

}
