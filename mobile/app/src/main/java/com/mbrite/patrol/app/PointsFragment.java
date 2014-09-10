package com.mbrite.patrol.app;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.PointGroup;
import com.mbrite.patrol.model.PointRecord;
import com.mbrite.patrol.model.RecordStatus;

public class PointsFragment extends Fragment {
    private static final String TAG = PointsFragment.class.getSimpleName();

    protected PointGroup point;
    protected PointRecord pointRecord;
    protected View view;
    protected String message;
    protected EditText memoView;
    protected InputMethodManager imm;
    protected int[] linearLayoutIds = new int[]{R.id.title,
            R.id.secondLine, R.id.range,
            R.id.select_content, R.id.content, R.id.memo};

    protected String value = "";
    protected int status = RecordStatus.PASS; // Default to Pass

    protected View renderView(LayoutInflater inflater, int resource) {
        int pointId = Integer.parseInt(this.getTag());
        point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        pointRecord = RecordProvider.INSTANCE.getPointRecord(pointId);
        view = inflater.inflate(resource, null);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            TextView nameView = (TextView) view.findViewById(R.id.name);
            nameView.setText(point.name);
            TextView descriptionView = (TextView) view.findViewById(R.id.description);
            descriptionView.setText(point.description);
            TextView stateView = (TextView) view.findViewById(R.id.state);
            stateView.setText(point.state);
            memoView = (EditText) view.findViewById(R.id.memo_value);
            if (pointRecord != null) {
                memoView.setText(pointRecord.memo);
            }
            setBackground();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
        return view;
    }

    /**
     * @return true if successful, false if error occurred
     */
    public boolean save() {
        try {
            RecordProvider.INSTANCE.addOrUpdatePointRecord(point, value, status,
                    memoView.getText().toString(), getActivity());
            return true;
        } catch (Exception ex) {
            message = String.format(getString(R.string.error_of), ex.getLocalizedMessage());
        }

        return false;
    }

    /**
     * @return true if entered value is valid, false otherwise
     */
    public boolean validate() {
        return true;
    }

    public String getWarning() {
        return null;
    }

    private void setBackground() {
        if (pointRecord != null) {
            // set background color
            int resId;
            switch (pointRecord.status) {
                case RecordStatus.PASS:
                    // pass
                    resId = R.drawable.pass_row_selector;
                    break;
                case RecordStatus.FAIL:
                    // fail
                    resId = R.drawable.fail_row_selector;
                    break;
                case RecordStatus.WARN:
                    resId = R.drawable.warning_row_selector;
                    // warning
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid point status: %d", pointRecord.status));
            }

            for (int id : linearLayoutIds) {
                LinearLayout layout = (LinearLayout) view.findViewById(id);
                if (layout != null) {
                    layout.setBackgroundResource(resId);
                }
            }
        }
    }

}
