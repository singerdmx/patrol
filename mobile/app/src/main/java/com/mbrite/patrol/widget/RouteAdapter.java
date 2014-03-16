package com.mbrite.patrol.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.model.Route;

public class RouteAdapter extends ArrayAdapter<Route> {

    private final Context context;
    private final ArrayList<Route> itemsArrayList;

    public RouteAdapter(Context context, ArrayList<Route> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_route, parent, false);

        // Get the text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);

        // Set the text for textView
        labelView.setText(itemsArrayList.get(position).description);

        return rowView;
    }
}
