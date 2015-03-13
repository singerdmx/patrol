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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UploadTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = UploadTask.class.getSimpleName();
    private Activity activity;
    private int total;
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
        int fails = 0;
        try {
            List<String> recordFiles = RecordProvider.INSTANCE.getRecordsFilesNotUploaded(activity);
            total = 0;
            for (String recordFile : recordFiles) {
                fails += uploadFile(recordFile);
            }

            return fails;
        } catch (final Exception e) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity,
                            String.format(activity.getString(R.string.error_of), e.getLocalizedMessage()),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        return fails + 1;
    }

    @Override
    protected void onProgressUpdate(Void... unused) {

    }

    @Override
    protected void onPostExecute(final Integer fails) {
        if (total - fails > 0) {
            Utils.updateDataFiles(activity);
            if (activity instanceof ParentActivity) {
                ((ParentActivity) activity).onResume();
            }
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (fails == 0) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity,
                            String.format(activity.getString(R.string.upload_success), total),
                            Toast.LENGTH_SHORT).show();
                }
            });
            try {
                // clear all record files (should be non) and image/audio files
                RecordProvider.INSTANCE.removeSavedRecordAndMediaFiles(activity);
            } catch (final Exception ex) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.showErrorPopupWindow(activity, ex);
                    }
                });
            }
            return;
        }

        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity,
                        String.format(activity.getString(R.string.error_upload), fails) + "\n" +
                                String.format(activity.getString(R.string.upload_success), total - fails),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private int uploadAudiosInRecord(Record record, String file) throws IOException {
        try {
            for (final PointRecord pointRecord : record.points) {
                if (pointRecord.audio == null ||
                        !pointRecord.audio.endsWith(Constants.AUDIO_FILE_SUFFIX)) {
                    // no audio or audio is already uploaded (audio is set to id in DB)
                    continue;
                }
                if (!FileMgr.exists(activity, pointRecord.audio)) {
                    throw new IllegalStateException(
                            String.format("Audio %s does not exist", pointRecord.audio));
                }
                String audioFilePath = FileMgr.getFullPath(activity, pointRecord.audio);
                FileInputStream in = new FileInputStream(new File(audioFilePath));
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("file", pointRecord.audio, in);
                HttpResponse response =
                        RestClient.INSTANCE.post(activity, "file", entity);
                int statusCode = response.getStatusLine().getStatusCode();
                if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity,
                                    "Fail to upload image " + pointRecord.audio,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return statusCode;
                }

                FileMgr.delete(activity, pointRecord.audio);
                String responseContent = Utils.convertStreamToString(response.getEntity().getContent());
                String audioId = new JSONObject(responseContent).getString("id");
                pointRecord.audio = audioId;
                // save audio field change
                RecordProvider.INSTANCE.save(activity, file, record);
            }
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Utils.showErrorPopupWindow(activity, ex);
                }
            });

            return -1;
        }

        return Constants.STATUS_CODE_CREATED;
    }

    private int uploadImagesInRecord(Record record, String file) throws IOException {
        try {
            for (final PointRecord pointRecord : record.points) {
                if (pointRecord.image == null ||
                        !pointRecord.image.endsWith(Constants.IMAGE_FILE_SUFFIX)) {
                    // no image or image is already uploaded (image is set to id in DB)
                    continue;
                }
                if (!FileMgr.exists(activity, pointRecord.image)) {
                    throw new IllegalStateException(
                            String.format("Image %s does not exist", pointRecord.image));
                }
                String imgFilePath = FileMgr.getFullPath(activity, pointRecord.image);
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
                // save image field change
                RecordProvider.INSTANCE.save(activity, file, record);
            }
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Utils.showErrorPopupWindow(activity, ex);
                }
            });

            return -1;
        }

        return Constants.STATUS_CODE_CREATED;
    }

    /**
     * @param file
     * @return 0 means success, otherwise 1
     */
    private int uploadFile(String file) {
        Log.i(TAG, String.format("Uploading record %s", file));
        try {
            String recordContent = FileMgr.read(activity, file);
            Record record = RecordProvider.INSTANCE.parseRecordString(recordContent);
            if (record.points.isEmpty()) {
                FileMgr.delete(activity, file);
                return 0;
            }
            total++;
            int statusCode = uploadAudiosInRecord(record, file);
            if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                throw new IllegalStateException();
            }
            statusCode = uploadImagesInRecord(record, file);
            if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                throw new IllegalStateException();
            }
            record.setSubmitter(Utils.getSavedUsernameAndPassword(activity)[0]);
            recordContent = RecordProvider.INSTANCE.toString(record);
            HttpResponse response = RestClient.INSTANCE.post(activity, Constants.RESULTS, recordContent, Constants.CONTENT_TYPE_JSON);
            statusCode = response.getStatusLine().getStatusCode();
            if (!Constants.STATUS_CODE_UPLOAD_SUCCESS.contains(statusCode)) {
                throw new IllegalStateException();
            }
            FileMgr.delete(activity, file);
        } catch (Exception ex) {
            Log.e(TAG,
                    String.format("Fail to upload file %s:\n%s\n%s",
                            file,
                            ex.getMessage(),
                            ex.getStackTrace())
            );
            return 1;
        }

        return 0;
    }
}