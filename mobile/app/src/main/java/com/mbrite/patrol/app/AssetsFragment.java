package com.mbrite.patrol.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.graphics.drawable.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.AssetProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.widget.AssetAdapter;

import org.json.JSONException;

import java.util.*;

public class AssetsFragment extends ParentFragment {
    private static final String TAG = AssetsFragment.class.getSimpleName();

    private ArrayList<Asset> assetList = new ArrayList<Asset>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();

        try {
            this.assetList.addAll(AssetProvider.INSTANCE.getAssets(getActivity(), Tracker.INSTANCE.assetIds));
        } catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RecordProvider.INSTANCE.clearState();
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
                        try {
                            Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                            intent.putExtra(Constants.BARCODE, asset.barcode);
                            startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(
                                    getActivity(),
                                    String.format("Error: %s", ex.getLocalizedMessage()),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
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