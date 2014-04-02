package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

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
            JSONObject standard = new JSONObject(pointJSON.getString(Constants.STANDARD));
            Point point = new Point(
                    pointJSON.getInt(Constants.ID),
                    pointJSON.getString(Constants.DESCRIPTION),
                    pointJSON.getString(Constants.TPM_TYPE),
                    pointJSON.getString(Constants.STANDARD),
                    pointJSON.getString(Constants.STATUS),
                    pointJSON.getString(Constants.PERIOD_UNIT));

            points.add(point);
        }
        return points;
    }

    public ArrayList<Point> getPoints(Activity activity, int[] pointIds)
            throws JSONException, IOException {
        Set<Integer> pointIndexes = new HashSet<Integer>(pointIds.length);
        for (int pointId : pointIds) {
            if (!pointIndexes.contains(pointId)) {
                pointIndexes.add(pointId);
            }
        }

        ArrayList<Point> result = new ArrayList<Point>();
        for (Point point : getPoints(activity)) {
            if (pointIndexes.contains(point.id)) {
                result.add(point);
            }
        }
        return result;
    }

    public String getStandardDescription(JSONObject standardJSON)
            throws JSONException {
        return standardJSON.getString(Constants.DESCRIPTION);
    }

    public Integer getMin(JSONObject standardJSON)
            throws JSONException {
        if (standardJSON.has(Constants.MIN)) {
            return standardJSON.getInt(Constants.MIN);
        }
        return null;
    }

    public Integer getMax(JSONObject standardJSON)
            throws JSONException {
        if (standardJSON.has(Constants.MAX)) {
            return standardJSON.getInt(Constants.MAX);
        }
        return null;
    }

    public List<String> getChoice(JSONObject standardJSON)
        throws JSONException {
        if (standardJSON.has(Constants.CHOICE)) {
            JSONArray choiceArr = standardJSON.getJSONArray(Constants.CHOICE);
            List<String> choice = new ArrayList<String>(choiceArr.length());
            for (int i = 0; i < choiceArr.length(); i++) {
                choice.add(choiceArr.getString(i));
            }
            return choice;
        }
        return null;
    }

}
