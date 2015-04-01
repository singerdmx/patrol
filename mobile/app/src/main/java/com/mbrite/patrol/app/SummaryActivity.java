package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.content.providers.RouteProvider;
import com.mbrite.patrol.model.Record;
import com.mbrite.patrol.model.RouteGroup;

import java.util.Calendar;
import java.util.TimeZone;

public class SummaryActivity extends ParentActivity {

    private static final TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    TextView continueBtn;
    TextView completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        setWindowTitle(R.string.summary);
        setupContinueButton();
        setupCompleteButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUsername();
        setupStatus();
        setupCurrentRoutes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    private void setupUsername() {
        TextView username = (TextView) findViewById(R.id.username);
        username.setText(Utils.getUserName(this));
    }

    private void setupStatus() {
        TextView time = (TextView) findViewById(R.id.time);
        Calendar c = Calendar.getInstance(timeZone);
        time.setText(String.format("%d/%02d/%02d   %s",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                Constants.DAY_OF_WEEK[c.get(Calendar.DAY_OF_WEEK)]));

        TextView status = (TextView) findViewById(R.id.status);
        String networkStatus = Utils.isNetworkConnected(this) ?
                getString(R.string.network_connected) : getString(R.string.network_not_connected);
        String uploadStatus = String.format(
                getString(R.string.not_uploaded),
                RecordProvider.INSTANCE.getRecordsFilesNotUploaded(this).size());
        status.setText(networkStatus + "   " + uploadStatus);
    }

    private void setupCurrentRoutes() {
        TextView taskTitle = (TextView) findViewById(R.id.taskTitle);
        TextView currentRoutes = (TextView) findViewById(R.id.currentRoutes);

        try {
            Record record = RecordProvider.INSTANCE.get(this);
            if (record != null) {
                taskTitle.setText(R.string.current_task);
                Tracker.INSTANCE.createRouteGroups(
                        RouteProvider.INSTANCE.getRoutes(this), record, this);
                StringBuilder sb = new StringBuilder();

                for (RouteGroup route : Tracker.INSTANCE.routeGroups) {
                    sb.append(String.format("%s (%d/%d)\n",
                            route.name, route.getStarted(), route.getTotal()));
                }
                completeButton.setVisibility(View.VISIBLE);
                currentRoutes.setText(sb.toString());
                continueBtn.setText(R.string.continue_task);
                return;
            }

            completeButton.setVisibility(View.INVISIBLE);
            currentRoutes.setText("");
            continueBtn.setText(R.string.new_task);
            taskTitle.setText(R.string.no_current_task);
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(this, ex);
        }
    }

    private void setupContinueButton() {
        continueBtn = (TextView) findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupCompleteButton() {
        completeButton = (TextView) findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeRecord(SummaryActivity.this);
            }
        });
    }
}
