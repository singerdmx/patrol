package com.mbrite.patrol.app;

import android.app.*;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.*;
import com.mbrite.patrol.widget.RouteAdapter;

import org.json.JSONException;

import java.util.*;

public class RoutesFragment extends ListFragment {
    private static final String TAG = RoutesFragment.class.getSimpleName();

    private RouteProvider routeProvider;
    private ArrayList<Route> routes = new ArrayList<Route>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDivider();

        try {
            this.routeProvider = new RouteProvider(getActivity());
            this.routes = routeProvider.getRoutes();
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
                       try {
                           Record record = RecordProvider.INSTANCE.get(getActivity());
                           record.route = route.id;
                           RecordProvider.INSTANCE.save(getActivity(), record);
                           Intent intent = new Intent(getActivity(), AssetsActivity.class);
                           intent.putExtra(Constants.ASSETS, route.assets);
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

    private void setDivider() {
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.black)));
        lv.setDividerHeight(1);
    }
}
