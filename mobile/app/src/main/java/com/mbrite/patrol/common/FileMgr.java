package com.mbrite.patrol.common;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileMgr {

    public static String getFullPath(Activity activity, String relativePath) {
        return activity.getFileStreamPath(relativePath).getAbsolutePath();
    }

    public static void copy(Activity activity, File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        try {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(activity, ex);
        } finally {
            in.close();
            out.close();
        }
    }

    public static boolean exists(Activity activity, String fileName) {
        return activity.getFileStreamPath(fileName).exists();
    }

    public static boolean delete(Activity activity, String fileName)
            throws IOException {
        return activity.deleteFile(fileName);
    }

    public static void copy(Activity activity, String sourceFileName, String targetFileName)
            throws IOException {
        String content = read(activity, sourceFileName);
        write(activity, targetFileName, content);
    }

    public static String[] fileList(Activity activity) {
        return activity.fileList();
    }

    public static void write(Activity activity, String fileName, String string)
            throws IOException {
        write(activity, fileName, string.getBytes());
    }

    public static void write(Activity activity, String fileName, byte[] content)
            throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content);
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
