package com.mbrite.patrol.model;

import java.util.*;

public class AssetGroup extends Asset {

    private int routeId;

    public List<PointGroup> pointList;

    public AssetGroup(Asset asset, ArrayList<Point> allPoints, RouteGroup routeGroup) {
        super(asset.id, asset.description, asset.serialNum, asset.barcode, asset.points);
        routeId = routeGroup.id;
        pointList = new ArrayList<PointGroup>();

        Set<Integer> pointIndexes = new TreeSet<>(asset.points);
        for (Point point : allPoints) {
            if (pointIndexes.contains(point.id) && point.routes.indexOf(routeGroup.id) != -1) {
                pointList.add(new PointGroup(point, this));
            }
        }
    }

    public int getRouteId() {
        return routeId;
    }
}
