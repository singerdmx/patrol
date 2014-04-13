package com.mbrite.patrol.model;

public class Asset {
    public final int id;
    public final String description;
    public final String serialNum;
    public final String barcode;
    public int[] points;
    public int routeId = -1;

    public Asset(int id, String description, String serialNum, String barcode) {
        this.id = id;
        this.description = description;
        this.serialNum = serialNum;
        this.barcode = barcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;

        Asset asset = (Asset) o;

        if (id != asset.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Asset(int id,
                 String description,
                 String serialNum,
                 String barcode,
                 int[] points) {
        this(id, description, serialNum, barcode);
        this.points = points;
    }

}
