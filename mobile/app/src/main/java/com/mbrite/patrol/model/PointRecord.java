package com.mbrite.patrol.model;

import java.util.Set;

public class PointRecord {
    final public int id;
    public String result;
    public int status;
    public long check_time;
    public Set<Integer> routes;
    public int asset_id;
    public String memo;
    public String image;

    public PointRecord(String result,
                       int status,
                       String memo,
                       int id,
                       Set<Integer> routes,
                       int asset_id,
                       String image) {
        this.result = result;
        this.status = status;
        this.memo = memo;
        this.id = id;
        this.check_time = System.currentTimeMillis() / 1000;
        this.routes = routes;
        this.asset_id = asset_id;
        this.image = image;
    }
}
