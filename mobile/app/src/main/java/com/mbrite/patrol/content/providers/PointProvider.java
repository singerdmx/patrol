package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.Point;

import org.json.*;

import java.io.IOException;
import java.util.*;

public enum PointProvider {

    INSTANCE;

    public ArrayList<Point> getPoints(Activity activity)
            throws JSONException, IOException {
        ArrayList<Point> points = new ArrayList<Point>();
        String data = FileMgr.read(activity, Constants.POINTS_FILE_NAME);
        List<JSONObject> pointsJSON = Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.POINTS));
        for(JSONObject pointJSON : pointsJSON) {
            List<Integer> routes = Utils.convertJSONArrayToList(pointJSON.getJSONArray(Constants.ROUTES));
            List<String> choice = Utils.convertJSONArrayToList(new JSONArray(pointJSON.getString(Constants.CHOICE)));
            Point point = new Point(
                    pointJSON.getInt(Constants.ID),
                    pointJSON.getString(Constants.NAME),
                    pointJSON.getString(Constants.DESCRIPTION),
                    pointJSON.getString(Constants.STATE),
                    routes,
                    Utils.getString(pointJSON, Constants.BARCODE),
                    pointJSON.getInt(Constants.CATEGORY),
                    choice);

            points.add(point);
        }
        return points;
    }

}
