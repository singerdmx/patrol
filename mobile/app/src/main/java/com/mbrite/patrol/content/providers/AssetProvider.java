package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Asset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public enum AssetProvider {

    INSTANCE;

    private ArrayList<Asset> assets;

    public ArrayList<Asset> getAssets(Activity activity)
            throws JSONException, IOException {
        if (assets != null) {
            return assets;
        }

        assets = new ArrayList<Asset>();
        String data = FileMgr.read(activity, Constants.ASSETS_FILE_NAME);
        JSONArray assetsJSON = new JSONObject(data).getJSONArray(Constants.ASSETS);
        for(int i = 0 ; i < assetsJSON.length() ; i++) {
            JSONObject assetJSON = assetsJSON.getJSONObject(i);
            JSONArray points = assetJSON.getJSONArray(Constants.POINTS);
            int[] pointIndexes = new int[points.length()];
            for (int j = 0; j < pointIndexes.length; j++) {
                pointIndexes[j] = points.getInt(j);
            }
            assets.add(
                    new Asset(
                            assetJSON.getInt(Constants.ID),
                            assetJSON.getString(Constants.DESCRIPTION),
                            assetJSON.getString(Constants.SERIAL_NUM),
                            assetJSON.getString(Constants.BARCODE),
                            pointIndexes));
        }
        return assets;
    }

    public ArrayList<Asset> getAssets(Activity activity, int[] assetIds)
            throws JSONException, IOException {
        Set<Integer> assetIndexes = new HashSet<Integer>(assetIds.length);
        for (int assetId : assetIds) {
            if (!assetIndexes.contains(assetId)) {
                assetIndexes.add(assetId);
            }
        }

        ArrayList<Asset> result = new ArrayList<Asset>();
        for (Asset asset : getAssets(activity)) {
            if (assetIndexes.contains(asset.id)) {
                result.add(asset);
            }
        }
        return result;
    }

    public Asset getAsset(Activity activity, String barcode, int[] assetIds)
            throws JSONException, IOException {
        for (Asset asset : getAssets(activity)) {
            if (asset.barcode.equals(barcode)) {
                for (int assetId : assetIds) {
                    if (assetId == asset.id) {
                        return asset;
                    }
                }
                return null;
            }
        }

        return null;
    }
}
