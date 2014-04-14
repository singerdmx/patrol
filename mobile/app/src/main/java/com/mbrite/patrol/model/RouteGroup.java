package com.mbrite.patrol.model;

import android.app.Activity;

import com.mbrite.patrol.content.providers.AssetProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class RouteGroup extends Route {

    public List<AssetGroup> assetList;

    public RouteGroup(Route route, ArrayList<Asset> allAssets)
        throws JSONException, IOException {
        super(route.id, route.description, route.assets);
        List<Asset> assets = AssetProvider.INSTANCE.filterAssets(route.assets, allAssets);
        assetList = new ArrayList<AssetGroup>(assets.size());
        for (Asset asset : assets) {
            assetList.add(new AssetGroup(asset, this));
        }
    }
}
