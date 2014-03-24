package com.mbrite.patrol.model;

public class Route {
    public final int id;
    public final String description;
    public int[] assets;

    public Route(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public Route(int id, String description, int[] assets) {
        this(id, description);
        this.assets = assets;
    }
}
