package com.mbrite.patrol.model;

import java.util.*;

public class Point {
    public final int id;
    public final String description;
    public final String tpmType;
    public final String status;
    public final String periodUnit;
    public final String standard;
    public final List<Integer> routes;
    public final String barcode;

    public Point(int id,
                 String description,
                 String tpmType,
                 String standard,
                 String status,
                 String periodUnit,
                 List<Integer> routes,
                 String barcode) {
        this.id = id;
        this.description = description;
        this.tpmType = tpmType;
        this.standard = standard;
        this.status = status;
        this.periodUnit = periodUnit;
        this.routes = routes;
        this.barcode = barcode;
    }
}
