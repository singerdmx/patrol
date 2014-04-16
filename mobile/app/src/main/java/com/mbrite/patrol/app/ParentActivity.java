package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;


/**
 * Parent class for shared methods.
 */
public class ParentActivity extends Activity {

    private ProgressDialog progressDialog;

    public void uploadRecords(final Activity activity, final boolean updateRecordFiles) {
        try {
            if (false) {
                // TODO: check incomplete
                throw new IllegalStateException(getString(R.string.error_incomplete_assets));
            }
            Record record = RecordProvider.INSTANCE.get(activity);
            if (record != null && record.end_time == 0) {
                record.end_time = System.currentTimeMillis()/1000;
                RecordProvider.INSTANCE.save(activity, record);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(activity.getString(R.string.upload_data))
                    .setTitle(R.string.complete_patrol)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            progressDialog = ProgressDialog.show(activity,
                                    getString(R.string.uploading),
                                    getString(R.string.please_wait),
                                    true);
                            try {
                                new UploadTask(activity, progressDialog, updateRecordFiles).execute();
                            } catch (Exception ex) {
                                Toast.makeText(
                                        activity,
                                        String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            try {
                                FileMgr.copy(activity,
                                        Constants.RECORD_FILE_NAME,
                                        String.format("%s.%d", Constants.RECORD_FILE_NAME, System.currentTimeMillis() / 1000));
                                RecordProvider.INSTANCE.reset(activity);
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } catch (Exception ex) {
                                Toast.makeText(
                                        activity,
                                        String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception ex) {
            Toast.makeText(
                    activity,
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
