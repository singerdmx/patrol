package com.mbrite.patrol.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HistoricalDataGraphActivity extends ParentActivity {

    private Spinner graphType;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Drawable startDatePickerBackground;
    private Drawable endDatePickerBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data_graph);
        setWindowTitle(R.string.historical_data_graph);

        setupReturnButton();
        setupShowGraphButton();
    }

    @Override
    public void onResume() {
        super.onResume();

        resetDatePickersBackground();

        if (Tracker.INSTANCE.targetPoint == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView pointTitle = (TextView) findViewById(R.id.point_title);
        pointTitle.setText(Tracker.INSTANCE.targetPoint.toString());

        graphType = (Spinner) findViewById(R.id.graph_type);
        List<String> types = new ArrayList<>();
        for (int typeResId : Constants.GRAPH_TYPES.keySet()) {
            types.add(getString(typeResId));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphType.setAdapter(dataAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.historical_data_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.logout(this);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void setupReturnButton() {
        TextView button = (TextView) findViewById(R.id.return_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetPoint = null;
                finish();
            }
        });
    }

    private void setupShowGraphButton() {
        TextView button = (TextView) findViewById(R.id.show_graph_button);
        startDatePicker = (DatePicker) findViewById(R.id.start_date);
        startDatePickerBackground = startDatePicker.getBackground();
        endDatePicker = (DatePicker) findViewById(R.id.end_date);
        endDatePickerBackground = endDatePicker.getBackground();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                String type = null;
                for (String urlParam : Constants.GRAPH_TYPES.values()) {
                    if (i >= graphType.getSelectedItemPosition()) {
                        type = urlParam;
                        break;
                    }
                    i++;
                }

                Date startDate = getDate(startDatePicker);
                Date endDate = getDate(endDatePicker);

                if (!isDateRangeValid(startDate, endDate)) {
                    return;
                }

                try {
                    String relativeUri =
                            String.format("graphs?check_point_id=%d&type=%s&check_time=%d..%d",
                                    Tracker.INSTANCE.targetPoint.id,
                                    type,
                                    startDate.getTime()/1000,
                                    endDate.getTime()/1000);
                    URI graph_url = new URI(
                            Utils.getSiteURI(HistoricalDataGraphActivity.this).trim())
                            .resolve(relativeUri);

                    Toast.makeText(
                            HistoricalDataGraphActivity.this,
                            graph_url.toString(),
                            Toast.LENGTH_LONG)
                            .show();

                } catch (Exception ex) {
                    Toast.makeText(
                            HistoricalDataGraphActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private boolean isDateRangeValid(Date startDate, Date endDate) {
        resetDatePickersBackground();

        if (startDate == null || endDate == null) {
            return false;
        }

        if (startDate.after(new Date())) {
            startDatePicker.setBackground(
                    getResources().getDrawable(R.drawable.background_darkred));
            Toast.makeText(
                    this,
                    String.format(
                            getString(R.string.error_is_after_today),
                            getString(R.string.start_date)),
                    Toast.LENGTH_LONG
            )
                    .show();
            return false;
        }

        if (endDate.after(new Date())) {
            endDatePicker.setBackground(
                    getResources().getDrawable(R.drawable.background_darkred));
            Toast.makeText(
                    this,
                    String.format(
                            getString(R.string.error_is_after_today),
                            getString(R.string.end_date)),
                    Toast.LENGTH_LONG
            )
                    .show();
            return false;
        }

        if (startDate.after(endDate)) {
            startDatePicker.setBackground(
                    getResources().getDrawable(R.drawable.background_darkred));
            endDatePicker.setBackground(
                    getResources().getDrawable(R.drawable.background_darkred));
            Toast.makeText(
                    this,
                    getString(R.string.error_start_date_after_end_date),
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }

    private void resetDatePickersBackground() {
        startDatePicker.setBackground(startDatePickerBackground);
        endDatePicker.setBackground(endDatePickerBackground);
    }

    private Date getDate(DatePicker datePicker) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            isoFormat.setTimeZone(TimeZone.getDefault());
            String dateString = String.format("%04d-%02d-%02dT00:00:00",
                    datePicker.getYear(),
                    datePicker.getMonth() + 1,
                    datePicker.getDayOfMonth());
            return isoFormat.parse(dateString);
        } catch (Exception ex) {
            Toast.makeText(
                    this,
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        return null;
    }
}
