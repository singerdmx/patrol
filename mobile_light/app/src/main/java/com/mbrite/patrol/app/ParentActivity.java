package com.mbrite.patrol.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.mbrite.patrol.common.Tracker;

/**
 * Parent class for shared methods.
 */
public class ParentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
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
