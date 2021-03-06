package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mbrite.patrol.common.BarcodeNotMatchException;
import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.AssetGroup;
import com.mbrite.patrol.model.PointGroup;
import com.mbrite.patrol.widget.AssetAdapter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.TreeSet;

public class AssetsActivity extends ParentActivity {
    private static final String TAG = AssetsActivity.class.getSimpleName();
    private TextView scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_assets);
        setWindowTitle(R.string.asset);
        setupScanButton();
        setupSaveDataButton();
        setupCompleteButton();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(Constants.CONTINUOUS_SCAN, false)) {
            if (!Tracker.INSTANCE.startedScan) {
                scanButton.performClick();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.assets);
        if (Tracker.INSTANCE.routeGroups == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
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
            case R.id.summary:
                Intent intent = new Intent(this, SummaryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.manual_input:
                manualInput();
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
                checkBarcode(scanResult.getContents());
            } catch (BarcodeNotMatchException ex) {
                Toast.makeText(
                        this,
                        ex.getLocalizedMessage(),
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
        scanButton = (TextView) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetAsset = null;
                Tracker.INSTANCE.startScan(AssetsActivity.this);
            }
        });
    }

    private void manualInput() {
        Tracker.INSTANCE.targetAsset = null;
        final EditText input = new EditText(AssetsActivity.this);
        new AlertDialog.Builder(AssetsActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                .setTitle(getString(R.string.manual_input_barcode))
                .setView(input)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        try {
                            checkBarcode(value, true);
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

    private void setupSaveDataButton() {
        TextView saveDataButton = (TextView) findViewById(R.id.save_data_button);
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
        TextView completeButton = (TextView) findViewById(R.id.complete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeRecord(AssetsActivity.this);
            }
        });
        if (Tracker.INSTANCE.isRecordComplete()) {
            completeButton.performClick();
        }
    }

    private void checkBarcode(String barcode)
            throws IOException, JSONException, BarcodeNotMatchException {
        checkBarcode(barcode, false);
    }

    private void checkBarcode(String barcode, boolean manualInputMode)
            throws IOException, JSONException, BarcodeNotMatchException {
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
            throw new BarcodeNotMatchException(getString(R.string.error_incorrect_barcode));
        }

        if (pointId != null) {
            Tracker.INSTANCE.pointGroups.add(pointId);
        } else {
            // assetId != null
            // Get all points of asset under all selected routes
            Tracker.INSTANCE.pointGroups = Tracker.INSTANCE.getAllPointIdsInAsset(assetId);
        }

        if (Utils.isScanOnly(pointId, assetId, targetAsset, this)) {
            if (!manualInputMode && Utils.getContinuousScanMode(this)) {
                Intent intent = new Intent(this, ScanOnlyPointActivity.class);
                startActivity(intent);
            } else {
                onResume();
            }
            return;
        }

        Intent intent = new Intent(this, PointsActivity.class);
        startActivity(intent);
    }

}
