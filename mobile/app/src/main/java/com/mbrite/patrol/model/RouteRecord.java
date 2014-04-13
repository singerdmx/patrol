package com.mbrite.patrol.model;

import java.util.ArrayList;
import java.util.List;

public class RouteRecord {
    public int id;

    public List<AssetRecord> assets;

    public String description;

    public RouteRecord(int id, String description) {
        this.id = id;
        this.description = description;
        assets = new ArrayList<AssetRecord>();
    }
}
