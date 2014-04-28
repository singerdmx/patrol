package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.AssetProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

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
        double sum = 0;
        for (AssetGroup a : assetList) {
            if (a.getStatus() != RecordStatus.NOT_STARTED) {
                sum += a.getCompleteness();
            }
        }

        return sum / assetList.size();
    }
}
