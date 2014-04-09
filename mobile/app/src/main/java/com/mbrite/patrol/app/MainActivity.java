package com.mbrite.patrol.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.*;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_main);
        setupNotification();
        setupSynchronizeDate();
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

    private void setupNotification() {
        final TextView notification = (TextView) findViewById(R.id.notification);
        notification.setTextColor(Utils.getColorStateList(this, R.drawable.textview_alter_selector));
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Show notification
                Toast.makeText(
                        MainActivity.this,
                        "Show notification",
                        Toast.LENGTH_LONG)
                        .show();
            };
        });
    }

    private void setupSynchronizeDate() {
        final TextView refresh = (TextView) findViewById(R.id.refresh);
        refresh.setTextColor(Utils.getColorStateList(this, R.drawable.textview_alter_selector));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean updated = Utils.updateSavedFile(MainActivity.this, Constants.ROUTES, Constants.ROUTES_FILE_NAME) ||
                            Utils.updateSavedFile(MainActivity.this, Constants.ASSETS, Constants.ASSETS_FILE_NAME) ||
                            Utils.updateSavedFile(MainActivity.this, Constants.POINTS, Constants.POINTS_FILE_NAME);

                    if (updated) {
                        Toast.makeText(
                                MainActivity.this,
                                R.string.update_complete,
                                Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                R.string.no_update,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception ex) {
                    refresh.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            MainActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            };
        });
    }
}
