package com.mbrite.patrol.model;

import java.util.ArrayList;
import java.util.List;

public class RouteRecord {
    public int id;

    public List<AssetRecord> assets;

    public RouteRecord(int id) {
        this.id = id;
        assets = new ArrayList<AssetRecord>();
    }
}
