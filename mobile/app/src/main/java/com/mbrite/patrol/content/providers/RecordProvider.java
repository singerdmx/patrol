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
        if (FileMgr.exists(activity, Constants.RECORD_FILE_NAME)) {
            FileMgr.delete(activity, Constants.RECORD_FILE_NAME);
        }
        record = null;
        Tracker.INSTANCE.reset();
    }

    public Record parseRecordString(String recordContent) {
        return gson.fromJson(recordContent, Record.class);
    }

    public String toString(Record record) {
        return gson.toJson(record);
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
        return parseRecordString(recordContent);
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
        FileMgr.write(activity, Constants.RECORD_FILE_NAME, toString(record));
    }

    public void setRoutes(List<Route> routes, Activity activity)
        throws IOException {
        record.routes = new ArrayList<Integer>(routes.size());
        for (Route route : routes) {
            record.routes.add(route.id);
        }
        save(activity);
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

    public void completeCurrentRecord(Activity activity)
        throws IOException {
        if (record != null && record.end_time == 0) {
            record.end_time = System.currentTimeMillis() / 1000;
            save(activity);
        }
        if (FileMgr.exists(activity, Constants.RECORD_FILE_NAME)) {
            FileMgr.copy(activity,
                    Constants.RECORD_FILE_NAME,
                    String.format("%s.%d", Constants.RECORD_FILE_NAME, System.currentTimeMillis() / 1000));
        }
        reset(activity);
    }

    /**
     * @param pointGroup
     * @param value
     * @param result
     * @param activity
     * @return true if added, false if updated
     * @throws IOException
     */
    public boolean addOrUpdatePointRecord(PointGroup pointGroup, String result, int status, String memo, Activity activity)
            throws IOException {
        return addOrUpdatePointRecord(convertToPointRecord(pointGroup, result, status, memo), activity);
    }

    public PointRecord getPointRecord(int id) {
        if (record == null) {
            return null;
        }
        for (PointRecord pointRecord : record.points) {
            if (pointRecord.id == id) {
                return pointRecord;
            }
        }

        return null;
    }

    private PointRecord convertToPointRecord(PointGroup pointGroup, String result, int status, String memo) {
        Set<Integer> routes = new TreeSet<>();
        for (PointGroup p : Tracker.INSTANCE.getPointDuplicates().get(pointGroup.id)) {
            routes.add(p.getRouteId());
        }
        return new PointRecord(result, status, memo, pointGroup.id, routes, pointGroup.getAssetId());
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
