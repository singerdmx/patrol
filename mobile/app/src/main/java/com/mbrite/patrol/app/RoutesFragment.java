package com.mbrite.patrol.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.*;
import com.mbrite.patrol.widget.RouteAdapter;

import org.json.JSONException;

import java.util.*;

public class RoutesFragment extends ParentFragment {
    private static final String TAG = RoutesFragment.class.getSimpleName();

    private ArrayList<Route> routes = new ArrayList<Route>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();

        try {
            this.routes = RouteProvider.INSTANCE.getRoutes(getActivity());
        }catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RouteAdapter adapter = new RouteAdapter(
                getActivity(),
                this.routes);
        setListAdapter(adapter);
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }
}
