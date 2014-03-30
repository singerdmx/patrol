package com.mbrite.patrol.common;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class FileMgr {

    public static boolean exists(Activity activity, String fileName) {
        return activity.getFileStreamPath(fileName).exists();
    }

    public static boolean delete(Activity activity, String fileName)
        throws IOException {
        return activity.deleteFile(fileName);
    }

    public static void write(Activity activity, String fileName, String string)
            throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static String read(Activity activity, String fileName)
            throws IOException {
        return Utils.convertStreamToString(activity.openFileInput(fileName));
    }
}
