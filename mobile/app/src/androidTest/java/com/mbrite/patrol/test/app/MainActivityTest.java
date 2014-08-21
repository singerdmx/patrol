package com.mbrite.patrol.test.app;

import android.test.ActivityInstrumentationTestCase2;

import com.mbrite.patrol.app.MainActivity;
import com.mbrite.patrol.app.R;
import com.robotium.solo.Solo;

/**
 * Test class for MainActivity.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    @Override
    public void setUp() throws Exception {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testOnClickRoute() {
        // Click on the first list line
        solo.clickInList(1);
        getInstrumentation().waitForIdleSync();

        // Check dialog title.
        assertTrue("Could not find the confirm dialog!",
                solo.searchText(getActivity().getString(R.string.confirm_route)));

        //Clicks on 'No' button in the dialog to go back to main activity
        solo.clickOnText(getActivity().getString(R.string.no));
    }

    public void testOnClickSettingsMenuItem() {
        solo.clickOnMenuItem(getActivity().getString(R.string.settings));
        getInstrumentation().waitForIdleSync();
        solo.assertCurrentActivity("Expected Settings activity", "Settings");
        solo.goBack();
    }

    public void testOnClickLogoutMenuItem() {
        solo.clickOnMenuItem(getActivity().getString(R.string.logout));
        getInstrumentation().waitForIdleSync();
        solo.hideSoftKeyboard();
        solo.assertCurrentActivity("Expected Login activity", "Login");
        solo.goBack();
    }
}
