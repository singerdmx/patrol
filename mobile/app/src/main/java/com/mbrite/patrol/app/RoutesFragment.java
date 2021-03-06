package com.mbrite.patrol.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.content.providers.RouteProvider;
import com.mbrite.patrol.model.Record;
import com.mbrite.patrol.model.Route;
import com.mbrite.patrol.widget.RouteAdapter;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class RoutesFragment extends ParentFragment {
    private static final String TAG = RoutesFragment.class.getSimpleName();

    private ArrayList<Route> routes;
    private Set<Integer> completedRoutes = new TreeSet<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();

        try {
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                for (int r : extras.getIntArray(Constants.ROUTES)) {
                    completedRoutes.add(r);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            routes = RouteProvider.INSTANCE.getRoutes(getActivity());
            Record record = RecordProvider.INSTANCE.get(getActivity());
            if (record != null) {
                Tracker.INSTANCE.createRouteGroups(routes, record, getActivity());
                Intent intent = new Intent(getActivity(), AssetsActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                RouteAdapter adapter = new RouteAdapter(
                        getActivity(),
                        routes,
                        completedRoutes);
                setListAdapter(adapter);
                completedRoutes = new TreeSet<>();
            }
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }
}
