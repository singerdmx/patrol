package com.mbrite.patrol.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;

/**
 * Parent class for shared methods.
 */
public class ParentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        checkRecentActiveTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkRecentActiveTime();
    }

    protected void setWindowTitle(int titleResId) {
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        TextView headerTitle = (TextView) findViewById(R.id.headerTitle);
        headerTitle.setText(titleResId);
    }

    protected void completeRecord(final Activity activity) {
        int msgId = R.string.whether_save_record;

        if (!Tracker.INSTANCE.isRecordComplete()) {
            msgId = R.string.whether_save_incomplete_data;
        }

        new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                .setMessage(msgId)
                .setTitle(R.string.complete_patrol)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(Constants.ROUTES, RecordProvider.INSTANCE.getRoutes());
                            // completeCurrentRecord will reset record, so run it after getRoutes
                            RecordProvider.INSTANCE.completeCurrentRecord(activity);
                            startActivity(intent);
                            finish();
                        } catch (Exception ex) {
                            Utils.showErrorPopupWindow(activity, ex);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                }).setIcon(R.drawable.question).show();
    }

    // Tracker in memory might be reclaimed by Android system
    // We will start from LoginActivity if so
    private void checkRecentActiveTime() {
        if (Tracker.INSTANCE == null || Tracker.INSTANCE.recentActiveTimestamp == null ||
                (System.currentTimeMillis() - Tracker.INSTANCE.recentActiveTimestamp) > 10 * 60 * 1000) {
            Tracker.INSTANCE.recentActiveTimestamp = System.currentTimeMillis();
            startActivity(new Intent(this, LoginActivity.class));
        }
        Tracker.INSTANCE.recentActiveTimestamp = System.currentTimeMillis();
    }
}
