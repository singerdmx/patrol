package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.content.providers.PointProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Point;
import com.mbrite.patrol.model.Record;
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
        setDivider();

        Bundle extras = getActivity().getIntent().getExtras();
        points = extras.getIntArray(Constants.POINTS);

        try {
            pointList.addAll(PointProvider.INSTANCE.getPoints(getActivity(), points));
        } catch (JSONException ex) {
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
                        try {
                            RecordProvider.INSTANCE.offerPoint(getActivity(), point.id);
//                        Intent intent = new Intent(getActivity(), BarcodeActivity.class);
//                        startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(
                                    getActivity(),
                                    String.format("Error: %s", ex.getLocalizedMessage()),
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