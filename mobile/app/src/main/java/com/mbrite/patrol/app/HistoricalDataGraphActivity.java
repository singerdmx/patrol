package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class HistoricalDataGraphActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data_graph);
        setWindowTitle(R.string.historical_data_graph);

        setupReturnButton();
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

        Spinner graphType = (Spinner) findViewById(R.id.graph_type);
        List<String> types = new ArrayList<>();
        for (int typeResId : Constants.GRAPH_TYPES.keySet()) {
            types.add(getString(typeResId));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphType.setAdapter(dataAdapter);
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

    private void setupReturnButton() {
        TextView button = (TextView) findViewById(R.id.return_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetPoint = null;
                finish();
            }
        });
    }
}
