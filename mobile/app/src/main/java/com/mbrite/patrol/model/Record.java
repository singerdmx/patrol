package com.mbrite.patrol.model;

import java.util.*;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    final public String session;

    final public String user;

    private String submitter;

    public List<Integer> routes;

    public List<PointRecord> points;

    public long start_time;

    public long end_time;

    public Record(String username) {
        session = UUID.randomUUID().toString();
        this.user = username;
        points = new ArrayList<PointRecord>();
    }
    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

}
