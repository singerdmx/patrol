package com.mbrite.patrol.common;

import android.app.Activity;

import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.*;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

/**
 * In memory tracker of navigation status
 */
public enum  Tracker {

    INSTANCE;

    /**
     * Keep track of current route groups to be shown on AssetsActivity
     */
    public List<RouteGroup> routeGroups;

    /**
     * Keep track of current asset ids available
     */
    public Set<Integer> assetIds;

    public String targetBarcode;

    public void createRouteGroups(ArrayList<Route> selectedRoutes, Activity activity)
        throws JSONException, IOException {
        routeGroups = new ArrayList<RouteGroup>(selectedRoutes.size());
        assetIds = new TreeSet<Integer>();
        ArrayList<Asset> allAssets = AssetProvider.INSTANCE.getAssets(activity);
        ArrayList<Point> allPoint = PointProvider.INSTANCE.getPoints(activity);
        for (Route route : selectedRoutes) {
            routeGroups.add(new RouteGroup(route, allAssets));
            assetIds.addAll(route.assets);
        }
    }

    public void setAssetIds(int routeId) {
        Set<Integer> result = new TreeSet<>();
        for (RouteGroup routeGroup : routeGroups) {
            if (routeGroup.id == routeId) {
                result.addAll(routeGroup.assets);
            }
        }

        assetIds = result;
    }

    public AssetGroup getAsset(String barcode) {
        for (RouteGroup routeGroup : routeGroups) {
            for (AssetGroup assetGroup : routeGroup.assetList) {
                if (assetGroup.barcode.equals(barcode)) {
                    for (int assetId : assetIds) {
                        if (assetId == assetGroup.id) {
                            return assetGroup;
                        }
                    }
                    return null;
                }
            }
        }

        return null;
    }

    public AssetGroup getAsset(int assetId) {
        for (RouteGroup routeGroup : routeGroups) {
            for (AssetGroup assetGroup : routeGroup.assetList) {
                if (assetId == assetGroup.id) {
                    return assetGroup;
                }
            }
        }

        return null;
    }
}
