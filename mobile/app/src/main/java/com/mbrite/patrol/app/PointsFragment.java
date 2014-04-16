package com.mbrite.patrol.app;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.model.*;

public class PointsFragment extends Fragment {
    private static final String TAG = PointsFragment.class.getSimpleName();

    protected PointGroup point;

    protected View renderView(LayoutInflater inflater, int resource) {
        int pointId = Integer.parseInt(this.getTag());
        point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        View v = inflater.inflate(resource, null);
        try {
            TextView standardView = (TextView) v.findViewById(R.id.standard);
            standardView.setText(point.tpmType);
            TextView statusView = (TextView) v.findViewById(R.id.status);
            statusView.setText(point.status);
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return v;
    }
}