package com.mbrite.patrol.test.app;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.mbrite.patrol.app.LoginActivity;
import com.mbrite.patrol.app.R;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.test.common.TestUtils;

import org.junit.Assert;

/**
 * Test class for LoginActivity class
 * See {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * <p>To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.mbrite.patrol.test.app.LoginActivityTest \
 * quux.tests/android.test.InstrumentationTestRunner
 * <p/>
 * <p>Individual tests are defined as any method beginning with 'test'.
 * <p/>
 * <p>ActivityInstrumentationTestCase2 allows these tests to run alongside a running
 * copy of the application under inspection. Calling getActivity() will return a
 * handle to this activity (launching it if needed).
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private String test_username = "admin";
    private String test_password = "admin";

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    /**
     * Test to make sure that spinner values are persisted across activity restarts.
     * <p/>
     * <p>Launches the main activity, sets a spinner value, closes the activity, then relaunches
     * that activity. Checks to make sure that the spinner values match what we set them to.
     */
    public void testOnCreate() {
        Activity activity = getActivity();

        TestUtils.setupFakePreferences(activity);

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
                mUsernameView.setText(test_username);
                mPasswordView.setText(test_password);
                mSignInButton.performClick();
            }
        });

        try {
            // wait for UI thread to manipulate
            Thread.sleep(25000);
        } catch (InterruptedException e) {
            return;
        }

        String[] credential = Utils.getSavedUsernameAndPassword(activity);
        Assert.assertArrayEquals(credential, new String[]{test_username, test_password});
        // Close the activity
        activity.finish();
        setActivity(null);
    }
}
