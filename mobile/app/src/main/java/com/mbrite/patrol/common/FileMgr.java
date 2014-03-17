package com.mbrite.patrol.common;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.*;

public class FileMgr {

    public static void write(Activity activity, String fileName, String string) {
        try {
            FileOutputStream outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(
                    activity,
                    String.format("Error: %s", e.toString()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static String read(Activity activity, String fileName) {
        String result = null;
        try {
            InputStreamReader input = new InputStreamReader(activity.openFileInput(fileName));
            BufferedReader reader = new BufferedReader(input);
            StringBuilder fileData = new StringBuilder();
            char[] buf = new char[1024];
            int numRead = 0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            result = fileData.toString();
        } catch (Exception e) {
            Toast.makeText(
                    activity,
                    String.format("Error: %s", e.toString()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return result;
    }

}
