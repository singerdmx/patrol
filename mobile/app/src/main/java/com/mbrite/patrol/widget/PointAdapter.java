package com.mbrite.patrol.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Point;
import com.mbrite.patrol.model.PointRecord;
import com.mbrite.patrol.model.Record;

import java.util.ArrayList;

public class PointAdapter extends ArrayAdapter<Point> {

    private final Context context;
    private final ArrayList<Point> itemsArrayList;

    public PointAdapter(Context context, ArrayList<Point> itemsArrayList) {

        super(context, R.layout.activity_list_item_asset, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_point, parent, false);
        Point point = itemsArrayList.get(position);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
        descriptionView.setText(point.description);
        TextView tpmTypeView = (TextView) rowView.findViewById(R.id.tpm_type);
        tpmTypeView.setText(point.tpmType);

        for (PointRecord p : RecordProvider.INSTANCE.currentAssetRecord.points) {
            if (p.id == point.id) {
                int result = p.result;
                switch(result) {
                    case 0:
                        rowView.setBackgroundResource(R.drawable.point_pass_row_selector);
                        break;
                    case 1:
                        rowView.setBackgroundResource(R.drawable.point_fail_row_selector);
                        break;

                }
                break;
            }
        }

        return rowView;
    }
}
