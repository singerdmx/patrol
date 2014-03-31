package com.mbrite.patrol.model;

public class Point {
    public final int id;
    public final String description;
    public final String tpmType;

    public Point(int id, String description, String tpmType) {
        this.id = id;
        this.description = description;
        this.tpmType = tpmType;
    }
}
