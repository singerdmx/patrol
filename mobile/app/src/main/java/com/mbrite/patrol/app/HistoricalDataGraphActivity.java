package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;

public class HistoricalDataGraphActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data_graph);
        setWindowTitle(R.string.historical_data_graph);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Tracker.INSTANCE.targetPoint == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView pointTitle = (TextView) findViewById(R.id.point_title);
        pointTitle.setText(Tracker.INSTANCE.targetPoint.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.historical_data_graph, menu);
        return true;
    }

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
}
