package com.mbrite.patrol.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.view.*;

import com.google.zxing.integration.android.*;

public class ScanBarcodeActivity extends BarcodeParentActivity {

    TextView tvStatus;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvResult = (TextView) findViewById(R.id.tvResult);

        final Button scanBtn = (Button) findViewById(R.id.btnScan);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentIntegrator integrator = new IntentIntegrator(ScanBarcodeActivity.this);
                    integrator.initiateScan();
                } catch (Exception ex) {
                    scanBtn.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            ScanBarcodeActivity.this,
                            String.format(getString(R.string.error_of), ex.toString()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            tvStatus.setText(scanResult.getFormatName());
            tvResult.setText(scanResult.toString());
        } else if (resultCode == RESULT_CANCELED) {
            tvStatus.setText("Press a button to start a scan.");
            tvResult.setText("Scan cancelled.");
        }
    }

}
