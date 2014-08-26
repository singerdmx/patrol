package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.model.Asset;

import java.util.ArrayList;

public class AssetAdapter extends ArrayAdapter<Asset> {

    private final Activity context;
    private final ArrayList<Asset> itemsArrayList;
    private LayoutInflater inflater;

    public AssetAdapter(Activity context, ArrayList<Asset> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_list_item_asset, null);
        }
        Asset asset = itemsArrayList.get(position);
        TextView descriptionView = (TextView) convertView.findViewById(R.id.description);
        descriptionView.setText(asset.name);

        TextView serialNumView = (TextView) convertView.findViewById(R.id.serial_num);
        serialNumView.setText(asset.serialNum);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        int resId = R.drawable.in_progress;
        icon.setImageResource(resId);
        convertView.setBackgroundResource(R.drawable.alterselector1);
        return convertView;
    }
}
