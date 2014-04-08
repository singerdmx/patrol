package com.mbrite.patrol.model;

import java.util.*;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    final public String session;

    final public String user;

    public int check_route_id;

    public List<AssetRecord> assets;

    public long start_time;

    public long end_time;

    public Record(String username) {
        session = UUID.randomUUID().toString();
        this.user = username;
        check_route_id = -1;
        assets = new ArrayList<AssetRecord>();
    }
}
