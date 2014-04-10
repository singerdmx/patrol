package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class UploadTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = UploadTask.class.getSimpleName();
    private int total = 0;
    private int fails = 0;
    private int statusCode = Constants.STATUS_CODE_CREATED;
    private Activity activity;
    private ProgressDialog progressDialog;
    private boolean updateRecordFiles;

    public UploadTask(Activity activity, ProgressDialog progressDialog) {
        this(activity, progressDialog, false);
    }

    public UploadTask(Activity activity, ProgressDialog progressDialog, boolean updateRecordFiles) {
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.updateRecordFiles = updateRecordFiles;
    }

    @Override
    protected Integer doInBackground(Void... unused) {
        try {
            List<String> recordFiles = RecordProvider.INSTANCE.getRecordFiles(activity);
            total = recordFiles.size();
            for (String recordFile : recordFiles) {
                uploadFile(recordFile);
            }
            return statusCode;
        } catch (Exception e) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(activity,
                    String.format(activity.getString(R.string.error_of), e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }

        return -1;
    }

    @Override
    protected void onProgressUpdate(Void... unused) {

    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (statusCode != Constants.STATUS_CODE_CREATED) {
            Toast.makeText(activity.getApplicationContext(),
                    String.format("%s\n" +
                                    "%s",
                            String.format(activity.getString(R.string.error_upload), fails),
                            String.format(activity.getString(R.string.upload_success), total - fails)),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity.getApplicationContext(),
                    String.format(activity.getString(R.string.upload_success), total),
                    Toast.LENGTH_SHORT).show();
            try {
                RecordProvider.INSTANCE.reset(activity);
                if (updateRecordFiles) {
                    Utils.updateRecordFiles(activity);
                } else {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            } catch (Exception ex) {
                Toast.makeText(
                        activity.getApplicationContext(),
                        String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void uploadFile(String file) {
        Log.i(TAG, String.format("Uploading file %s", file));
        try {
            String record = FileMgr.read(activity, file);
            HttpResponse response = RestClient.INSTANCE.post(activity, Constants.RESULTS, record, Constants.CONTENT_TYPE_JSON);
            int responseStatusCode = response.getStatusLine().getStatusCode();
            if (responseStatusCode != 201) {
                statusCode = responseStatusCode;
                fails++;
                Log.e(TAG, String.format("Fail to upload file %s:\n" +
                        "Status Code: %d\n" +
                        "%s", file, responseStatusCode, response.getEntity().getContent()));
            } else {
                FileMgr.delete(activity, file);
            }
        } catch (Exception ex) {
            fails++;
            if (statusCode == Constants.STATUS_CODE_CREATED) {
                statusCode = -1;
            }
            Log.e(TAG,
                    String.format("Fail to upload file %s:\n%s\n%s",
                            file,
                            ex.getMessage(),
                            ex.getStackTrace()));
        }
    }
}