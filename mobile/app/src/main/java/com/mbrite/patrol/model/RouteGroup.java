package com.mbrite.patrol.model;

import android.app.Activity;

import com.mbrite.patrol.content.providers.AssetProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class RouteGroup extends Route {

    public List<Asset> assetList;

    public RouteGroup(Route route, Activity activity)
        throws JSONException, IOException {
        super(route.id, route.description, route.assets);
        assetList = AssetProvider.INSTANCE.getAssets(activity, route.assets);
        for (Asset asset : assetList) {
            asset.routeId = route.id;
        }
    }
}
