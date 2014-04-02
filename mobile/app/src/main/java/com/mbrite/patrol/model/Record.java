package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.RecordProvider;

import java.util.*;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    public String id;

    public int route;

    public List<AssetRecord> assets;

    public long startTime;

    public long endTime;

    public Record() {
        id = UUID.randomUUID().toString();
        route = -1;
        assets = new ArrayList<AssetRecord>();
    }
}
