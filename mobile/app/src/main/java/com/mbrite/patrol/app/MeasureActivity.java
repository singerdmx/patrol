package com.mbrite.patrol.app;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.PointProvider;
import com.mbrite.patrol.content.providers.RecordProvider;

import org.json.JSONObject;

import java.util.*;

public class MeasureActivity extends Activity {
    private static final String TAG = MeasureActivity.class.getSimpleName();

    private JSONObject standardJSON;
    private List<String> choice;

    private EditText valueView;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "started");
//        setContentView(R.layout.activity_measure);
//
//        Bundle extras = getIntent().getExtras();
//        TextView tpmTypeView = (TextView) findViewById(R.id.tpm_type);
//        tpmTypeView.setText(extras.getString(Constants.TPM_TYPE));
//        TextView statusView = (TextView) findViewById(R.id.status);
//        statusView.setText(extras.getString(Constants.STATUS));
//        TextView standardView = (TextView) findViewById(R.id.standard);
//        valueView = (EditText) findViewById(R.id.value);
//        valueView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                valueView.setHint("");
//            }
//        });
//        try {
//            standardJSON = new JSONObject(extras.getString(Constants.STANDARD));
//            standardView.setText(PointProvider.INSTANCE.getStandardDescription(standardJSON));
//
//            choice = PointProvider.INSTANCE.getChoice(standardJSON);
//            // get an instance of FragmentTransaction from your Activity
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            if (choice == null) {
//                if (RecordProvider.INSTANCE.currentPointRecord.result != -1) {
//                    valueView.setHint(RecordProvider.INSTANCE.currentPointRecord.value);
//                }
//                fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.fifthLine));
//            } else {
//                fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.fourthLine));
//                Spinner select = (Spinner) findViewById(R.id.select);
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
//                        this,
//                        android.R.layout.simple_spinner_item,
//                        choice);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                select.setAdapter(dataAdapter);
//                if (RecordProvider.INSTANCE.currentPointRecord.result != -1) {
//                    select.setSelection(choice.indexOf(RecordProvider.INSTANCE.currentPointRecord.value));
//                }
//            }
//            fragmentTransaction.commit();
//        } catch (Exception ex) {
//            Toast.makeText(
//                    this,
//                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
//
//        Button submitButton = (Button) findViewById(R.id.submit_button);
//        if (RecordProvider.INSTANCE.currentPointRecord.result != -1) {
//            submitButton.setText(R.string.update);
//        }
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String value = null;
//                int result = 0;
//                try {
//                    if (choice == null) {
//                        // enter value
//                        value = valueView.getText().toString();
//
//                        if (TextUtils.isEmpty(value)) {
//                            value = valueView.getHint().toString();
//                        }
//                        Double inputValue = Double.parseDouble(value);
//                        Double min = PointProvider.INSTANCE.getMin(standardJSON);
//                        Double max = PointProvider.INSTANCE.getMax(standardJSON);
//                        if ((min != null && inputValue < min) ||
//                                (max != null && inputValue > max)) {
//                            result = 1;
//                        }
//                    } else {
//                        // select value
//                        Spinner select = (Spinner) findViewById(R.id.select);
//                        value = String.valueOf(select.getSelectedItem());
//                        if (!choice.get(0).equals(value)) {
//                            result = 1;
//                        }
//                    }
//
//                    RecordProvider.INSTANCE.setCurrentPointRecord(MeasureActivity.this, value, result);
//                    finish();
//                }   catch (NumberFormatException ex) {
//                    Toast.makeText(
//                            MeasureActivity.this,
//                            String.format(getString(R.string.error_not_a_number), value),
//                            Toast.LENGTH_LONG)
//                            .show();
//                }   catch (Exception ex) {
//                    Toast.makeText(
//                            MeasureActivity.this,
//                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
//                            Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
}
