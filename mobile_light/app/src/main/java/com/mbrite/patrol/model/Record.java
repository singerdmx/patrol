package com.mbrite.patrol.model;

import com.mbrite.patrol.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    final public String session;

    final public String user;
    final public String version;
    public List<Integer> routes;
    public List<PointRecord> points;
    public long start_time;
    public long end_time;
    private String submitter;

    public Record(String username) {
        session = UUID.randomUUID().toString();
        this.user = username;
        points = new ArrayList<PointRecord>();
        version = Constants.APP_VERSION;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

}
