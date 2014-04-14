package com.mbrite.patrol.content.providers;

import android.app.*;

import com.google.gson.*;
import com.mbrite.patrol.common.*;
import com.mbrite.patrol.model.*;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public enum RecordProvider {

    INSTANCE;

    private Record record;

    private Gson gson = new Gson();

    /**
     * Keep track of current route record being operated on
     */
    public RouteRecord currentRouteRecord;

    /**
     * Keep track of current asset record being operated on
     */
    public AssetRecord currentAssetRecord;

    /**
     * Keep track of current point record being operated on
     */
    public PointRecord currentPointRecord;

    /**
     * ids of incomplete assets
     */
    private Set<Integer> incompleteAssets = new TreeSet<Integer>();

    /**
     * ids of fail assets
     */
    private Set<Integer> failPoints = new TreeSet<Integer>();

    public void reset(Activity activity)
        throws IOException {
        record = null;
        if (FileMgr.exists(activity, Constants.RECORD_FILE_NAME)) {
            FileMgr.delete(activity, Constants.RECORD_FILE_NAME);
        }
    }

    /**
     * Deserialize Record object from file
     * @param activity
     * @param fileName The name of the file that contains json content of record
     * @return Record object
     */
    public Record getRecord(Activity activity, String fileName)
        throws IOException {
        String recordContent = FileMgr.read(activity, fileName);
        return gson.fromJson(recordContent, Record.class);
    }

    /**
     * Get current record object
     * @param activity
     * @return Record object
     * @throws IOException
     */
    public Record get(Activity activity)
        throws IOException {
        if (record == null && FileMgr.exists(activity, Constants.RECORD_FILE_NAME)) {
            try {
                record = getRecord(activity, Constants.RECORD_FILE_NAME);
                return record;
            } catch (Exception ex) {
                reset(activity);
            }
        }

        return record;
    }

    public Record create(Activity activity)
        throws IOException {
        record = new Record(Utils.getSavedUsernameAndPassword(activity)[0]);
        record.start_time =  System.currentTimeMillis()/1000;
        save(activity);
        return record;
    }

    private void save(Activity activity)
        throws IOException {
        FileMgr.write(activity, Constants.RECORD_FILE_NAME, gson.toJson(record));
    }

    public void save(Activity activity, Record record)
            throws IOException {
        this.record = record;
        save(activity);
    }

    public void setRoutes(List<Route> routes, Activity activity)
        throws IOException {
        record.routes = new ArrayList<RouteRecord>(routes.size());
        for (Route route : routes) {
            record.routes.add(new RouteRecord(route.id));
        }
        save(activity);
    }

    public void setCurrentRouteRecord(int routeId) {
        for (RouteRecord routeRecord : record.routes) {
            if (routeRecord.id == routeId) {
                currentRouteRecord = routeRecord;
            }
        }

        throw new IllegalArgumentException(String.format("Invalid route ID: %s", routeId));
    }

    /**
     * Add the specified asset to {@code assets} if it is not present.
     * @return true if the asset was added, else false
     */
    public boolean offerAsset(Activity activity, int assetId)
            throws IOException {
        for (AssetRecord ar : currentRouteRecord.assets) {
            if (ar.id == assetId) {
                currentAssetRecord = ar;
                return false;
            }
        }

        AssetRecord newAssetRecord = new AssetRecord(assetId);
        currentAssetRecord = newAssetRecord;
        currentRouteRecord.assets.add(newAssetRecord);
        save(activity, record);
        return true;
    }

    /**
     * Add the specified point to {@code points} if it is not present.
     * @return true if the point was added, else false
     */
    public boolean offerPoint(Activity activity, int pointId)
            throws IOException {
        for (PointRecord pr : currentAssetRecord.points) {
            if (pr.id == pointId) {
                currentPointRecord = pr;
                return false;
            }
        }

        PointRecord newPointRecord = new PointRecord(pointId);
        currentPointRecord = newPointRecord;
        currentAssetRecord.points.add(newPointRecord);
        save(activity);
        return true;
    }

    public Record setCurrentPointRecord(Activity activity, String value, int result)
        throws IOException {
        currentPointRecord.value = value;
        currentPointRecord.result = result;
        currentPointRecord.check_time = System.currentTimeMillis()/1000;
        save(activity);
        return record;
    }

    public RecordState getAssetRecordState(Activity activity, int assetId)
        throws JSONException, IOException {
        RecordState state = new RecordState();
        Asset asset = Tracker.INSTANCE.getAsset(assetId);
        AssetRecord assetRecord = null;
        for (AssetRecord ar : currentRouteRecord.assets) {
            if (ar.id == assetId) {
                assetRecord = ar;
                break;
            }
        }

        if (assetRecord == null || assetRecord.points.isEmpty()) {
            state.status = RecordState.Status.NOT_STARTED;
            incompleteAssets.add(assetId);
            return state;
        }

        state.result = RecordState.Result.PASS;
        state.status = RecordState.Status.COMPLETE;
        if (assetRecord.points.size() < asset.points.length) {
            state.status = RecordState.Status.IN_PROGRESS;
        }
        for(PointRecord pr : assetRecord.points) {
            if (pr.result == -1) {
                state.status = RecordState.Status.IN_PROGRESS;
                incompleteAssets.add(assetId);
            } else if (pr.result == 1) {
                state.result = RecordState.Result.FAIL;
                failPoints.add(pr.id);
            }
        }
        return state;
    }

    /**
     * Clear statistics of incomplete assets and failed points
     * In method getAssetRecordState, we will recalculate
     */
    public void clearState() {
        incompleteAssets = new TreeSet<Integer>();
        failPoints = new TreeSet<Integer>();
    }

    public boolean isComplete() {
        return incompleteAssets.isEmpty();
    }

    public List<String> getRecordFiles(Activity activity) {
        List<String> recordFiles = new ArrayList<String>();
        for (String file : FileMgr.fileList(activity)) {
            if (file.startsWith(Constants.RECORD_FILE_NAME)) {
                recordFiles.add(file);
            }
        }
        return recordFiles;
    }
}
