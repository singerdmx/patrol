package com.mbrite.patrol.model;

import java.util.*;

public class PointGroup extends Point {

    private int routeId;

    private int assetId;

    public Set<PointGroup> duplicates;

    public PointGroup(Point point, AssetGroup assetGroup) {
        super(point.id,
                point.description,
                point.tpmType,
                point.standard,
                point.status,
                point.periodUnit,
                point.routes,
                point.barcode);
        routeId = assetGroup.getRouteId();
        assetId = assetGroup.id;
        duplicates = new TreeSet<PointGroup>();
    }

    public int getRouteId() {
        return routeId;
    }

    public int getAssetId() {
        return assetId;
    }
}
