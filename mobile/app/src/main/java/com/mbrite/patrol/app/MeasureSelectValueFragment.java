package com.mbrite.patrol.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.mbrite.patrol.model.RecordStatus;

public class MeasureSelectValueFragment extends PointsFragment {

    private Spinner select;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        renderView(inflater, R.layout.fragment_meaure_select_value);
        select = (Spinner) view.findViewById(R.id.select);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                point.choice);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select.setAdapter(dataAdapter);
        if (pointRecord != null) {
            select.setSelection(point.choice.indexOf(pointRecord.result));
        }
        return view;
    }

    @Override
    public boolean save() {
        value = String.valueOf(select.getSelectedItem());
        if (point.category == 41) {
            if (!point.choice.get(0).equals(value)) {
                status = RecordStatus.FAIL;
                message = point.toString() + ": " + getString(R.string.result_in_fail_status);
            }
        }
        return super.save() && (status == RecordStatus.PASS);
    }
}
