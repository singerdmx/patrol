package com.mbrite.patrol.app;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.*;

import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class PointsActivity extends Activity {

    private FragmentManager fragmentManager;
    private Set<PointsFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragments = new HashSet<>();

        try {
            for (int pointId : Tracker.INSTANCE.pointGroups) {
                String tag = Integer.toString(pointId);
                PointGroup point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
                PointsFragment fragment = (PointsFragment) fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    int category = point.category;
                    switch (category) {
                        case 10:
                        case 20:
                            // scan only, skip such point
                            RecordProvider.INSTANCE.addOrUpdatePointRecord(point, "", 0, "", this);
                            continue;
                        case 30:
                        case 50:
                            fragment = new MeasureEnterValueFragment();
                            break;
                        case 40:
                        case 41:
                            fragment = new MeasureSelectValueFragment();
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

    private void setupCancelButton() {
        Button button = (Button) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAssetsActivity();
            }
        });
    }

    private void setupSaveButton() {
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<String> messages = new ArrayList<String>();
                    for (PointsFragment fragment : fragments) {
                        if (!fragment.validate() && StringUtils.isNoneBlank(fragment.message)) {
                            messages.add(fragment.message);
                        }
                    }

                    if (!messages.isEmpty()) {
                        // pop up Dialog showing error occurred and stay on current page
                        new AlertDialog.Builder(PointsActivity.this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                                .setTitle(R.string.error)
                                .setMessage(StringUtils.join(messages, '\n'))
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Do nothing
                                    }
                                }).setIcon(R.drawable.error).show();
                        return;
                    }

                    promptWarning();
                } catch (Exception ex) {
                    Toast.makeText(
                            PointsActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
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
                    }).setIcon(android.R.drawable.stat_sys_warning).show();
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
        Intent intent = new Intent(PointsActivity.this, AssetsActivity.class);
        startActivity(intent);
        finish();
    }

}
