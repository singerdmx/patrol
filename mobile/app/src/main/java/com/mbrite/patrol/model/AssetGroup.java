package com.mbrite.patrol.model;

import java.util.ArrayList;
import java.util.List;

public class AssetGroup extends Asset {

    public int routeId = -1;

    public List<PointGroup> pointList;

    public AssetGroup(Asset asset, RouteGroup routeGroup) {
        super(asset.id, asset.description, asset.serialNum, asset.barcode, asset.points);
        routeId = routeGroup.id;
        // TODO: filter points based on route
        pointList = new ArrayList<PointGroup>();
    }
}
