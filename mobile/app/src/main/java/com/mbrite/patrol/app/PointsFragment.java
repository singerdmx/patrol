package com.mbrite.patrol.app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
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
    protected int[] layoutIds = new int[]{
            R.id.title,
            R.id.pointCodeLine,
//            R.id.secondLine,
            R.id.range,
            R.id.select_content,
            R.id.content,
            R.id.memo
    };

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
            if (point.pointCode != null) {
                ((TextView) view.findViewById(R.id.pointCode)).setText(point.pointCode);
            }
//            TextView stateView = (TextView) view.findViewById(R.id.state);
//            stateView.setText(point.state);
            memoView = (EditText) view.findViewById(R.id.memo_value);
            if (pointRecord != null) {
                memoView.setText(pointRecord.memo);
            }
            setupAddPhotoButton(view);
            setBackground();
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(getActivity(), ex);
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

    private void setupAddPhotoButton(View view) {
        Button button = (Button) view.findViewById(R.id.add_memo_photo);
        if (point.getImage() != null ||
                (pointRecord != null && pointRecord.image != null)) {
            if (point.getImage() == null) {
                point.setImage(pointRecord.image);
            }
            button.setBackground(getResources().getDrawable(R.drawable.background_green));
            button.setText(R.string.change_photo);
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.background_cyan));
            button.setText(R.string.add_photo);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PointsActivity) getActivity()).save(false);
                Tracker.INSTANCE.targetPoint = point;
                Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
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

            for (int id : layoutIds) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.setBackgroundResource(resId);
                }
            }
        }
    }

}
