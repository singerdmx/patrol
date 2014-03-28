package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.model.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public enum PointProvider {

    INSTANCE;

    private ArrayList<Point> points;

    public ArrayList<Point> getPoints(Activity activity)
            throws JSONException, IOException {
        if (points != null) {
            return points;
        }

        points = new ArrayList<Point>();
        String data = FileMgr.read(activity, Constants.POINTS_FILE_NAME);
        JSONArray pointsJSON = new JSONObject(data).getJSONArray(Constants.POINTS);
        for(int i = 0 ; i < pointsJSON.length() ; i++) {
            JSONObject pointJSON = pointsJSON.getJSONObject(i);
            points.add(
                    new Point(
                            pointJSON.getInt(Constants.ID),
                            pointJSON.getString(Constants.DESCRIPTION)));
        }
        return points;
    }

}
