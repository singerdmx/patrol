package com.mbrite.patrol.content.providers;

import android.app.Activity;
import android.widget.*;

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
        int index = 1;

        // TODO: load json data from file "routes.json"
        String[] values = new String[] {
                "一工区机械8小时点巡检",
                "二工区机械10小时点巡检",
                "三工区机械8小时点巡检",
                "东门四工区重工机械12小时点巡检路线加长加长加长加长加长加长",
                "五工区机械8小时点巡检"
        };

        StringBuilder sb = new StringBuilder("{routes :[");
        sb.append("{id:0,description:四工区重工机械12小时点巡检}");
        for (String value : values) {
            sb.append(String.format(",{id:%d,description:%s}", index++, value));
        }
        sb.append("]}");
        String data = sb.toString();
        try {
            JSONArray routesJSON = new JSONObject(data).getJSONArray("routes");
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
