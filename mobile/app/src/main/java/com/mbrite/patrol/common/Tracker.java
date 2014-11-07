package com.mbrite.patrol.common;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.mbrite.patrol.content.providers.AssetProvider;
import com.mbrite.patrol.content.providers.PointProvider;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.model.AssetGroup;
import com.mbrite.patrol.model.Point;
import com.mbrite.patrol.model.PointGroup;
import com.mbrite.patrol.model.Route;
import com.mbrite.patrol.model.RouteGroup;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Keep track of current route groups to be shown on MainActivity
     */
    public List<RouteGroup> routeGroups;

    /**
     * Keep track of current asset being clicked
     */
    public AssetGroup targetAsset;

    /**
     * Keep track of current point ids to be shown on PointsActivity
     */
    public TreeSet<Integer> pointGroups;
    public boolean startedScan;
    // key is asset id, value is list of AssetGroup having the id
    private Map<Integer, List<AssetGroup>> assetDuplicates = new HashMap<>();
    // key is barcode, value is asset id
    private Map<String, Integer> assetBarcodeMap = new HashMap<>();
    // key is point id, value is list of PointGroup having the id
    private Map<Integer, List<PointGroup>> pointDuplicates = new HashMap<>();
    // key is barcode, value is point id
    private Map<String, Integer> pointBarcodeMap = new HashMap<>();

    public void startScan(Activity activity) {
        if (!startedScan) {
            IntentIntegrator integrator = new IntentIntegrator(activity);
            startedScan = true;
            integrator.initiateScan();
        }
    }

    public Map<Integer, List<AssetGroup>> getAssetDuplicates() {
        return assetDuplicates;
    }

    public Map<Integer, List<PointGroup>> getPointDuplicates() {
        return pointDuplicates;
    }

    public Map<String, Integer> getAssetBarcodeMap() {
        return assetBarcodeMap;
    }

    public Map<String, Integer> getPointBarcodeMap() {
        return pointBarcodeMap;
    }

    public void createRouteGroups(ArrayList<Route> selectedRoutes, Activity activity)
            throws JSONException, IOException {
        routeGroups = new ArrayList<RouteGroup>(selectedRoutes.size());
        ArrayList<Asset> allAssets = AssetProvider.INSTANCE.getAssets(activity);
        ArrayList<Point> allPoints = PointProvider.INSTANCE.getPoints(activity);
        for (Route route : selectedRoutes) {
            routeGroups.add(new RouteGroup(route, allAssets, allPoints));
        }

        processDuplicatesAndBarcode();
    }

    public void reset() {
        routeGroups = null;
        targetAsset = null;
        pointGroups = null;
        assetDuplicates = new HashMap<>();
        assetBarcodeMap = new HashMap<>();
        pointDuplicates = new HashMap<>();
        pointBarcodeMap = new HashMap<>();
        offLine = false;
    }

    public boolean isRecordComplete() {
        if (routeGroups == null) {
            return true;
        }
        for (RouteGroup r : routeGroups) {
            if (!Utils.areEqualDouble(r.getCompleteness(), 1)) {
                return false;
            }
        }

        return true;
    }

    // Get all points of asset under all selected routes
    public TreeSet<Integer> getAllPointIdsInAsset(int assetId) {
        TreeSet<Integer> result = new TreeSet<>();
        for (AssetGroup a : getAssetDuplicates().get(assetId)) {
            for (PointGroup p : a.pointList) {
                result.add(p.id);
            }
        }

        return result;
    }

    private void processDuplicatesAndBarcode() {
        for (RouteGroup routeGroup : routeGroups) {
            for (final AssetGroup assetGroup : routeGroup.assetList) {
                if (!assetDuplicates.containsKey(assetGroup.id)) {
                    assetDuplicates.put(assetGroup.id, new ArrayList<AssetGroup>());
                }
                assetDuplicates.get(assetGroup.id).add(assetGroup);

                if (StringUtils.isNotBlank(assetGroup.barcode)) {
                    assetBarcodeMap.put(assetGroup.barcode, assetGroup.id);
                }
                for (final PointGroup pointGroup : assetGroup.pointList) {
                    if (!pointDuplicates.containsKey(pointGroup.id)) {
                        pointDuplicates.put(pointGroup.id, new ArrayList<PointGroup>());
                    }
                    pointDuplicates.get(pointGroup.id).add(pointGroup);

                    if (StringUtils.isNotBlank(pointGroup.barcode)) {
                        pointBarcodeMap.put(pointGroup.barcode, pointGroup.id);
                    }
                }
            }
        }
    }
}
