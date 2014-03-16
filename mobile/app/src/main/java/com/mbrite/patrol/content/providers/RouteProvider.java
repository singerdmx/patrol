package com.mbrite.patrol.content.providers;

import android.app.Activity;
import android.widget.*;

import com.mbrite.patrol.model.Route;

import java.util.*;

public class RouteProvider {

    private Activity activity;

    public RouteProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Route> getRoutes() {
        ArrayList<Route> routes = new ArrayList<Route>();
        int index = 0;

        String[] values = new String[] {
                "一工区机械8小时点巡检",
                "二工区机械10小时点巡检",
                "三工区机械8小时点巡检",
                "东门四工区重工机械12小时点巡检路线加长加长加长加长加长加长",
                "五工区机械8小时点巡检"
        };

        try {
            for (String value : values) {
                routes.add(new Route(index++, value));
            }
        }
        catch (Exception ex) {
            // TODO: load data from get request call and display error message upon network failure
            Toast.makeText(activity,
                    String.format("Error: %s", ex.toString()),
                    Toast.LENGTH_LONG).show();
        }
        return routes;
    }
}
