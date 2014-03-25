package com.mbrite.patrol.content.providers;

import android.app.Activity;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.model.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class AssetProvider {
    private Activity activity;

    public AssetProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Asset> getAssets()
            throws JSONException, IOException {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        String data = FileMgr.read(activity, Constants.ASSETS_FILE_NAME);
        JSONArray assetsJSON = new JSONObject(data).getJSONArray(Constants.ASSETS);
        for(int i = 0 ; i < assetsJSON.length() ; i++) {
            JSONObject assetJSON = assetsJSON.getJSONObject(i);
            assets.add(
                    new Asset(
                            assetJSON.getInt(Constants.ID),
                            assetJSON.getString(Constants.DESCRIPTION),
                            assetJSON.getString(Constants.SERIAL_NUM)));
        }
        return assets;
    }
}
