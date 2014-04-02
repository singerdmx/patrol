package com.mbrite.patrol.model;

import com.mbrite.patrol.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Point {
    public final int id;
    public final String description;
    public final String tpmType;
    public final String status;
    public final String periodUnit;
    public final String standard;

    public Point(int id,
                 String description,
                 String tpmType,
                 String standard,
                 String status,
                 String periodUnit) {
        this.id = id;
        this.description = description;
        this.tpmType = tpmType;
        this.standard = standard;
        this.status = status;
        this.periodUnit = periodUnit;
    }
}
