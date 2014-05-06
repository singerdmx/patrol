package com.mbrite.patrol.app;

import android.os.Bundle;
import android.content.*;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.*;
import com.mbrite.patrol.widget.RouteAdapter;

import org.json.JSONException;

import java.util.*;

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
            routes = RouteProvider.INSTANCE.getRoutes(getActivity());
        }catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
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
            Record record = RecordProvider.INSTANCE.get(getActivity());
            if (record != null) {
                ArrayList<Route> selectedRoutes = new ArrayList<>();
                for (Route route : routes) {
                    if (record.routes.indexOf(route.id) != -1) {
                        selectedRoutes.add(route);
                    }
                }

                Tracker.INSTANCE.createRouteGroups(selectedRoutes, getActivity());
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
