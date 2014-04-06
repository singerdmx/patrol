package com.mbrite.patrol.app;

import android.app.ListFragment;
import android.content.*;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.content.providers.PointProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;
import com.mbrite.patrol.widget.PointAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class PointsFragment extends ParentFragment {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        PointAdapter adapter = new PointAdapter(
                getActivity(),
                this.pointList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Point point = (Point) getListAdapter().getItem(position);
        try {
            RecordProvider.INSTANCE.offerPoint(getActivity(), point.id);
            Intent intent = new Intent(getActivity(), MeasureActivity.class);
            intent.putExtra(Constants.TPM_TYPE, point.tpmType);
            intent.putExtra(Constants.STATUS, point.status);
            intent.putExtra(Constants.STANDARD, point.standard);
            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}