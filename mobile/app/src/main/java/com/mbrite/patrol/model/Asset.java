package com.mbrite.patrol.model;

import java.util.List;

public class Asset {
    public final int id;
    public final String name;
    public final String serialNum;
    public final String barcode;
    public List<Integer> points;

    public Asset(int id, String name, String serialNum, String barcode) {
        this.id = id;
        this.name = name;
        this.serialNum = serialNum;
        this.barcode = barcode;
    }

    public Asset(int id,
                 String name,
                 String serialNum,
                 String barcode,
                 List<Integer> points) {
        this(id, name, serialNum, barcode);
        this.points = points;
    }

}
