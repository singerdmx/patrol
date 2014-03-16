package com.mbrite.patrol.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.mbrite.patrol.content.providers.RouteProvider;
import com.mbrite.patrol.model.Route;
import com.mbrite.patrol.widget.RouteAdapter;

import java.util.*;

public class RoutesFragment extends ListFragment {
    private static final String TAG = RoutesFragment.class.getSimpleName();

    private RouteProvider routeProvider;
    private ArrayList<Route> routes;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.routeProvider = new RouteProvider(getActivity());
        this.routes = routeProvider.getRoutes();

        RouteAdapter adapter = new RouteAdapter(
                getActivity(),
                this.routes);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Route route = (Route) getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.selected_route), route.description))
               .setTitle(R.string.confirm_route)
               .setCancelable(false)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: go to AssetActivity
                        Toast.makeText(getActivity(),
                                String.format("%d: %s", route.id, route.description),
                                Toast.LENGTH_LONG).show();
                    }
               })
               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
