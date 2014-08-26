package com.mbrite.patrol.app;

import android.os.Bundle;
import android.widget.Toast;

import com.mbrite.patrol.content.providers.AssetProvider;
import com.mbrite.patrol.model.Asset;
import com.mbrite.patrol.widget.AssetAdapter;

import java.util.ArrayList;

public class AssetsFragment extends ParentFragment {
    private static final String TAG = AssetsFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<Asset> assets = AssetProvider.INSTANCE.getAssets(getActivity());
            AssetAdapter adapter = new AssetAdapter(
                    getActivity(),
                    assets);
            setListAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
