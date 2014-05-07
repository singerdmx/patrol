package com.mbrite.patrol.widget;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.*;

import org.apache.commons.lang3.StringUtils;

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
        final AssetGroup asset = (AssetGroup) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_list_item_asset, null);
        }
        final TextView descriptionView = (TextView) convertView.findViewById(R.id.description);
        descriptionView.setText(asset.name);
        TextView serialNumView = (TextView) convertView.findViewById(R.id.serial_num);
        serialNumView.setText(asset.serialNum);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        int resId;
        double completeness = asset.getCompleteness();
        if (Utils.areEqualDouble(completeness, 0)) {
            resId = R.drawable.progress_start;
        } else if (Utils.areEqualDouble(completeness, 1)) {
            resId = R.drawable.progress_complete;
        }  else {
            resId = R.drawable.in_progress;
        }
        icon.setImageResource(resId);
        int backgroundResId = 0;
        switch (asset.getStatus()) {
            case RecordStatus.PASS:
                backgroundResId = R.drawable.pass_row_selector;
                break;
            case RecordStatus.FAIL:
                backgroundResId = R.drawable.fail_row_selector;
                break;
            case RecordStatus.WARN:
                backgroundResId = R.drawable.warning_row_selector;
                break;
            case RecordStatus.NOT_STARTED:
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + asset.getStatus());
        }
        convertView.setBackgroundResource(backgroundResId);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Tracker.INSTANCE.targetAsset = asset;
                    if (asset != null && StringUtils.isBlank(asset.barcode)) {
                        // asset does not have barcode, special treatment
                        if (Utils.isScanOnly(null, asset.id, asset, activity)) {
                            if (Utils.getContinuousScanMode(activity)) {
                                Intent intent = new Intent(activity, ScanOnlyPointActivity.class);
                                activity.startActivity(intent);
                            } else {
                                ((AssetsActivity) activity).onResume();
                            }
                            return;
                        }
                        Tracker.INSTANCE.pointGroups = Tracker.INSTANCE.getAllPointIdsInAsset(asset.id);
                        Intent intent = new Intent(activity, PointsActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                        return;
                    }
                    Tracker.INSTANCE.startScan(activity);
                } catch (Exception ex) {
                    Toast.makeText(
                            activity,
                            String.format(activity.getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
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
        CheckedTextView checkedTextView = (CheckedTextView) convertView;
        checkedTextView.setText(group.name);
        checkedTextView.setChecked(isExpanded);
        ColorBarDrawable drawable = new ColorBarDrawable((float) group.getCompleteness());
        checkedTextView.setBackground(drawable);
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
}
