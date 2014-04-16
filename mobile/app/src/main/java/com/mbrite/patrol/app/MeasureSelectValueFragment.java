package com.mbrite.patrol.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import java.util.*;

public class MeasureSelectValueFragment extends PointsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = renderView(inflater, R.layout.fragment_meaure_select_value);
        Spinner select = (Spinner) v.findViewById(R.id.select);
        List<String> l = new ArrayList<>();
        l.add("1");
        l.add("2");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                l);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select.setAdapter(dataAdapter);
        return v;
    }
}
