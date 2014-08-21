package com.mbrite.patrol.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mbrite.patrol.app.R;
import com.mbrite.patrol.model.Route;

import java.util.ArrayList;
import java.util.Set;

public class RouteAdapter extends ArrayAdapter<Route> {

    private final Activity context;
    private final ArrayList<Route> itemsArrayList;
    private final Set<Integer> completedRoutes;

    public RouteAdapter(Activity context, ArrayList<Route> itemsArrayList, Set<Integer> completedRoutes) {

        super(context, R.layout.activity_list_item_route, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.completedRoutes = completedRoutes;
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
            if (completedRoutes != null && completedRoutes.contains(route.id)) {
                view.setBackgroundResource(R.drawable.alterselector3);
            }
            rowView.setTag(viewHolder);
            viewHolder.checkbox.setTag(route);
        } else {
            rowView = convertView;
            ((ViewHolder) rowView.getTag()).checkbox.setTag(route);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.text.setText(route.name);
        holder.checkbox.setChecked(itemsArrayList.get(position).isSelected());
        return rowView;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }
}
