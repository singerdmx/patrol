package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Route;

import java.io.IOException;
import java.util.*;
import org.json.*;

public class RouteProvider {

    private Activity activity;

    public RouteProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Route> getRoutes()
            throws JSONException, IOException {
        ArrayList<Route> routes = new ArrayList<Route>();
        String data = FileMgr.read(activity, Constants.ROUTES_FILE_NAME);
        JSONArray routesJSON = new JSONObject(data).getJSONArray(Constants.ROUTES);
        for(int i = 0 ; i < routesJSON.length() ; i++) {
            JSONObject routeJSON = routesJSON.getJSONObject(i);
            JSONArray assets = routeJSON.getJSONArray(Constants.ASSETS);
            int[] assetIndexes = new int[assets.length()];
            for (int j = 0; j < assets.length(); j++) {
                assetIndexes[j] = assets.getInt(j);
            }
            routes.add(new Route(routeJSON.getInt(Constants.ID),
                    routeJSON.getString(Constants.DESCRIPTION),
                    assetIndexes));
        }
        return routes;
    }
}
