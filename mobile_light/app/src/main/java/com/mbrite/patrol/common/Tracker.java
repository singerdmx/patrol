package com.mbrite.patrol.common;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * In memory tracker of navigation status
 */
public enum Tracker {

    INSTANCE;

    public boolean offLine;

    public Long recentActiveTimestamp = null;

    /**
     * Keep track of current point ids to be shown on PointsActivity
     */
    public TreeSet<Integer> pointGroups;
    public boolean startedScan;
    // key is barcode, value is asset id
    private Map<String, Integer> assetBarcodeMap = new HashMap<>();
    // key is barcode, value is point id
    private Map<String, Integer> pointBarcodeMap = new HashMap<>();

    public void startScan(Activity activity) {
        if (!startedScan) {
            IntentIntegrator integrator = new IntentIntegrator(activity);
            startedScan = true;
            integrator.initiateScan();
        }
    }

    public Map<String, Integer> getAssetBarcodeMap() {
        return assetBarcodeMap;
    }

    public Map<String, Integer> getPointBarcodeMap() {
        return pointBarcodeMap;
    }

    public void reset() {
        pointGroups = null;
        assetBarcodeMap = new HashMap<>();
        pointBarcodeMap = new HashMap<>();
        offLine = false;
    }

}
