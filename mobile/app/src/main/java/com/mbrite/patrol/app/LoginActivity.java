package com.mbrite.patrol.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Settings;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Utils.setDefaultLocale(getBaseContext());
        super.onCreate(savedInstanceState);

        if (Tracker.INSTANCE.lastLoginTimestamp != null &&
                System.currentTimeMillis() - Tracker.INSTANCE.lastLoginTimestamp < 60000) {
            Tracker.INSTANCE.lastLoginTimestamp = null;
            goToMainActivity();
            return;
        }
        setContentView(R.layout.activity_login);

        setupOffLineButton();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mSignInButton = (TextView) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        try {
            checkAppVersion();
            Record record = RecordProvider.INSTANCE.get(this);
            if (Tracker.INSTANCE.offLine || record != null) {
                // If there is open record in progress, skip login
                goToMainActivity();
                return;
            }
        } catch (Exception ex) {
            Toast.makeText(
                    LoginActivity.this,
                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        signInWithSavedUsernameAndPassword();
    }

    // Called to lazily initialize the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // Called every time user clicks on an action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.clear_local_data:
                Utils.clearLocalData(this);
                Toast.makeText(
                        this,
                        getString(R.string.complete),
                        Toast.LENGTH_LONG)
                        .show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setTitle(R.string.quit_app)
                    .setMessage(R.string.confirm_quit_app)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            LoginActivity.super.onBackPressed();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing.
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
            return;
        }
        super.onBackPressed();
    }

    private boolean isSiteURLEmpty() {
        if (StringUtils.isBlank(Utils.getSiteURI(this))) {
            new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                    .setMessage(R.string.error_please_input_site_url)
                    .setTitle(R.string.error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
                        }
                    }).setIcon(R.drawable.error).show();
            return true;
        }

        return false;
    }

    private void checkAppVersion()
            throws IOException {
        if (!FileMgr.exists(this, Constants.APP_VERSION_FILE) ||
                !FileMgr.read(this, Constants.APP_VERSION_FILE).equals(Constants.APP_VERSION)) {
            if (FileMgr.exists(this, Constants.RECORD_FILE_NAME)) {
                RecordProvider.INSTANCE.completeCurrentRecord(this);
                Toast.makeText(
                        this,
                        R.string.app_version_upgrade_message,
                        Toast.LENGTH_LONG)
                        .show();
            }
            Utils.clearLocalData(this);
            FileMgr.write(this, Constants.APP_VERSION_FILE, Constants.APP_VERSION);
        }
    }

    private void signInWithSavedUsernameAndPassword() {
        String[] savedUsernameAndPassword = Utils.getSavedUserEmailAndPassword(this);
        if (savedUsernameAndPassword != null && !Constants.OFFLINE.equals(savedUsernameAndPassword[0])) {
            mUsernameView.setText(savedUsernameAndPassword[0]);
            mPasswordView.setText(savedUsernameAndPassword[1]);
            mSignInButton.performClick();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(Constants.MAIN_ACTIVITY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors , the errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (isSiteURLEmpty()) {
            return;
        }

        if (mAuthTask != null) {
            return;
        }

        if (!Utils.isNetworkConnected(this)) {
            attemptOffline(getString(R.string.error_no_network));
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setupOffLineButton() {
        TextView offLineButton = (TextView) findViewById(R.id.off_line_button);

        if (Settings.DISABLE_OFFLINE_MODE) {
            offLineButton.setVisibility(View.INVISIBLE);
            return;
        }
        offLineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptOffline(null);
            }
        });
    }

    private void attemptOffline(String message) {
        if (Settings.DISABLE_OFFLINE_MODE) {
            if (StringUtils.isNoneBlank(message)) {
                new AlertDialog.Builder(this,
                        R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                        .setMessage(message)
                        .setTitle(R.string.error)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // do nothing
                                    }
                                }
                        ).setIcon(R.drawable.error).show();
            }
            return;
        }

        Tracker.INSTANCE.offLine = false;
        AlertDialog.Builder builder = (new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                .setTitle(R.string.use_offline_mode)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Tracker.INSTANCE.offLine = true;
                            Utils.saveUserEmailAndPassword(LoginActivity.this, Constants.OFFLINE, "");
                            goToMainActivity();
                        } catch (Exception ex) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing.
                    }
                }).setIcon(R.drawable.question));

        if (message != null) {
            builder.setMessage(message + getString(R.string.use_offline_mode));
        }
        builder.show();
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserEmail;
        private final String mPassword;
        private String errorMsg;

        UserLoginTask(String username, String password) {
            mUserEmail = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (!Utils.isValidUserEmailAndPassword(LoginActivity.this, mUserEmail, mPassword)) {
                    errorMsg = getString(R.string.error_incorrect_password);
                    return false;
                }

                return true;
            } catch (JSONException ex) {
                errorMsg = String.format("JSONException: %s", ex.getLocalizedMessage());
            } catch (URISyntaxException | IllegalStateException ex) {
                errorMsg = String.format(getString(R.string.error_site_url_invalid),
                        RestClient.INSTANCE.getSite());
            } catch (IOException ex) {
                errorMsg = String.format(getString(R.string.error_network_connection_failure),
                        RestClient.INSTANCE.getSite());
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        attemptOffline(errorMsg);
                    }
                });
            } catch (Exception ex) {
                errorMsg = String.format(getString(R.string.error_of), ex.getLocalizedMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try {
                    Utils.saveUserEmailAndPassword(LoginActivity.this, mUserEmail, mPassword);
                    Utils.updateDataFiles(LoginActivity.this);
                    new UploadTask(LoginActivity.this).execute();
                    Tracker.INSTANCE.lastLoginTimestamp = System.currentTimeMillis();
                    goToMainActivity();
                } catch (Exception ex) {
                    errorMsg = String.format(getString(R.string.error_of), ex.getLocalizedMessage());
                }
            }

            if (StringUtils.isNoneBlank(errorMsg)) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mSignInButton.setError(errorMsg);
                        mPasswordView.setError(errorMsg);
                        mPasswordView.requestFocus();
                        Toast.makeText(
                                LoginActivity.this,
                                errorMsg,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



