package com.mbrite.patrol.model;

public class PointRecord {
    final public int id;
    public String value;
    public int result = -1;
    public long check_time;

    public PointRecord(int id) {
       this.id = id;
    }
}
