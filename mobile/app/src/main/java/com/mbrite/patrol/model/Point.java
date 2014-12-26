package com.mbrite.patrol.model;

import java.util.List;

public class Point {
    public final int id;
    public final String name;
    public final String description;
    public final String state;
    public final List<Integer> routes;
    public final String barcode;
    public final int category;
    public final List<String> choice;
    public final String defaultValue;
    public final String measureUnit;
    public final String pointCode;

    public Point(int id,
                 String name,
                 String description,
                 String state,
                 List<Integer> routes,
                 String barcode,
                 int category,
                 List<String> choice,
                 String defaultValue,
                 String measureUnit,
                 String pointCode) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.state = state;
        this.routes = routes;
        this.barcode = barcode;
        this.category = category;
        this.choice = choice;
        this.defaultValue = defaultValue;
        this.measureUnit = measureUnit;
        this.pointCode = pointCode;
    }

    public String getMeasureUnit() {
        return measureUnit == null ? "" : String.format(" %s", measureUnit);
    }
}
