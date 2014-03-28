package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.model.Point;
import com.mbrite.patrol.widget.PointAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class PointsFragment extends ListFragment {
    private static final String TAG = PointsFragment.class.getSimpleName();

    private int[] points;
    private ArrayList<Point> pointList = new ArrayList<Point>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        points = extras.getIntArray(Constants.POINTS);

        try {
//            this.pointList.addAll(AssetProvider.INSTANCE.getAssets(getActivity(), points));
//        } catch (JSONException ex) {
//            Toast.makeText(
//                    getActivity(),
//                    String.format("JSONException: %s", ex.getLocalizedMessage()),
//                    Toast.LENGTH_LONG)
//                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        PointAdapter adapter = new PointAdapter(
                getActivity(),
                this.pointList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Point point = (Point) getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.selected_point), point.description))
                .setTitle(R.string.confirm_point)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent(getActivity(), BarcodeActivity.class);
//                        startActivity(intent);
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