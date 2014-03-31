package com.mbrite.patrol.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.model.Point;

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

//        if (position % 2 == 0){
//            rowView.setBackgroundResource(R.drawable.alterselector1);
//        } else {
//            rowView.setBackgroundResource(R.drawable.alterselector2);
//        }

        Point point = itemsArrayList.get(position);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
        descriptionView.setText(point.description);
        TextView tpmTypeView = (TextView) rowView.findViewById(R.id.tpm_type);
        tpmTypeView.setText(point.tpmType);

        return rowView;
    }
}
