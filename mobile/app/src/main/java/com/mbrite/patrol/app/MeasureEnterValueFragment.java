package com.mbrite.patrol.app;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.view.inputmethod.*;

import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.RecordStatus;

import org.apache.commons.lang3.StringUtils;

public class MeasureEnterValueFragment extends PointsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = renderView(inflater, R.layout.fragment_meaure_enter_value);
        final EditText valueView = (EditText) v.findViewById(R.id.value);
        if (pointRecord != null) {
            valueView.setHint(pointRecord.result);
        }
        valueView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                //onFocus
                if (gainFocus) {
                    valueView.setHint("");
                    imm.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        return v;
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

        Double inputValue;
        try {
            inputValue = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            message = String.format(getString(R.string.error_not_a_number), value);
            return false;
        }

        if (point.category == 50) {
            int i = 0;
            Double min = Utils.getDouble(point.choice.get(i++));
            Double low = Utils.getDouble(point.choice.get(i++));
            Double high = Utils.getDouble(point.choice.get(i++));
            Double max = Utils.getDouble(point.choice.get(i++));
            if ((min != null && inputValue < min) ||
                    (max != null && inputValue > max)) {
                status = RecordStatus.FAIL;
                message = point.toString() + ": " + getString(R.string.result_in_fail_status);
            } else if ((low != null && inputValue < low) ||
                    (high != null && inputValue > high)) {
                status = RecordStatus.WARN;
                message = point.toString() + ": " + getString(R.string.result_in_warning_status);
            }
        }

        return super.save() && (status == RecordStatus.PASS);
    }

    @Override
    public String getWarning() {
        if (StringUtils.isBlank(value)) {
            return point.toString() + ": " + getString(R.string.error_no_data); // User did not enter value
        }
        return null;
    }

}
