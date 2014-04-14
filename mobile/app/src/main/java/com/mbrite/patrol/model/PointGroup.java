package com.mbrite.patrol.model;

import java.util.*;

public class PointGroup extends Point {

    public int assetId = -1;

    public Set<PointGroup> duplicates;

    public PointGroup(Point point, AssetGroup assetGroup) {
        super(point.id,
                point.description,
                point.tpmType,
                point.standard,
                point.status,
                point.periodUnit);
        assetId = assetGroup.id;
        duplicates = new TreeSet<PointGroup>();
    }
}
