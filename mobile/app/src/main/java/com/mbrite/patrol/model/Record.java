package com.mbrite.patrol.model;

import java.util.*;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    final public String session;

    final public String user;

    private int check_route_id;

    public List<AssetRecord> assets;

    public long start_time;

    public long end_time;

    public Record(String username, int routeId) {
        session = UUID.randomUUID().toString();
        this.user = username;
        check_route_id = routeId;
        assets = new ArrayList<AssetRecord>();
    }

    public int getRouteId() {
        return check_route_id;
    }
}
