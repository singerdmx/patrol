package com.mbrite.patrol.model;

public class RecordState {
    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETE;
    }

    public enum Result {
        FAIL,
        PASS;
    }

    public Status status;
    public Result result;

    public RecordState() {
    }

    public RecordState(Status status, Result result) {
        this.status = status;
        this.result = result;
    }
}
