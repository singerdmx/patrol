package com.mbrite.patrol.app;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost.*;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;


public class BarcodeActivity extends TabActivity {

    private String targetBarcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        Bundle extras = getIntent().getExtras();
        targetBarcode = extras == null ? null : extras.getString(Constants.BARCODE);

        TabHost tabHost = getTabHost();
        createTabSpec(tabHost, "scan_barcode", R.string.scan_barcode, R.drawable.barcode, ScanBarcodeActivity.class);
        createTabSpec(tabHost, "manual_input", R.string.manual_input, R.drawable.ic_menu_edit, InputBarcodeActivity.class);
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

    private TabSpec createTabSpec(TabHost tabHost, String specTag, int indicator, int drawable, Class activityClass) {
        TabSpec tabSpec = tabHost.newTabSpec(specTag);
        tabSpec.setIndicator(getString(indicator), getResources().getDrawable(drawable));
        Intent intent = new Intent(this, activityClass);
        if (targetBarcode != null) {
            intent.putExtra(Constants.BARCODE, targetBarcode);
        }
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);
        return tabSpec;
    }
}
