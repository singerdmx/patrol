package com.mbrite.patrol.common;

import android.app.Activity;

import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.*;

import org.json.JSONException;
import org.apache.commons.lang3.*;

import java.io.IOException;
import java.util.*;

/**
 * In memory tracker of navigation status
 */
public enum  Tracker {

    INSTANCE;

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
    public Set<Integer> pointGroups;

    // key is asset id, value is list of AssetGroup having the id
    private Map<Integer, List<AssetGroup>> assetDuplicates = new HashMap<>();

    // key is barcode, value is asset id
    private Map<String, Integer> assetBarcodeMap = new HashMap<>();

    // key is point id, value is list of PointGroup having the id
    private Map<Integer, List<PointGroup>> pointDuplicates = new HashMap<>();

    // key is barcode, value is point id
    private Map<String, Integer> pointBarcodeMap = new HashMap<>();

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

                    if (StringUtils.isNoneBlank(pointGroup.barcode)) {
                        pointBarcodeMap.put(pointGroup.barcode, pointGroup.id);
                    }
                }
            }
        }
    }
}
