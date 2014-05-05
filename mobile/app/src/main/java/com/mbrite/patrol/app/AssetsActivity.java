package com.mbrite.patrol.app;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mbrite.patrol.common.*;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;
import com.mbrite.patrol.widget.AssetAdapter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class AssetsActivity extends Activity {
    private static final String TAG = AssetsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_assets);
        setupScanButton();
        setupInputButton();
        setupSaveDataButton();
        setupCompleteButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.assets);
        AssetAdapter adapter = new AssetAdapter(
                this,
                Tracker.INSTANCE.routeGroups);
        listView.setAdapter(adapter);
        for (int i = 0; i < Tracker.INSTANCE.routeGroups.size(); i++) {
            if (!Utils.areEqualDouble(Tracker.INSTANCE.routeGroups.get(i).getCompleteness(), 1)) {
                listView.expandGroup(i);
            }
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
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            try {
                checkBarcode(scanResult.getContents());
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
        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetAsset = null;
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
                Tracker.INSTANCE.targetAsset = null;
                final EditText input = new EditText(AssetsActivity.this);
                new AlertDialog.Builder(AssetsActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                        .setTitle(getString(R.string.manual_input_barcode))
                        .setView(input)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();
                                try {
                                    checkBarcode(value);
                                } catch (Exception ex) {
                                    Toast.makeText(
                                            AssetsActivity.this,
                                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).setIcon(android.R.drawable.ic_menu_edit).show();
            }
        });
    }

    private void setupSaveDataButton() {
        Button saveDataButton = (Button) findViewById(R.id.save_data_button);
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        AssetsActivity.this,
                        R.string.data_saved,
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupCompleteButton() {
        Button completeButton = (Button) findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeRecord();
            }
        });
        if (Tracker.INSTANCE.isRecordComplete()) {
            completeButton.performClick();
        }
    }

    private void completeRecord() {
        int msgId = R.string.whether_save_record;

        if (!Tracker.INSTANCE.isRecordComplete()) {
            msgId = R.string.whether_save_incomplete_data;
        }

        new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                .setMessage(msgId)
                .setTitle(R.string.complete_patrol)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            RecordProvider.INSTANCE.completeCurrentRecord(AssetsActivity.this);
                            Intent intent = new Intent(AssetsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } catch (Exception ex) {
                            Toast.makeText(
                                    AssetsActivity.this,
                                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                }).setIcon(R.drawable.question).show();
    }

    private void checkBarcode(String barcode)
            throws IOException, JSONException {
        AssetGroup targetAsset = Tracker.INSTANCE.targetAsset;
        Tracker.INSTANCE.pointGroups = new TreeSet<>(); // Reset pointGroups to be displayed on PointsActivity
        Integer assetId = null, pointId = null;
        if (targetAsset != null) {
            // one asset is clicked
            if (StringUtils.isNoneBlank(targetAsset.barcode) &&
                    barcode.equals(targetAsset.barcode)) {
                // verify barcode
                assetId = targetAsset.id;
            } else {
                // look for barcode in targetAsset's points
                for (PointGroup p : targetAsset.pointList) {
                    if (barcode.equals(p.barcode)) {
                        pointId = p.id;
                        break;
                    }
                }
            }
        } else {
            // Either scan or manual input button is pressed
            // Try Point first
            pointId = Tracker.INSTANCE.getPointBarcodeMap().get(barcode);

            if (pointId == null) {
                assetId = Tracker.INSTANCE.getAssetBarcodeMap().get(barcode);
            }
        }

        if (assetId == null && pointId == null) {
            throw new IllegalStateException(getString(R.string.error_incorrect_barcode));
        }

        if (pointId != null) {
            Tracker.INSTANCE.pointGroups.add(pointId);
        } else {
            // assetId != null
            // Get all points of asset under all selected routes
            Tracker.INSTANCE.pointGroups = Tracker.INSTANCE.getAllPointIdsInAsset(assetId);
        }

        if (Utils.isScanOnly(pointId, assetId, targetAsset, this)) {
            onResume();
            return;
        }

        Intent intent = new Intent(this, PointsActivity.class);
        startActivity(intent);
        finish();
    }

}
