package com.mbrite.patrol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AssetGroup extends Asset {

    public List<PointGroup> pointList;
    private int routeId;

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
        int notStarted = 0, fail = 0, warn = 0;
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
        return (double) getStarted() / getTotal();
    }

    public int getStarted() {
        int started = 0;
        for (PointGroup p : pointList) {
            if (p.getStatus() != RecordStatus.NOT_STARTED) {
                started++;
            }
        }

        return started;
    }

    public int getTotal() {
        return pointList.size();
    }
}
