package com.mbrite.patrol.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class AssetGroup extends Asset {

    public int routeId = -1;

    public List<Point> pointList;

    public AssetGroup(Asset asset, RouteGroup routeGroup) {
        super(asset.id, asset.description, asset.serialNum, asset.barcode, asset.points);
        routeId = routeGroup.id;
        // TODO: filter points based on route
        pointList = new ArrayList<Point>();
    }
}
