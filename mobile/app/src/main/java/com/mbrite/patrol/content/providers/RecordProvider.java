package com.mbrite.patrol.content.providers;

import android.app.*;

import com.google.gson.*;
import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.*;

import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public enum RecordProvider {

    INSTANCE;

    private Record record;

    private Gson gson = new Gson();

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

    public Record get(Activity activity)
        throws IOException {
        if (record == null) {
            if (FileMgr.exists(activity, Constants.RECORD_FILE_NAME)) {
                String recordContent = FileMgr.read(activity, Constants.RECORD_FILE_NAME);
                try {
                    record = gson.fromJson(recordContent, Record.class);
                    return record;
                } catch (JsonSyntaxException ex) {
                    reset(activity);
                }
            }

            record = new Record();
            record.startTime =  System.currentTimeMillis()/1000;
        }

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

    /**
     * Add the specified asset to {@code assets} if it is not present.
     * @return true if the asset was added, else false
     */
    public boolean offerAsset(Activity activity, int assetId)
            throws IOException {
        for (AssetRecord ar : record.assets) {
            if (ar.id == assetId) {
                currentAssetRecord = ar;
                return false;
            }
        }

        AssetRecord newAssetRecord = new AssetRecord(assetId);
        currentAssetRecord = newAssetRecord;
        record.assets.add(newAssetRecord);
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
        currentPointRecord.updateTime = System.currentTimeMillis()/1000;
        save(activity);
        return record;
    }

    public RecordState getAssetRecordState(Activity activity, int assetId)
        throws JSONException, IOException {
        RecordState state = new RecordState();
        Asset asset = AssetProvider.INSTANCE.getAsset(activity, assetId);
        AssetRecord assetRecord = null;
        for (AssetRecord ar : record.assets) {
            if (ar.id == assetId) {
                assetRecord = ar;
                break;
            }
        }

        if (assetRecord == null) {
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
