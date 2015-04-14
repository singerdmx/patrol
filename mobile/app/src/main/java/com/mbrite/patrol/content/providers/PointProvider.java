package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.Point;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum PointProvider {

    INSTANCE;

    public ArrayList<Point> getPoints(Activity activity)
            throws JSONException, IOException {
        ArrayList<Point> points = new ArrayList<>();
        if (!FileMgr.exists(activity, Constants.POINTS_FILE_NAME)) {
            return points;
        }
        String data = FileMgr.read(activity, Constants.POINTS_FILE_NAME);
        List<JSONObject> pointsJSON = Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.POINTS));
        for (JSONObject pointJSON : pointsJSON) {
            List<Integer> routes = Utils.convertJSONArrayToList(pointJSON.getJSONArray(Constants.ROUTES));
            List<String> choice = new ArrayList<>();
            if (!StringUtils.isBlank(pointJSON.getString(Constants.CHOICE))) {
                choice = Utils.convertJSONArrayToList(new JSONArray(pointJSON.getString(Constants.CHOICE)));
            }
            Point point = new Point(
                    pointJSON.getInt(Constants.ID),
                    pointJSON.getString(Constants.NAME),
                    pointJSON.getString(Constants.DESCRIPTION),
                    pointJSON.getString(Constants.STATE),
                    routes,
                    Utils.getString(pointJSON, Constants.BARCODE),
                    pointJSON.getInt(Constants.CATEGORY),
                    choice,
                    pointJSON.getString(Constants.DEFAULT_VALUE),
                    pointJSON.has("measure_unit") ? pointJSON.getString("measure_unit") : null,
                    pointJSON.has("point_code") ? pointJSON.getString("point_code") : null,
                    pointJSON.has("standard") ? pointJSON.getString("standard") : null);

            points.add(point);
        }
        return points;
    }

}
