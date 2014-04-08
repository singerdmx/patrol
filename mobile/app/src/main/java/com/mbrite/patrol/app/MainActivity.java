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
    }

    private void setupNotification() {
        TextView notification = (TextView) findViewById(R.id.notification);
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
}
