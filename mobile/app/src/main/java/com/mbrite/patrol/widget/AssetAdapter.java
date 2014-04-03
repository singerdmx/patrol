package com.mbrite.patrol.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;

public class AssetAdapter extends ArrayAdapter<Asset> {

    private final Activity context;
    private final ArrayList<Asset> itemsArrayList;

    public AssetAdapter(Activity context, ArrayList<Asset> itemsArrayList) {

        super(context, R.layout.activity_list_item_asset, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_asset, parent, false);
        Asset asset = itemsArrayList.get(position);
        try {
            RecordState state = RecordProvider.INSTANCE.getAssetRecordState(context, asset.id);
            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
            int resId = 0;
            switch (state.status) {
                case NOT_STARTED:
                    resId = R.drawable.progress_start;
                    break;
                case IN_PROGRESS:
                    resId = R.drawable.in_progress;
                    break;
                case COMPLETE:
                    resId = R.drawable.progress_complete;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status: " + state.status.toString());
            }
            if (state.result != null) {
                switch (state.result) {
                    case PASS:
                        rowView.setBackgroundResource(R.drawable.pass_row_selector);
                        break;
                    case FAIL:
                        rowView.setBackgroundResource(R.drawable.fail_row_selector);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid result: " + state.result.toString());
                }
            }
            icon.setImageResource(resId);
        } catch (Exception ex) {
            Toast.makeText(
                    context,
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
        descriptionView.setText(asset.description);
        TextView serialNumView = (TextView) rowView.findViewById(R.id.serial_num);
        serialNumView.setText(asset.serialNum);

        return rowView;
    }
}
