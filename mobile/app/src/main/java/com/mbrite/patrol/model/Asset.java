package com.mbrite.patrol.model;

public class Asset {
    public final int id;
    public final String description;
    public final String serialNum;

    public Asset(int id, String description, String serialNum) {
        this.id = id;
        this.description = description;
        this.serialNum = serialNum;
    }
}
