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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.mbrite.patrol.common.*;
import com.mbrite.patrol.connection.RestClient;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.Record;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.io.*;

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
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setDefaultLocale(getBaseContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
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
            if (record != null) {
                // If there is open record in progress, skip login
                startActivity(new Intent(Constants.MAIN_ACTIVITY));
                finish();
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
            default:
                return false;
        }
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
            FileMgr.write(this, Constants.APP_VERSION_FILE, Constants.APP_VERSION);
        }
    }

    private void signInWithSavedUsernameAndPassword() {
        String[] savedUsernameAndPassword = Utils.getSavedUsernameAndPassword(this);
        if (savedUsernameAndPassword != null) {
            mUsernameView.setText(savedUsernameAndPassword[0]);
            mPasswordView.setText(savedUsernameAndPassword[1]);
            mSignInButton.performClick();
        }
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors , the errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
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

    private void attemptOffline(String message) {
        new AlertDialog.Builder(this, R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                .setTitle(R.string.use_offline_mode)
                .setMessage(message + getString(R.string.use_offline_mode))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Utils.saveUsernameAndPassword(LoginActivity.this, Constants.OFFLINE, "");
                            startActivity(new Intent(Constants.MAIN_ACTIVITY));
                            finish();
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
                }).setIcon(R.drawable.question).show();
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt offline if server is down
            try {
                return Utils.isValidUsernameAndPassword(LoginActivity.this, mUsername, mPassword);
            } catch (JSONException ex) {
                mSignInButton.setError(ex.getLocalizedMessage());
                Toast.makeText(
                        LoginActivity.this,
                        String.format("JSONException: %s", ex.getLocalizedMessage()),
                        Toast.LENGTH_LONG)
                        .show();
            } catch (URISyntaxException | IllegalStateException ex) {
                mSignInButton.setError(ex.getLocalizedMessage());
                Toast.makeText(
                        LoginActivity.this,
                        String.format(getString(R.string.error_site_url_invalid),
                                RestClient.INSTANCE.getSite()),
                        Toast.LENGTH_LONG)
                        .show();
            } catch (IOException ex) {
                mSignInButton.setError(ex.getLocalizedMessage());
                Toast.makeText(
                        LoginActivity.this,
                        String.format(getString(R.string.error_network_connection_failure),
                                RestClient.INSTANCE.getSite()),
                        Toast.LENGTH_LONG)
                        .show();
            } catch (Exception ex) {
                mSignInButton.setError(ex.getLocalizedMessage());
                Toast.makeText(
                        LoginActivity.this,
                        String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                        Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try {
                    Utils.saveUsernameAndPassword(LoginActivity.this, mUsername, mPassword);
                    Utils.updateDataFiles(LoginActivity.this);
                    startActivity(new Intent(Constants.MAIN_ACTIVITY));
                    finish();
                } catch (JSONException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format("JSONException: %s", ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (URISyntaxException | IllegalStateException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format(getString(R.string.error_site_url_invalid),
                                          RestClient.INSTANCE.getSite()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (IOException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format(getString(R.string.error_network_connection_failure),
                                          RestClient.INSTANCE.getSite()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (Exception ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(
                        LoginActivity.this,
                        R.string.error_incorrect_password,
                        Toast.LENGTH_LONG)
                        .show();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



