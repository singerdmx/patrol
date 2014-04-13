package com.mbrite.patrol.common;

import com.mbrite.patrol.model.RouteGroup;

import java.util.*;

/**
 * In memory tracker of navigation status
 */
public enum  Tracker {

    INSTANCE;

    /**
     * Keep track of current route groups to be shown on AssetsActivity
     */
    public List<RouteGroup> routeGroups;

    /**
     * Keep track of current asset ids to be shown on AssetsActivity
     */
    public int[] assetIds;

    public String targetBarcode;

}
