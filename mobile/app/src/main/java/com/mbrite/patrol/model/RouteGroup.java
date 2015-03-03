package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.AssetProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteGroup extends Route {

    public List<AssetGroup> assetList;

    public RouteGroup(Route route, ArrayList<Asset> allAssets, ArrayList<Point> allPoints)
            throws JSONException, IOException {
        super(route.id, route.name, route.assets);
        List<Asset> assets = AssetProvider.INSTANCE.filterAssets(route.assets, allAssets);
        assetList = new ArrayList<AssetGroup>(assets.size());
        for (Asset asset : assets) {
            assetList.add(new AssetGroup(asset, allPoints, this));
        }
    }

    public double getCompleteness() {
        return (double) getStarted() / getTotal();
    }

    public int getStarted() {
        int started = 0;
        for (AssetGroup a : assetList) {
            started += a.getStarted();
        }

        return started;
    }

    public int getTotal() {
        int total = 0;
        for (AssetGroup a : assetList) {
            total += a.getTotal();
        }

        return total;
    }
}
