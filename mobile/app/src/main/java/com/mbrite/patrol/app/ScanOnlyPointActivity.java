package com.mbrite.patrol.app;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.PointGroup;


public class ScanOnlyPointActivity extends Activity {

    private Button continueScanButton;
    private TextView label;
    private CountDown countDown;
    private PointGroup point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_only_point);

        setupView();

        setupCancelButton();
        setupContinueScanButton();

        label = (TextView) findViewById(R.id.label);

        countDown = new CountDown(5000, 1000);
        countDown.start();
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

    private void setupCancelButton() {
        Button button = (Button) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDown.cancel();
                Intent intent = new Intent(ScanOnlyPointActivity.this, AssetsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupView() {
        int pointId = Tracker.INSTANCE.pointGroups.first();
        point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(point.name);
        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(point.description);
        TextView stateView = (TextView) findViewById(R.id.state);
        stateView.setText(point.state);
    }

    private void setupContinueScanButton() {
        continueScanButton = (Button) findViewById(R.id.continue_scan_button);
        continueScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanOnlyPointActivity.this, AssetsActivity.class);
                intent.putExtra(Constants.CONTINUOUS_SCAN, true);
                startActivity(intent);
                finish();
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
