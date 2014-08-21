package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.PointGroup;


public class ScanOnlyPointActivity extends ParentActivity {

    private TextView continueScanButton;
    private TextView label;
    private CountDown countDown;
    private PointGroup point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_only_point);
        setWindowTitle(R.string.check_point);

        setupView();

        setupCancelButton();
        setupContinueScanButton();

        label = (TextView) findViewById(R.id.label);

        countDown = new CountDown(5000, 1000);
        countDown.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDown.cancel();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_only_point, menu);
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

    private void setupView() {
        if (Tracker.INSTANCE.pointGroups == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        int pointId = Tracker.INSTANCE.pointGroups.first();
        point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(point.name);
        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(point.description);
        TextView stateView = (TextView) findViewById(R.id.state);
        stateView.setText(point.state);
    }

    private void setupCancelButton() {
        TextView button = (TextView) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDown.cancel();
                Intent intent = new Intent(ScanOnlyPointActivity.this, AssetsActivity.class);
                startActivity(intent);
                ScanOnlyPointActivity.this.finish();
            }
        });
    }

    private void setupContinueScanButton() {
        continueScanButton = (TextView) findViewById(R.id.continue_scan_button);
        continueScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDown.cancel();
                Intent intent = new Intent(ScanOnlyPointActivity.this, AssetsActivity.class);
                intent.putExtra(Constants.CONTINUOUS_SCAN, true);
                startActivity(intent);
                ScanOnlyPointActivity.this.finish();
            }
        });
    }

    private class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            continueScanButton.performClick();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            label.setText(String.format(getString(R.string.enter_scan_in_secs), millisUntilFinished / 1000));
        }
    }
}
