package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.RecordProvider;

import java.util.*;

public class AssetRecord {
    public int id;

    public List<PointRecord> points;

    public AssetRecord(int id) {
        this.id = id;
        points = new ArrayList<PointRecord>();
    }
}