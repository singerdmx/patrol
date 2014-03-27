package com.mbrite.patrol.app;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost.*;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Utils;


public class BarcodeActivity extends TabActivity {

    private String targetBarcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        TabHost tabHost = getTabHost();

        TabSpec scanBarcodeSpec = tabHost.newTabSpec("scan_barcode");
        scanBarcodeSpec.setIndicator(getString(R.string.scan_barcode), getResources().getDrawable(R.drawable.barcode));
        Intent scanBarcodeIntent = new Intent(this, ScanBarcodeActivity.class);
        scanBarcodeSpec.setContent(scanBarcodeIntent);

        TabSpec manualInputSpec = tabHost.newTabSpec("manual_input");
        manualInputSpec.setIndicator(getString(R.string.manual_input), getResources().getDrawable(R.drawable.ic_menu_edit));
        Intent inputBarcodeIntent = new Intent(this, InputBarcodeActivity.class);
        manualInputSpec.setContent(inputBarcodeIntent);

        tabHost.addTab(scanBarcodeSpec);
        tabHost.addTab(manualInputSpec);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            targetBarcode = extras.getString(Constants.BARCODE);
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

}
