package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Utils;
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
        List<JSONObject> assetsJSON = Utils.convertJSONArrayToList(new JSONObject(data).getJSONArray(Constants.ASSETS));
        for(JSONObject assetJSON : assetsJSON) {
            List<Integer> points = Utils.convertJSONArrayToList(assetJSON.getJSONArray(Constants.POINTS));
            assets.add(
                    new Asset(
                            assetJSON.getInt(Constants.ID),
                            assetJSON.getString(Constants.NAME),
                            assetJSON.getString(Constants.SERIAL_NUM),
                            assetJSON.getString(Constants.BARCODE),
                            points));
        }
        return assets;
    }

    public ArrayList<Asset> filterAssets(Collection<Integer> assetIds, ArrayList<Asset> allAssets)
            throws JSONException, IOException {
        Set<Integer> assetIndexes = new HashSet<Integer>(assetIds.size());
        for (int assetId : assetIds) {
                assetIndexes.add(assetId);
        }

        ArrayList<Asset> result = new ArrayList<Asset>();
        for (Asset asset : allAssets) {
            if (assetIndexes.contains(asset.id)) {
                result.add(asset);
            }
        }
        return result;
    }
}
