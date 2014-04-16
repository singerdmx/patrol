package com.mbrite.patrol.app;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class MeasureEnterValueFragment extends PointsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return renderView(inflater, R.layout.fragment_meaure_enter_value);
    }
}
