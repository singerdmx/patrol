package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.*;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.NotificationProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;

import org.apache.commons.lang3.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;

import java.util.concurrent.*;
import java.util.*;

public class MainActivity extends ParentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final Semaphore NOTIFICATION_SYNC_LOCK = new Semaphore(1);

    // UI references.
    private TextView notificationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_main);
        setupNotification();
        setupSynchronizeData();
        setupStartPatrol();
    }

    @Override
    public void onResume() {
        super.onResume();
        new UserNotificationTask().execute((Void) null);
    }

    // Called to lazily initialize the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Called every time user clicks on an action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.logout(this);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void setupNotification() {
        notificationView = (TextView) findViewById(R.id.notification);
        notificationView.setTextColor(Utils.getColorStateList(this, R.drawable.textview_alter_selector));
        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                notificationView
                        .setCompoundDrawablesWithIntrinsicBounds(null,
                                getResources().getDrawable(R.drawable.mail),
                                null,
                                null);
                finish();
            };
        });
    }

    private void setupSynchronizeData() {
        final TextView refresh = (TextView) findViewById(R.id.refresh);
        refresh.setTextColor(Utils.getColorStateList(this, R.drawable.textview_alter_selector));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Tracker.INSTANCE.offLine || Constants.OFFLINE.equals(Utils.getSavedUsernameAndPassword(MainActivity.this)[0])) {
                    new AlertDialog.Builder(MainActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                            .setMessage(R.string.not_logged_in)
                            .setTitle(R.string._notice)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Utils.logout(MainActivity.this);
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // do nothing
                                }
                            }).setIcon(R.drawable.question).show();
                } else {
                    synchronizeData(refresh);
                }
            };
        });
    }

    private void synchronizeData(TextView refresh) {
        refresh.setBackground(getResources().getDrawable(R.drawable.background_darkblue));
        if (!Tracker.INSTANCE.isRecordComplete()) {
            new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setMessage(R.string.error_incomplete_assets)
                    .setTitle(R.string.error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    }).setIcon(R.drawable.error).show();
            return;
        }

        if (!Utils.isNetworkConnected(MainActivity.this)) {
            new AlertDialog.Builder(MainActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setMessage(R.string.error_no_network)
                    .setTitle(R.string.error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    }).setIcon(R.drawable.error).show();
            return;
        }

        Utils.updateDataFiles(MainActivity.this);
        new UploadTask(MainActivity.this).execute();
    }

    private void setupStartPatrol() {
        final TextView start = (TextView) findViewById(R.id.start);
        start.setTextColor(Utils.getColorStateList(this, R.drawable.textview_alter_selector));
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize);
                RoutesFragment fragment = (RoutesFragment) getFragmentManager().findFragmentById(R.id.routes);
                final ArrayList<Route> selectedRoutes = new ArrayList<Route>();
                List<String> selectedRoutesString = new ArrayList<String>();
                for (Route route : fragment.getRoutes()) {
                    if (route.isSelected()) {
                        selectedRoutes.add(route);
                        selectedRoutesString.add(route.name);
                    }
                }

                if (selectedRoutes.isEmpty()) {
                    Toast.makeText(
                            MainActivity.this,
                            getString(R.string.no_route),
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                String message = String.format(getString(R.string.selected_route),
                        StringUtils.join(selectedRoutesString, getString(R.string.comma)));

                builder.setMessage(message)
                        .setTitle(R.string.confirm_route)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Record record = RecordProvider.INSTANCE.get(MainActivity.this);
                                    if (record == null) {
                                        RecordProvider.INSTANCE.create(MainActivity.this, Utils.getSavedUsernameAndPassword(MainActivity.this)[0]);
                                    }
                                    RecordProvider.INSTANCE.setRoutes(selectedRoutes, MainActivity.this);
                                    Tracker.INSTANCE.createRouteGroups(selectedRoutes, MainActivity.this);
                                    Intent intent = new Intent(MainActivity.this, AssetsActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception ex) {
                                    Toast.makeText(
                                            MainActivity.this,
                                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setIcon(R.drawable.question);
                alert.show();
            };
        });
    }

    /**
     * Represents an asynchronous task used to retrieve notification.
     */
    private class UserNotificationTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (Tracker.INSTANCE.offLine ||
                    Constants.OFFLINE.equals(Utils.getSavedUsernameAndPassword(MainActivity.this)[0]) ||
                    !Utils.isNetworkConnected(MainActivity.this)) {
                return false;
            }

            if (NOTIFICATION_SYNC_LOCK.tryAcquire()) {
                try {
                    Map<String, String> headers = null;
                    String ifModifiedSince = NotificationProvider.INSTANCE.getIfModifiedSince(MainActivity.this);
                    if (StringUtils.isNoneBlank(ifModifiedSince)) {
                        headers = new HashMap<>();
                        headers.put(Constants.IF_MODIFIED_SINCE, ifModifiedSince);
                    }
                    HttpResponse response = RestClient.INSTANCE
                            .get(MainActivity.this,
                                    String.format("%s.json", Constants.NOTIFICATION),
                                    headers);
                    int statusCode = response.getStatusLine().getStatusCode();
                    switch (statusCode) {
                        case Constants.STATUS_CODE_OK:
                            String responseContent = Utils.convertStreamToString(response.getEntity().getContent());
                            JSONArray responseData = new JSONArray(responseContent);
                            Header lastModified = response.getFirstHeader(Constants.LAST_MODIFIED);
                            NotificationProvider.INSTANCE
                                    .addNewNotifications(
                                            MainActivity.this,
                                            responseData,
                                            lastModified == null ? null : lastModified.getValue());
                            break;
                        case Constants.STATUS_CODE_NOT_MODIFIED:
                            break;
                        case Constants.STATUS_CODE_UNAUTHORIZED:
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(
                                            MainActivity.this,
                                            R.string.error_incorrect_password_please_login,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                            break;
                        default:
                            throw new HttpResponseException(statusCode,
                                    "Error occurred for GET request");
                    }

                } catch (final Exception ex) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                } finally {
                    NOTIFICATION_SYNC_LOCK.release();
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean newNotification) {
            if (newNotification)  {
                try {
                    ArrayList<Notification> newNotifications = NotificationProvider.INSTANCE.getNewNotifications(MainActivity.this);
                    if (!newNotifications.isEmpty()) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                notificationView
                                        .setCompoundDrawablesWithIntrinsicBounds(null,
                                                getResources().getDrawable(R.drawable.mailplus),
                                                null,
                                                null);
                            }
                        });
                    }
                } catch (final Exception ex) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
        }

    }
}
