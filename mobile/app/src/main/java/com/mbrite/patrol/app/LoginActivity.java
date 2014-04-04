package com.mbrite.patrol.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.*;

import java.net.URISyntaxException;
import java.io.*;
import java.util.*;

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

        String[] savedUsernameAndPassword = Utils.getSavedUsernameAndPassword(this);
        if (savedUsernameAndPassword != null) {
            mUsernameView.setText(savedUsernameAndPassword[0]);
            mPasswordView.setText(savedUsernameAndPassword[1]);
            mSignInButton.performClick();
        }
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

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors , the errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
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
            return Utils.isValidUsernameAndPassword(mUsername, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try {
                    Utils.savedUsernameAndPassword(LoginActivity.this, mUsername, mPassword);
                    updateSavedFile(Constants.ROUTES, Constants.ROUTES_FILE_NAME);
                    updateSavedFile(Constants.ASSETS, Constants.ASSETS_FILE_NAME);
                    updateSavedFile(Constants.POINTS, Constants.POINTS_FILE_NAME);
                    startActivity(new Intent("com.mbrite.patrol.app.action.main"));
                } catch (JSONException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format("JSONException: %s", ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (URISyntaxException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format(getString(R.string.error_site_url_invalid),
                                    Utils.getSiteURI(LoginActivity.this)),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (IOException ex) {
                    mSignInButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            String.format(getString(R.string.error_network_connection_failure),
                                    Utils.getSiteURI(LoginActivity.this)),
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
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void updateSavedFile (String type, String fileName)
                throws JSONException, URISyntaxException, IOException {
            Map<String, String> headers = null;
            if (FileMgr.exists(LoginActivity.this, fileName)) {
                JSONObject savedRoutes = new JSONObject(FileMgr.read(LoginActivity.this, fileName));
                if (savedRoutes.has(Constants.IF_MODIFIED_SINCE) && savedRoutes.has(Constants.IF_NONE_MATCH)) {
                    headers = new HashMap<String, String>();
                    headers.put(Constants.IF_NONE_MATCH, savedRoutes.getString(Constants.IF_NONE_MATCH));
                    headers.put(Constants.IF_MODIFIED_SINCE, savedRoutes.getString(Constants.IF_MODIFIED_SINCE));
                }
            }
            HttpResponse response = RestClient.INSTANCE
                                        .get(LoginActivity.this,
                                                String.format("%s.json", type),
                                                headers);
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    // update file
                    String responseContent = Utils.convertStreamToString(response.getEntity().getContent());
                    JSONArray responseData = new JSONArray(responseContent);
                    JSONObject data = new JSONObject();
                    data.put(type, responseData);
                    Header ifNoneMatch = response.getFirstHeader(Constants.ETAG);
                    Header ifModifiedSince= response.getFirstHeader(Constants.LAST_MODIFIED);
                    if (ifNoneMatch != null && ifModifiedSince != null) {
                        data.put(Constants.IF_NONE_MATCH, ifNoneMatch.getValue());
                        data.put(Constants.IF_MODIFIED_SINCE, ifModifiedSince.getValue());
                    }
                    FileMgr.write(LoginActivity.this, fileName, data.toString());
                    break;
                case 304:
                    // Not Modified
                    break;
                default:
                    throw new HttpResponseException(statusCode,
                            "Error occurred for GET request: " + type);
            }
        }
    }
}



