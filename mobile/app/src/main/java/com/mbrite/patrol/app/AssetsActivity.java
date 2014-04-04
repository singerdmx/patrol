package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.model.*;

import java.util.*;


public class AssetsActivity extends Activity {
    private static final String TAG = AssetsActivity.class.getSimpleName();
    private int[] assets;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_assets);

        assets = Tracker.INSTANCE.assetIds;
        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssetsActivity.this, BarcodeActivity.class);
                startActivity(intent);
            }
        });

        Button completeButton = (Button) findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!RecordProvider.INSTANCE.isComplete()) {
                        throw new IllegalStateException(getString(R.string.error_incomplete_assets));
                    }
                    Record record = RecordProvider.INSTANCE.get(AssetsActivity.this);
                    if (record.endTime == 0) {
                        record.endTime = System.currentTimeMillis()/1000;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(AssetsActivity.this);
                    builder.setMessage(getString(R.string.upload_data))
                            .setTitle(R.string.complete_patrol)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    progressDialog = ProgressDialog.show(AssetsActivity.this,
                                            getString(R.string.uploading),
                                            getString(R.string.please_wait),
                                            true);
                                    try {
                                        new UploadTask().execute();
                                    } catch (Exception ex) {
                                        Toast.makeText(
                                                AssetsActivity.this,
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
                                        FileMgr.copy(AssetsActivity.this,
                                                Constants.RECORD_FILE_NAME,
                                                String.format("%s.%d", Constants.RECORD_FILE_NAME, System.currentTimeMillis() / 1000));
                                        RecordProvider.INSTANCE.reset(AssetsActivity.this);
                                        startActivity(new Intent(AssetsActivity.this, MainActivity.class));
                                    } catch (Exception ex) {
                                        Toast.makeText(
                                                AssetsActivity.this,
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
                            AssetsActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.assets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.clearUsernameAndPassword(this);
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    class UploadTask extends AsyncTask<Void, Void, Integer> {

        private int total = 0;
        private int fails = 0;
        private int statusCode = 200;

        @Override
        protected Integer doInBackground(Void... unused) {
            try {
                // TODO: upload
                Thread.sleep(2000);
                List<String> recordFiles = RecordProvider.INSTANCE.getRecordFiles(AssetsActivity.this);
                total = recordFiles.size();
                for (String recordFile : recordFiles) {
                    uploadFile(recordFile);
                }
                return statusCode;
            } catch (Exception e) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(AssetsActivity.this,
                        String.format(getString(R.string.error_of), e.getLocalizedMessage()),
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

            if (statusCode != 200) {
                Toast.makeText(getApplicationContext(),
                        String.format("%s\n%s",
                            String.format(getString(R.string.error_upload), fails),
                            String.format(getString(R.string.upload_success), total -fails)),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        String.format(getString(R.string.upload_success), total),
                        Toast.LENGTH_SHORT).show();
                try {
                    RecordProvider.INSTANCE.reset(AssetsActivity.this);
                    startActivity(new Intent(AssetsActivity.this, MainActivity.class));
                } catch (Exception ex) {
                    Toast.makeText(
                            getApplicationContext(),
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        }

        private void uploadFile(String file) {
            Log.i(TAG, String.format("Uploading file %s", file));
            try {
                //TODO: upload
                FileMgr.delete(AssetsActivity.this, file);
            } catch (Exception ex) {
                fails++;
                statusCode = 400;
                Log.e(TAG,
                      String.format("Fail to upload file %s:\n%s\n%s",
                              file,
                              ex.getMessage(),
                              ex.getStackTrace()));
            }
        }
    }

}
