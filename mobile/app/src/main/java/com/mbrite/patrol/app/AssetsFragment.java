package com.mbrite.patrol.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.content.providers.AssetProvider;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.widget.AssetAdapter;

import org.json.JSONException;

import java.util.*;

public class AssetsFragment extends ListFragment {
    private static final String TAG = AssetsFragment.class.getSimpleName();

    private AssetProvider assetProvider;
    private int[] assets;
    private ArrayList<Asset> assetList = new ArrayList<Asset>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        assets = extras.getIntArray(Constants.ASSETS);
        Set<Integer> assetIndexes = new HashSet<Integer>(assets.length);
        for (int asset : assets) {
            assetIndexes.add(asset);
        }

        try {
            this.assetProvider = new AssetProvider(getActivity());
            ArrayList<Asset> allAssets = assetProvider.getAssets();
            for (Asset asset : allAssets) {
                if (assetIndexes.contains(asset.id)) {
                    this.assetList.add(asset);
                }
            }
        } catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        AssetAdapter adapter = new AssetAdapter(
                getActivity(),
                this.assetList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Asset asset = (Asset) getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.selected_asset), asset.description))
                .setTitle(R.string.confirm_asset)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                        intent.putExtra(Constants.ASSETS, assets);
                        intent.putExtra(Constants.BARCODE, asset.barcode);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}