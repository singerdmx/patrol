package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Route;

import java.io.IOException;
import java.util.*;
import org.json.*;

public enum RouteProvider {

    INSTANCE;

    private Activity activity;

    public ArrayList<Route> getRoutes(Activity activity)
            throws JSONException, IOException {
        ArrayList<Route> routes = new ArrayList<Route>();
        String data = FileMgr.read(activity, Constants.ROUTES_FILE_NAME);
        JSONArray routesJSON = new JSONObject(data).getJSONArray(Constants.ROUTES);
        for(int i = 0 ; i < routesJSON.length() ; i++) {
            JSONObject routeJSON = routesJSON.getJSONObject(i);
            routes.add(new Route(routeJSON.getInt(Constants.ID),
                    routeJSON.getString(Constants.DESCRIPTION),
                    getAssetIndexes(routeJSON)));
        }
        return routes;
    }

    public Route getRoute(Activity activity, int routeId)
            throws JSONException, IOException {
        String data = FileMgr.read(activity, Constants.ROUTES_FILE_NAME);
        JSONArray routesJSON = new JSONObject(data).getJSONArray(Constants.ROUTES);
        for(int i = 0 ; i < routesJSON.length() ; i++) {
            JSONObject routeJSON = routesJSON.getJSONObject(i);
            int id = routeJSON.getInt(Constants.ID);
            if (id == routeId) {
                return new Route(id,
                        routeJSON.getString(Constants.DESCRIPTION),
                        getAssetIndexes(routeJSON));
            }
        }
        return null;
    }

    private int[] getAssetIndexes(JSONObject routeJSON)
        throws JSONException {
        JSONArray assets = routeJSON.getJSONArray(Constants.ASSETS);
        int[] assetIndexes = new int[assets.length()];
        for (int j = 0; j < assets.length(); j++) {
            assetIndexes[j] = assets.getInt(j);
        }

        return assetIndexes;
    }
}
