package com.mbrite.patrol.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.mbrite.patrol.app.*;
import com.mbrite.patrol.model.Route;

public class RouteAdapter extends ArrayAdapter<Route> {

    private final Activity context;
    private final ArrayList<Route> itemsArrayList;

    public RouteAdapter(Activity context, ArrayList<Route> itemsArrayList) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        Route route = itemsArrayList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Get rowView from inflater
            rowView = inflater.inflate(R.layout.activity_list_item_route, parent, false);
            final View view = rowView;
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) rowView.findViewById(R.id.check);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            Route element = (Route) viewHolder.checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());
                            if (buttonView.isChecked()) {
                                view.setBackgroundResource(R.drawable.alterselector2);
                            } else {
                                view.setBackgroundResource(R.drawable.alterselector1);
                            }
                        }
                    });
            rowView.setTag(viewHolder);
            viewHolder.checkbox.setTag(route);
        } else {
            rowView = convertView;
            ((ViewHolder) rowView.getTag()).checkbox.setTag(route);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.text.setText(route.description);
        holder.checkbox.setChecked(itemsArrayList.get(position).isSelected());
        return rowView;
    }
}
