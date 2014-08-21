package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.google.gson.Gson;
import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.model.PointRecord;
import com.mbrite.patrol.model.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     *
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
     *
     * @param activity
     * @return Record object
     * @throws java.io.IOException
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
        record.start_time = System.currentTimeMillis() / 1000;
        save(activity);
        return record;
    }

    private void save(Activity activity)
            throws IOException {
        FileMgr.write(activity, Constants.RECORD_FILE_NAME, toString(record));
    }

    public int[] getRoutes() {
        int[] routes = new int[record.routes.size()];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = record.routes.get(i);
        }
        return routes;
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

    /**
     * @param pointRecord
     * @param activity
     * @return true if added, false if updated
     * @throws java.io.IOException
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
