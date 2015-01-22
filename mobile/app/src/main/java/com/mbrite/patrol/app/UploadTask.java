package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.connection.MultipartEntity;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.PointRecord;
import com.mbrite.patrol.model.Record;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UploadTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = UploadTask.class.getSimpleName();
    private int total = 0;
    private int fails = 0;
    private int statusCode = Constants.STATUS_CODE_CREATED;
    private Activity activity;
    private ProgressDialog progressDialog;

    public UploadTask(Activity activity) {
        this.activity = activity;
        this.progressDialog = ProgressDialog.show(activity,
                activity.getString(R.string.uploading),
                activity.getString(R.string.please_wait),
                true);
    }

    @Override
    protected Integer doInBackground(Void... unused) {
        try {
            List<String> recordFiles = RecordProvider.INSTANCE.getRecordFiles(activity);
            total = recordFiles.size();
            for (String recordFile : recordFiles) {
                if (!Constants.RECORD_FILE_NAME.equals(recordFile)) {
                    uploadFile(recordFile);
                }
            }

            return statusCode;
        } catch (final Exception e) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity,
                            String.format(activity.getString(R.string.error_of), e.getLocalizedMessage()),
                            Toast.LENGTH_LONG).show();
                }
            });
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

        if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity.getApplicationContext(),
                            String.format("%s\n" +
                                            "%s",
                                    String.format(activity.getString(R.string.error_upload), fails),
                                    String.format(activity.getString(R.string.upload_success), total - fails)
                            ),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity,
                            String.format(activity.getString(R.string.upload_success), total),
                            Toast.LENGTH_SHORT).show();
                }
            });
            try {
                // clear all record files (should be non) and image files
                RecordProvider.INSTANCE.resetAll(activity, true);
            } catch (final Exception ex) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.showErrorPopupWindow(activity, ex);
                    }
                });
            }
        }
    }

    private int uploadImagesInRecord(Record record) throws IOException {
        try {
            for (final PointRecord pointRecord : record.points) {
                if (!pointRecord.image.endsWith(Constants.IMAGE_FILE_SUFFIX)) {
                    // image is already uploaded. Now image is set to id in DB.
                    continue;
                }
                String imgFilePath = activity.getFileStreamPath(pointRecord.image).getAbsolutePath();
                Bitmap bitmap = Utils.decodeFile(imgFilePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // convert Bitmap to ByteArrayOutputStream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                // convert ByteArrayOutputStream to ByteArrayInputStream
                InputStream in = new ByteArrayInputStream(stream.toByteArray());

                MultipartEntity entity = new MultipartEntity();
                entity.addPart("file", pointRecord.image, in);
                HttpResponse response =
                        RestClient.INSTANCE.post(activity, "file", entity);
                int statusCode = response.getStatusLine().getStatusCode();
                if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity,
                                    "Fail to upload image " + pointRecord.image,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return statusCode;
                }

                FileMgr.delete(activity, pointRecord.image);
                String responseContent = Utils.convertStreamToString(response.getEntity().getContent());
                String imageId = new JSONObject(responseContent).getString("id");
                pointRecord.image = imageId;
            }
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Utils.showErrorPopupWindow(activity, ex);
                }
            });

            return -1;
        }

        RecordProvider.INSTANCE.save(activity);
        return Constants.STATUS_CODE_CREATED;
    }

    private void uploadFile(String file) {
        Log.i(TAG, String.format("Uploading record %s", file));
        try {
            String recordContent = FileMgr.read(activity, file);
            Record record = RecordProvider.INSTANCE.parseRecordString(recordContent);
            int responseStatusCode = uploadImagesInRecord(record);
            if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(responseStatusCode)) {
                statusCode = responseStatusCode;
                throw new IllegalStateException();
            }
            record.setSubmitter(Utils.getSavedUsernameAndPassword(activity)[0]);
            recordContent = RecordProvider.INSTANCE.toString(record);
            HttpResponse response = RestClient.INSTANCE.post(activity, Constants.RESULTS, recordContent, Constants.CONTENT_TYPE_JSON);
            responseStatusCode = response.getStatusLine().getStatusCode();
            if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(responseStatusCode)) {
                statusCode = responseStatusCode;
                throw new IllegalStateException();
            }
            FileMgr.delete(activity, file);
            if (Constants.RECORD_FILE_NAME.equals(file)) {
                RecordProvider.INSTANCE.reset(activity);
            }
        } catch (Exception ex) {
            fails++;
            if (Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                statusCode = -1;
            }
            Log.e(TAG,
                    String.format("Fail to upload file %s:\n%s\n%s",
                            file,
                            ex.getMessage(),
                            ex.getStackTrace())
            );
        }
    }
}