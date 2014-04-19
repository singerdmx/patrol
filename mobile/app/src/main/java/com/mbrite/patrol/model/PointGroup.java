package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.RecordProvider;

public class PointGroup extends Point {

    private int routeId;

    private int assetId;

    public PointGroup(Point point, AssetGroup assetGroup) {
        super(point.id,
                point.name,
                point.description,
                point.state,
                point.routes,
                point.barcode,
                point.category,
                point.choice);
        routeId = assetGroup.getRouteId();
        assetId = assetGroup.id;
    }

    public int getRouteId() {
        return routeId;
    }

    public int getAssetId() {
        return assetId;
    }

    public int getStatus() {
        PointRecord pr = RecordProvider.INSTANCE.getPointRecord(id);
        if (pr == null) {
            return RecordStatus.NOT_STARTED;
        }

        return pr.status;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, description);
    }
}
