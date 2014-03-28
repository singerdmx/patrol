package com.mbrite.patrol.model;

public class Asset {
    public final int id;
    public final String description;
    public final String serialNum;
    public final String barcode;
    public int[] points;

    public Asset(int id, String description, String serialNum, String barcode) {
        this.id = id;
        this.description = description;
        this.serialNum = serialNum;
        this.barcode = barcode;
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
