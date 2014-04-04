package com.mbrite.patrol.common;

/**
 * In memory tracker of navigation status
 */
public enum  Tracker {

    INSTANCE;

    /**
     * Keep track of current asset Ids to be shown on AssetsActivity
     */
    public int[] assetIds;

}
