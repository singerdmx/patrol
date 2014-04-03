package com.mbrite.patrol.model;

import java.util.*;

public class AssetRecord {
    public int id;

    public List<PointRecord> points;

    public AssetRecord(int id) {
        this.id = id;
        points = new ArrayList<PointRecord>();
    }

}
