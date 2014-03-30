package com.mbrite.patrol.content.providers;

import android.app.*;

import com.google.gson.*;
import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.*;

import java.io.IOException;

public enum RecordProvider {

    INSTANCE;

    private Record record;

    private Gson gson = new Gson();

    /**
     * Keep track of current asset record being operated on
     */
    public AssetRecord currentAssetRecord;

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
        }

        return record;
    }

    public void save(Activity activity, Record record)
        throws IOException {
      this.record = record;
      FileMgr.write(activity, Constants.RECORD_FILE_NAME, gson.toJson(record));
    }
}
