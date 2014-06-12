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

    private Double min;
    private Double low;
    private Double high;
    private Double max;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = renderView(inflater, R.layout.fragment_meaure_enter_value);
        setNormalRangeValue(v);

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

    private void setNormalRangeValue(View v) {
        TextView rangeValue = (TextView) v.findViewById(R.id.normal_range_value);
        String rangeDisplayValue = "N/A";
        if (point.category == 50) {
            min = Utils.getDouble(point.choice.get(0));
            low = Utils.getDouble(point.choice.get(1));
            high = Utils.getDouble(point.choice.get(2));
            max = Utils.getDouble(point.choice.get(3));
            if (min != null && max != null) {
                rangeDisplayValue = String.format("介于%1$,.0f和%2$,.0f之间", min, max);
            } else if (min != null) {
                rangeDisplayValue = String.format("大于%1$,.0f", min);
            } else if (max != null) {
                rangeDisplayValue = String.format("小于%1$,.0f", max);
            }
        }
        rangeValue.setText(rangeDisplayValue);
    }

}
