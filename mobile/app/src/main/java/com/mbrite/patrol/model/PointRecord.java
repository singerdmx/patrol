package com.mbrite.patrol.model;

import java.util.*;

public class PointRecord {
    final public int id;
    public String value;
    public int result;
    public long check_time;
    public Set<Integer> routes;
    public int asset_id;

    public PointRecord(String value, int result, int id, Set<Integer> routes, int asset_id) {
        this.value = value;
        this.result = result;
        this.id = id;
        this.check_time = System.currentTimeMillis()/1000;
        this.routes = routes;
        this.asset_id = asset_id;
    }
}
