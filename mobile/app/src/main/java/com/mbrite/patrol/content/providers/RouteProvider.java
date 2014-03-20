package com.mbrite.patrol.content.providers;

import android.app.Activity;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Route;

import java.util.*;
import org.json.*;

public class RouteProvider {

    private Activity activity;

    public RouteProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Route> getRoutes() {
        ArrayList<Route> routes = new ArrayList<Route>();
        try {
            String data = FileMgr.read(activity, Constants.ROUTES_FILE_NAME);
            JSONArray routesJSON = new JSONObject(data).getJSONArray(Constants.ROUTES);
            for(int i = 0 ; i < routesJSON.length() ; i++) {
                JSONObject routeJSON = routesJSON.getJSONObject(i);
                routes.add(new Route(routeJSON.getInt("id"), routeJSON.getString("description")));
            }
        }
        catch (JSONException ex) {
            Toast.makeText(
                    activity,
                    String.format("JSONException: %s", ex.toString()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        catch (Exception ex) {
            Toast.makeText(
                    activity,
                    String.format("Error: %s", ex.toString()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return routes;
    }
}
