package com.mbrite.patrol.app;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.model.*;


public class PointsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int pointId : Tracker.INSTANCE.pointGroups) {
            PointGroup point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
            if (fragmentManager.findFragmentByTag(Integer.toString(pointId)) == null) {
                PointsFragment fragment;
                int type = point.id % 2;
                switch (type) {
                    case 0:
                        fragment = new MeasureSelectValueFragment();
                        break;
                    case 1:
                        fragment = new MeasureEnterValueFragment();
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Invalid point type: %d", type));
                }
                fragmentTransaction.add(R.id.fragment_container, fragment, Integer.toString(pointId));
            }
        }

        fragmentTransaction.commit();
        setupCancelButton();
    }

    private void setupCancelButton() {
        Button assetListButton = (Button) findViewById(R.id.cancel);
        assetListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: pop up warning
                Intent intent = new Intent(PointsActivity.this, AssetsActivity.class);
                startActivity(intent);
                PointsActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.points, menu);
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

     public static class EnterMeasureValueFragment extends PointsFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_meaure_enter_value, container, false);
        }
    }

    public static class SelectMeasureValueFragment extends PointsFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_meaure_select_value, container, false);
        }
    }

}
