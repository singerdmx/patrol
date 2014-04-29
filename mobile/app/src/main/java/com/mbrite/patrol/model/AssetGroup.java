package com.mbrite.patrol.model;

import java.util.*;

public class AssetGroup extends Asset {

    private int routeId;

    public List<PointGroup> pointList;

    public AssetGroup(Asset asset, ArrayList<Point> allPoints, RouteGroup routeGroup) {
        super(asset.id, asset.name, asset.serialNum, asset.barcode, asset.points);
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

    public int getStatus() {
        int notStarted = 0, fail  = 0, warn = 0;
        for (PointGroup p : pointList) {
            switch (p.getStatus()) {
                case RecordStatus.NOT_STARTED:
                    notStarted++;
                    break;
                case RecordStatus.FAIL:
                    fail++;
                    break;
                case RecordStatus.WARN:
                    warn++;
                    break;
            }
        }

        if (notStarted == pointList.size()) {
            return RecordStatus.NOT_STARTED;
        }
        if (fail > 0) {
            return RecordStatus.FAIL;
        }
        if (warn > 0) {
            return RecordStatus.WARN;
        }
        return RecordStatus.PASS;
    }

    public double getCompleteness() {
        double started = 0;
        for (PointGroup p : pointList) {
            if (p.getStatus() != RecordStatus.NOT_STARTED) {
                started++;
            }
        }

        return started / pointList.size();
    }
}
