package com.mbrite.patrol.model;

import com.mbrite.patrol.content.providers.RecordProvider;

import java.util.*;

/**
 * POJO class for a record to submit to server.
 */
public class Record {

    public String id;

    public int route;

    public List<AssetRecord> assets;

    public Record() {
        id = UUID.randomUUID().toString();
        route = -1;
        assets = new ArrayList<AssetRecord>();
    }

    /**
     * Add the specified asset to {@code assets} if it is not present.
     * @return true if the asset was added, else false
     */
    public boolean offer(int assetId) {
        for (AssetRecord ar : assets) {
            if (ar.id == assetId) {
                RecordProvider.INSTANCE.currentAssetRecord = ar;
                return false;
            }
        }

        AssetRecord newAssetRecord = new AssetRecord(assetId);
        RecordProvider.INSTANCE.currentAssetRecord = newAssetRecord;
        assets.add(newAssetRecord);
        return true;
    }
}
