package com.mbrite.patrol.model;

import java.util.*;

public class Route {
    public final int id;
    public final String name;
    public List<Integer> assets;
    private boolean selected;

    public Route(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Route(int id, String name, List<Integer> assets) {
        this(id, name);
        this.assets = assets;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
