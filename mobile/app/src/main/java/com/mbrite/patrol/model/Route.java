package com.mbrite.patrol.model;

import java.util.*;

public class Route {
    public final int id;
    public final String description;
    public List<Integer> assets;
    private boolean selected;

    public Route(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public Route(int id, String description, List<Integer> assets) {
        this(id, description);
        this.assets = assets;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
