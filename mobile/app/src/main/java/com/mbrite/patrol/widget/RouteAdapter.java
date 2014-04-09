package com.mbrite.patrol.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;
import com.mbrite.patrol.model.Route;

public class RouteAdapter extends ArrayAdapter<Route> {

    private final Activity context;
    private final ArrayList<Route> itemsArrayList;

    public RouteAdapter(Activity context, ArrayList<Route> itemsArrayList) {

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
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        Route route = itemsArrayList.get(position);
        try {
            Record record = RecordProvider.INSTANCE.get(context);
            if (record != null && record.getRouteId() == route.id) {
                rowView.setBackgroundResource(R.drawable.alterselector2);
                icon.setImageResource(R.drawable.spanner);
            } else {
                icon.setImageResource(R.drawable.blank);
            }
        } catch (Exception ex) {
            Toast.makeText(
                    context,
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        // Get the text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);

        // Set the text for textView
        labelView.setText(itemsArrayList.get(position).description);

        return rowView;
    }
}
