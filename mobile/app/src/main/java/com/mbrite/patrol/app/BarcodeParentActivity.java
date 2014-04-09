package com.mbrite.patrol.app;

import android.app.Activity;
import android.content.Intent;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.AssetProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Asset;

import org.json.JSONException;

import java.io.IOException;

/**
 * Parent class for shared methods.
 */
public class BarcodeParentActivity extends Activity {

    /**
     * Shared by InputBarcodeActivity and ScanBarcodeActivity
     */
    protected void checkBarcode(Activity activity, String barcode, String targetBarcode)
        throws IOException, JSONException {
        if (targetBarcode != null) {
            // verify barcode
            if (!targetBarcode.equals(barcode)) {
                throw new IllegalStateException(getString(R.string.error_incorrect_barcode));
            }
        }

        Asset asset = AssetProvider.INSTANCE.getAsset(activity, barcode, Tracker.INSTANCE.assetIds);
        if (asset == null) {
            throw new IllegalStateException(getString(R.string.error_incorrect_barcode));
        }
        RecordProvider.INSTANCE.offerAsset(activity, asset.id);
        Intent intent = new Intent(activity, PointsActivity.class);
        intent.putExtra(Constants.POINTS, asset.points);
        startActivity(intent);
        finish();
    }
}
