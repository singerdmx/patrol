package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;

import java.util.*;

public class UploadTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = UploadTask.class.getSimpleName();
    private int total = 0;
    private int fails = 0;
    private int statusCode = Constants.STATUS_CODE_CREATED;
    private Activity activity;
    private ProgressDialog progressDialog;

    public UploadTask(Activity activity, ProgressDialog progressDialog) {
        this.activity = activity;
        this.progressDialog = progressDialog;
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
                Utils.updateRecordFiles(activity);
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
        Log.i(TAG, String.format("Uploading record %s", file));
        try {
            String recordContent = FileMgr.read(activity, file);
            Record record = RecordProvider.INSTANCE.parseRecordString(recordContent);
            record.setSubmitter(Utils.getSavedUsernameAndPassword(activity)[0]);
            recordContent = RecordProvider.INSTANCE.toString(record);
//            HttpResponse response = RestClient.INSTANCE.post(activity, Constants.RESULTS, recordContent, Constants.CONTENT_TYPE_JSON);
//            int responseStatusCode = response.getStatusLine().getStatusCode();
            int responseStatusCode = 201;
            if (responseStatusCode != Constants.STATUS_CODE_CREATED) {
                statusCode = responseStatusCode;
                fails++;
//                Log.e(TAG, String.format("Fail to upload file %s:\n" +
//                        "Status Code: %d\n" +
//                        "%s", file, responseStatusCode, response.getEntity().getContent()));
            } else {
                FileMgr.delete(activity, file);
                if (Constants.RECORD_FILE_NAME.equals(file)) {
                    RecordProvider.INSTANCE.reset(activity);
                }
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