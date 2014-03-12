package com.mbrite.patrol.test.app;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import android.widget.*;
import com.mbrite.patrol.app.LoginActivity;
import com.mbrite.patrol.app.R;

/**
 * Test class for LoginActivity class
 * See {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 *
 * <p>To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.mbrite.patrol.test.app.LoginActivityTest \
 * quux.tests/android.test.InstrumentationTestRunner
 *
 * <p>Individual tests are defined as any method beginning with 'test'.
 *
 * <p>ActivityInstrumentationTestCase2 allows these tests to run alongside a running
 * copy of the application under inspection. Calling getActivity() will return a
 * handle to this activity (launching it if needed).
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    public LoginActivityTest() {
        super("com.mbrite.patrol.app", LoginActivity.class);
    }

    /**
     * Test to make sure that spinner values are persisted across activity restarts.
     *
     * <p>Launches the main activity, sets a spinner value, closes the activity, then relaunches
     * that activity. Checks to make sure that the spinner values match what we set them to.
     */
    public void testOnCreate() {
        Activity activity = getActivity();

        final EditText mUsernameView = (EditText) activity.findViewById(R.id.username);
        final EditText mPasswordView = (EditText) activity.findViewById(R.id.password);
        final Button mSignInButton = (Button) activity.findViewById(R.id.sign_in_button);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Attempts to manipulate the UI must be performed on a UI thread.
                // Calling this outside runOnUiThread() will cause an exception.
                //
                // You could also use @UiThreadTest, but activity lifecycle methods
                // cannot be called if this annotation is used.
                mUsernameView.setText("admin");
                mPasswordView.setText("admin");
                mSignInButton.performClick();
            }
        });

        try {
            // wait for UI thread to manipulate
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            return;
        }

        // Close the activity
        activity.finish();
        setActivity(null);
    }
}
