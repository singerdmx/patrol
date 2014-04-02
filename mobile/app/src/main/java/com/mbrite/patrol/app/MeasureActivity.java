package com.mbrite.patrol.app;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.PointProvider;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;

import org.json.JSONObject;

import java.util.*;

public class MeasureActivity extends Activity {
    private static final String TAG = MeasureActivity.class.getSimpleName();

    private List<String> choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "started");
        setContentView(R.layout.activity_measure);

        Bundle extras = getIntent().getExtras();
        TextView tpmTypeView = (TextView) findViewById(R.id.tpm_type);
        tpmTypeView.setText(extras.getString(Constants.TPM_TYPE));
        TextView statusView = (TextView) findViewById(R.id.status);
        statusView.setText(extras.getString(Constants.STATUS));
        TextView standardView = (TextView) findViewById(R.id.standard);
        try {
            JSONObject standardJSON = new JSONObject(extras.getString(Constants.STANDARD));
            standardView.setText(PointProvider.INSTANCE.getStandardDescription(standardJSON));

            choice = PointProvider.INSTANCE.getChoice(standardJSON);
            // get an instance of FragmentTransaction from your Activity
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (choice == null) {
                fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.fifthLine));
            } else {
                fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.fourthLine));
                Spinner select = (Spinner) findViewById(R.id.select);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        choice);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                select.setAdapter(dataAdapter);
            }
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Toast.makeText(
                    this,
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String value = null;
               if (choice == null) {
                   // enter value
                   EditText valueView = (EditText) findViewById(R.id.value);
                   value = valueView.getText().toString();
               } else {
                   // select value
                   Spinner select = (Spinner) findViewById(R.id.select);
                   value = String.valueOf(select.getSelectedItem());
               }

                try {
                    RecordProvider.INSTANCE.setCurrentPointRecordValue(MeasureActivity.this, value);
                }   catch (Exception ex) {
                    Toast.makeText(
                            MeasureActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

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
                Utils.clearUsernameAndPassword(this);
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    public static class EnterMeasureValueFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_meaure_enter_value, container, false);
        }
    }

    public static class SelectMeasureValueFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_meaure_select_value, container, false);
        }
    }
}
