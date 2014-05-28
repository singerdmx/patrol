package com.mbrite.patrol.model;

public class Notification {

    private String content;
    private boolean old;

    public Notification(String content, boolean old) {
        this.content = content;
        this.old = old;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOld() {
        return old;
    }

    public void setOld(boolean old) {
        this.old = old;
    }
}
