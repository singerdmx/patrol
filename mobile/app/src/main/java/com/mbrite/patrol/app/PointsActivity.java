package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.PointGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PointsActivity extends ParentActivity {

    private FragmentManager fragmentManager;
    private Set<PointsFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        setWindowTitle(R.string.check_point);

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragments = new HashSet<>();

        try {
            if (Tracker.INSTANCE.pointGroups == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            for (int pointId : Tracker.INSTANCE.pointGroups) {
                String tag = Integer.toString(pointId);
                PointGroup point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
                PointsFragment fragment = (PointsFragment) fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    int category = point.category;
                    if (Constants.CATEGORY_SCAN_ONLY.contains(category)) {
                        // scan only, skip such point
                        RecordProvider.INSTANCE.addOrUpdatePointRecord(point, "", 0, "", this);
                        continue;
                    }
                    switch (category) {
                        case 20:
                        case 30:
                        case 50:
                            fragment = new MeasureEnterValueFragment();
                            break;
                        case 40:
                        case 41:
                            fragment = new MeasureSelectValueFragment();
                            break;
                        case 51:
                            fragment = new MeasureEnterSelectValueFragment();
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Invalid point category: %d", category));
                    }

                    fragmentTransaction.add(R.id.fragment_container, fragment, tag);
                }
                fragments.add(fragment);
            }

            fragmentTransaction.commit();
            setupSaveButton();
            setupCancelButton();
        } catch (Exception ex) {
            Toast.makeText(
                    this,
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }
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

    public void save(boolean prompt) {
        try {
            Tracker.INSTANCE.targetPoint = null;
            List<String> messages = new ArrayList<String>();
            for (PointsFragment fragment : fragments) {
                if (!fragment.validate() && StringUtils.isNoneBlank(fragment.message)) {
                    messages.add(fragment.message);
                }
            }

            if (!messages.isEmpty()) {
                // pop up Dialog showing error occurred and stay on current page
                Toast.makeText(
                        PointsActivity.this,
                        String.format(StringUtils.join(messages, '\n')),
                        Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!prompt) {
                for (PointsFragment fragment : fragments) {
                    fragment.save();
                }
                return;
            }

            promptWarning();
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(PointsActivity.this, ex);
        }
    }

    private void setupCancelButton() {
        TextView button = (TextView) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker.INSTANCE.targetPoint = null;
                Intent intent = new Intent(PointsActivity.this, AssetsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupSaveButton() {
        TextView button = (TextView) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });
    }

    private void promptWarning() {
        final List<String> warnings = new ArrayList<String>();
        for (PointsFragment fragment : fragments) {
            String warning = fragment.getWarning();
            if (StringUtils.isNoneBlank(warning)) {
                warnings.add(warning);
            }
        }

        if (!warnings.isEmpty()) {
            // pop up Dialog showing a summary of warnings (because no value entered)
            new AlertDialog.Builder(PointsActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setTitle(R.string.warning)
                    .setMessage(StringUtils.join(warnings, '\n'))
                    .setPositiveButton(R.string._continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            promptNotice();
                        }
                    }).setNegativeButton(R.string._return, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Do nothing
                }
            }).setIcon(R.drawable.warning).show();
        } else {
            promptNotice();
        }
    }

    private void promptNotice() {
        List<String> messages = new ArrayList<String>();
        for (PointsFragment fragment : fragments) {
            if (!fragment.save() && StringUtils.isNoneBlank(fragment.message)) {
                messages.add(fragment.message);
            }
        }

        if (!messages.isEmpty()) {
            // pop up Dialog showing points that do not pass (fail or warning)
            new AlertDialog.Builder(PointsActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setTitle(R.string._notice)
                    .setMessage(StringUtils.join(messages, '\n'))
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            goToAssetsActivity();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_info).show();
        } else {
            goToAssetsActivity();
        }
    }

    private void goToAssetsActivity() {
        Intent intent = new Intent(this, AssetsActivity.class);
        if (Utils.getContinuousScanMode(this)) {
            intent.putExtra(Constants.CONTINUOUS_SCAN, true);
        }
        startActivity(intent);
        finish();
    }

}
