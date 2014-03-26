package com.mbrite.patrol.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.model.Asset;

public class AssetAdapter extends ArrayAdapter<Asset> {

    private final Context context;
    private final ArrayList<Asset> itemsArrayList;

    public AssetAdapter(Context context, ArrayList<Asset> itemsArrayList) {

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

        if (position % 2 == 0){
            rowView.setBackgroundResource(R.drawable.alterselector1);
        } else {
            rowView.setBackgroundResource(R.drawable.alterselector2);
        }

        Asset asset = itemsArrayList.get(position);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
        descriptionView.setText(asset.description);
        TextView serialNumView = (TextView) rowView.findViewById(R.id.serial_num);
        serialNumView.setText(asset.serialNum);

        return rowView;
    }
}
