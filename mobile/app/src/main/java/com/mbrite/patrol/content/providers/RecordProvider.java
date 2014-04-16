package com.mbrite.patrol.content.providers;

import android.app.*;

import com.google.gson.*;
import com.mbrite.patrol.common.*;
import com.mbrite.patrol.model.*;

import java.io.IOException;
import java.util.*;

public enum RecordProvider {

    INSTANCE;

    private Record record;

    private Gson gson = new Gson();

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

    public Record create(Activity activity, String userName)
        throws IOException {
        record = new Record(userName);
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
        record.routes = new ArrayList<Integer>(routes.size());
        for (Route route : routes) {
            record.routes.add(route.id);
        }
        save(activity);
    }

//
//    public void setCurrentRouteRecord(int routeId) {
//        for (RouteRecord routeRecord : record.routes) {
//            if (routeRecord.id == routeId) {
//                currentRouteRecord = routeRecord;
//                return;
//            }
//        }
//
//        throw new IllegalArgumentException(String.format("Invalid route ID: %s", routeId));
//    }

    /**
     * Add the specified asset to {@code assets} if it is not present.
     * @return true if the asset was added, else false
     */
//    public boolean offerAsset(Activity activity, int assetId)
//            throws IOException {
//        for (AssetRecord ar : currentRouteRecord.assets) {
//            if (ar.id == assetId) {
//                currentAssetRecord = ar;
//                return false;
//            }
//        }
//
//        AssetRecord newAssetRecord = new AssetRecord(assetId);
//        currentAssetRecord = newAssetRecord;
//        currentRouteRecord.assets.add(newAssetRecord);
//        save(activity, record);
//        return true;
//    }

//    /**
//     * Add the specified point to {@code points} if it is not present.
//     * @return true if the point was added, else false
//     */
//    public boolean offerPoint(Activity activity, int pointId)
//            throws IOException {
//        for (PointRecord pr : currentAssetRecord.points) {
//            if (pr.id == pointId) {
//                currentPointRecord = pr;
//                return false;
//            }
//        }
//
//        PointRecord newPointRecord = new PointRecord(pointId);
//        currentPointRecord = newPointRecord;
//        currentAssetRecord.points.add(newPointRecord);
//        save(activity);
//        return true;
//    }

//    public Record setCurrentPointRecord(Activity activity, String value, int result)
//        throws IOException {
//        currentPointRecord.value = value;
//        currentPointRecord.result = result;
//        currentPointRecord.check_time = System.currentTimeMillis()/1000;
//        save(activity);
//        return record;
//    }

//    public RecordState getAssetRecordState(Activity activity, int assetId)
//        throws JSONException, IOException {
//        RecordState state = new RecordState();
//        Asset asset = Tracker.INSTANCE.getAsset(assetId);
//        AssetRecord assetRecord = null;
//        for (AssetRecord ar : currentRouteRecord.assets) {
//            if (ar.id == assetId) {
//                assetRecord = ar;
//                break;
//            }
//        }
//
//        if (assetRecord == null || assetRecord.points.isEmpty()) {
//            state.status = RecordState.Status.NOT_STARTED;
//            incompleteAssets.add(assetId);
//            return state;
//        }
//
//        state.result = RecordState.Result.PASS;
//        state.status = RecordState.Status.COMPLETE;
//        if (assetRecord.points.size() < asset.points.size()) {
//            state.status = RecordState.Status.IN_PROGRESS;
//        }
//        for(PointRecord pr : assetRecord.points) {
//            if (pr.result == -1) {
//                state.status = RecordState.Status.IN_PROGRESS;
//                incompleteAssets.add(assetId);
//            } else if (pr.result == 1) {
//                state.result = RecordState.Result.FAIL;
//                failPoints.add(pr.id);
//            }
//        }
//        return state;
//    }

    public List<String> getRecordFiles(Activity activity) {
        List<String> recordFiles = new ArrayList<String>();
        for (String file : FileMgr.fileList(activity)) {
            if (file.startsWith(Constants.RECORD_FILE_NAME)) {
                recordFiles.add(file);
            }
        }
        return recordFiles;
    }

    /**
     * @param pointGroup
     * @param value
     * @param result
     * @param activity
     * @return true if added, false if updated
     * @throws IOException
     */
    public boolean addOrUpdatePointRecord(PointGroup pointGroup, String value, int result, Activity activity)
            throws IOException {
        return addOrUpdatePointRecord(convertToPointRecord(pointGroup, value, result), activity);
    }

    public PointRecord getPointRecord(int id) {
        for (PointRecord pointRecord : record.points) {
            if (pointRecord.id == id) {
                return pointRecord;
            }
        }

        return null;
    }

    private PointRecord convertToPointRecord(PointGroup pointGroup, String value, int result) {
        Set<Integer> routes = new TreeSet<>();
        for (PointGroup p : Tracker.INSTANCE.getPointDuplicates().get(pointGroup.id)) {
            routes.add(p.getRouteId());
        }
        return new PointRecord(value, result, pointGroup.id, routes, pointGroup.getAssetId());
    }

    /**
     * @param pointRecord
     * @param activity
     * @return true if added, false if updated
     * @throws IOException
     */
    private boolean addOrUpdatePointRecord(PointRecord pointRecord, Activity activity)
        throws IOException {
        for (int i = 0; i < record.points.size(); i++) {
            if (record.points.get(i).id == pointRecord.id) {
                // update
                record.points.set(i, pointRecord);
                save(activity);
                return false;
            }
        }

        record.points.add(pointRecord);
        save(activity);
        return true;
    }
}
