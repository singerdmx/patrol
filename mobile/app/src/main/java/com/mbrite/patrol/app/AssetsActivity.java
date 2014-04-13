package com.mbrite.patrol.app;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.widget.AssetAdapter;

public class AssetsActivity extends ParentActivity {
    private static final String TAG = AssetsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_assets);
        setupScanButton();
        setupInputButton();
        setupCompleteButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        RecordProvider.INSTANCE.clearState();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.assets);
        AssetAdapter adapter = new AssetAdapter(
                this,
                Tracker.INSTANCE.routeGroups);
        listView.setAdapter(adapter);
        for (int i = 0; i < Tracker.INSTANCE.routeGroups.size(); i++) {
            listView.expandGroup(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.assets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Log.i(TAG, scanResult.getFormatName());
            Log.i(TAG, scanResult.toString());
        }
    }

    private void setupScanButton() {
        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetBarcode = null;
                IntentIntegrator integrator = new IntentIntegrator(AssetsActivity.this);
                integrator.initiateScan();
            }
        });
    }

    private void setupInputButton() {
        Button inputButton = (Button) findViewById(R.id.input_button);
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetBarcode = null;
                final EditText input = new EditText(AssetsActivity.this);
                new AlertDialog.Builder(AssetsActivity.this)
                        .setTitle(getString(R.string.manual_input_barcode))
                        .setView(input)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();
                                Toast.makeText(
                                        AssetsActivity.this,
                                        value,
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        });
    }

    private void setupCompleteButton() {
        Button completeButton = (Button) findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadRecords(AssetsActivity.this, false);
            }
        });
    }

}
