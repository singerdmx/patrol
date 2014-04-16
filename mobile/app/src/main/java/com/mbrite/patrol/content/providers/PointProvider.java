package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.Point;

import org.json.JSONException;
import org.json.JSONObject;

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
            Point point = new Point(
                    pointJSON.getInt(Constants.ID),
                    pointJSON.getString(Constants.DESCRIPTION),
                    pointJSON.getString(Constants.TPM_TYPE),
                    pointJSON.getString(Constants.STANDARD),
                    pointJSON.getString(Constants.STATUS),
                    pointJSON.getString(Constants.PERIOD_UNIT),
                    routes,
                    Utils.getString(pointJSON, Constants.BARCODE));

            points.add(point);
        }
        return points;
    }

//    public String getStandardDescription(JSONObject standardJSON)
//            throws JSONException {
//        return standardJSON.getString(Constants.DESCRIPTION);
//    }
//
//    public Double getMin(JSONObject standardJSON)
//            throws JSONException {
//        if (standardJSON.has(Constants.MIN)) {
//            return standardJSON.getDouble(Constants.MIN);
//        }
//        return null;
//    }
//
//    public Double getMax(JSONObject standardJSON)
//            throws JSONException {
//        if (standardJSON.has(Constants.MAX)) {
//            return standardJSON.getDouble(Constants.MAX);
//        }
//        return null;
//    }
//
//    public List<String> getChoice(JSONObject standardJSON)
//        throws JSONException {
//        if (standardJSON.has(Constants.CHOICE)) {
//            return Utils.convertJSONArrayToList(standardJSON.getJSONArray(Constants.CHOICE));
//        }
//        return null;
//    }

}
