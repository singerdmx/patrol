package com.mbrite.patrol.widget;

import java.util.*;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.zxing.integration.android.IntentIntegrator;
import com.mbrite.patrol.app.*;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;

public class AssetAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private Activity activity;
    private final List<RouteGroup> groups;

    public AssetAdapter(Activity context, List<RouteGroup> groups) {
        activity = context;
        this.groups = groups;
        inflater = context.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).assetList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Asset asset = (Asset) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_list_item_asset, null);
        }
        final TextView descriptionView = (TextView) convertView.findViewById(R.id.description);
        descriptionView.setText(asset.description);
        TextView serialNumView = (TextView) convertView.findViewById(R.id.serial_num);
        serialNumView.setText(asset.serialNum);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordProvider.INSTANCE.setCurrentRouteRecord(asset.routeId);
                Tracker.INSTANCE.targetBarcode = asset.barcode;
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.initiateScan();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).assetList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.route_group, null);
        }
        RouteGroup group = (RouteGroup) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.description);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        // Get rowView from inflater
//        View rowView = inflater.inflate(R.layout.activity_list_item_asset, parent, false);
//        Asset asset = itemsArrayList.get(position);
//        try {
//            RecordState state = RecordProvider.INSTANCE.getAssetRecordState(context, asset.id);
//            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
//            int resId = 0;
//            switch (state.status) {
//                case NOT_STARTED:
//                    resId = R.drawable.progress_start;
//                    break;
//                case IN_PROGRESS:
//                    resId = R.drawable.in_progress;
//                    break;
//                case COMPLETE:
//                    resId = R.drawable.progress_complete;
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid status: " + state.status.toString());
//            }
//            if (state.result != null) {
//                switch (state.result) {
//                    case PASS:
//                        rowView.setBackgroundResource(R.drawable.pass_row_selector);
//                        break;
//                    case FAIL:
//                        rowView.setBackgroundResource(R.drawable.fail_row_selector);
//                        break;
//                    default:
//                        throw new IllegalArgumentException("Invalid result: " + state.result.toString());
//                }
//            }
//            icon.setImageResource(resId);
//        } catch (Exception ex) {
//            Toast.makeText(
//                    context,
//                    String.format("Error: %s", ex.getLocalizedMessage()),
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
//        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
//        descriptionView.setText(asset.description);
//        TextView serialNumView = (TextView) rowView.findViewById(R.id.serial_num);
//        serialNumView.setText(asset.serialNum);
//
//        return rowView;
//    }
}
