package com.mbrite.patrol.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mbrite.patrol.model.RecordStatus;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MeasureEnterSelectValueFragment extends PointsFragment {

    private Spinner select;

    private List<String> choice = new ArrayList<>(2);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        renderView(inflater, R.layout.fragment_meaure_enter_select_value);
        select = (Spinner) view.findViewById(R.id.select);
        choice.add(getString(R.string.normal));
        choice.add(getString(R.string.abnormal));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                choice);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select.setAdapter(dataAdapter);
        if (pointRecord != null) {
            select.setSelection(pointRecord.status);
        }

        final EditText valueView = (EditText) view.findViewById(R.id.value);
        if (pointRecord != null) {
            valueView.setHint(pointRecord.result);
        } else if (StringUtils.isNoneBlank(point.defaultValue)) {
            valueView.setText(point.defaultValue);
        }
        valueView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                //onFocus
                if (gainFocus) {
                    valueView.setHint("");
                    imm.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        return view;
    }

    /**
     * @return true if entered value is valid, false otherwise
     */
    @Override
    public boolean validate() {
        EditText valueView = (EditText) view.findViewById(R.id.value);
        value = valueView.getText().toString();
        if (StringUtils.isBlank(value) && valueView.getHint() != null) {
            value = valueView.getHint().toString();
        }

        if (StringUtils.isBlank(value)) {
            return true;
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            message = point.toString() + ": " + String.format(getString(R.string.error_not_a_number), value);
            return false;
        }
        return true;
    }

    @Override
    public boolean save() {
        if (StringUtils.isBlank(value)) {
            return true; // User did not enter value, skip
        }

        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            message = String.format(getString(R.string.error_not_a_number), value);
            return false;
        }

        status = select.getSelectedItemPosition();
        if (status == RecordStatus.FAIL) {
            message = point.toString() + ": " + getString(R.string.result_in_fail_status);
        }
        return super.save() && (status == RecordStatus.PASS);
    }
}
