package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;


public class MainActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWindowTitle(R.string.patrol);

        setupScanButton();
    }


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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Tracker.INSTANCE.startedScan = false;
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            try {
                Toast.makeText(
                        this,
                        scanResult.getContents(),
                        Toast.LENGTH_LONG)
                        .show();
            } catch (Exception ex) {
                Toast.makeText(
                        this,
                        String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(
                    this,
                    getString(R.string.not_scanned),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void setupScanButton() {
        TextView scanButton = (TextView) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.startScan(MainActivity.this);
            }
        });
    }

}
