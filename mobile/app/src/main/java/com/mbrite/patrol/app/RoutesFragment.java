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

    private boolean switchRoute;

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

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Route route = (Route) getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getString(R.string.selected_route), route.description);

        try {
            Record record = RecordProvider.INSTANCE.get(getActivity());
            if (record != null) {
                switchRoute = record.getRouteId() != route.id;
            }
            if (switchRoute) {
                message = String.format(getString(R.string.confirm_new_route), route.description);
            }
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        builder.setMessage(message)
               .setTitle(R.string.confirm_route)
               .setCancelable(false)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       try {
                           if (switchRoute) {
                               RecordProvider.INSTANCE.reset(getActivity());
                           }
                           Record record = RecordProvider.INSTANCE.get(getActivity());
                           if (record == null) {
                               RecordProvider.INSTANCE.create(getActivity(), route.id);
                           }
                           Tracker.INSTANCE.assetIds = route.assets;
                           Intent intent = new Intent(getActivity(), AssetsActivity.class);
                           startActivity(intent);
                       }  catch (Exception ex) {
                           Toast.makeText(
                                   getActivity(),
                                   String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                   Toast.LENGTH_LONG)
                                   .show();
                       }
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
