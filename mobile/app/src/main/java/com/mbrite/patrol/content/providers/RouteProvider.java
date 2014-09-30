package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum RouteProvider {

    INSTANCE;

    public ArrayList<Route> getRoutes(Activity activity)
            throws JSONException, IOException {
        ArrayList<Route> routes = new ArrayList<>();
        if (!FileMgr.exists(activity, Constants.ROUTES_FILE_NAME)) {
            return routes;
        }
        String data = FileMgr.read(activity, Constants.ROUTES_FILE_NAME);
        List<JSONObject> routesJSON = Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.ROUTES));
        for (JSONObject routeJSON : routesJSON) {
            routes.add(new Route(routeJSON.getInt(Constants.ID),
                    routeJSON.getString(Constants.DESCRIPTION),
                    getAssetIndexes(routeJSON)));
        }
        return routes;
    }

    private List<Integer> getAssetIndexes(JSONObject routeJSON)
            throws JSONException {
        List<Integer> result = new ArrayList<>();
        JSONArray assetArray = routeJSON.getJSONArray(Constants.ASSETS);
        for (int i = 0; i < assetArray.length(); i++) {
            JSONObject asset = assetArray.getJSONObject(i);
            result.add(asset.getInt(Constants.ID));
        }
        return result;
    }
}
