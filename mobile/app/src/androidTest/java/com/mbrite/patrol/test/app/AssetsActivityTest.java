package com.mbrite.patrol.test.app;

import android.test.ActivityInstrumentationTestCase2;

import com.mbrite.patrol.app.*;
import com.robotium.solo.Solo;

/**
 * Test class for AssetsActivity
 */
public class AssetsActivityTest extends ActivityInstrumentationTestCase2<AssetsActivity> {
    private Solo solo;

    public AssetsActivityTest() {
        super(AssetsActivity.class);
    }
//
//    @Override
//    public void tearDown() throws Exception {
//        //tearDown() is run after a test case has finished.
//        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
//        solo.finishOpenedActivities();
//    }
//
//    @Override
//    public void setUp() throws Exception {
//        //setUp() is run before a test case is started.
//        //This is where the solo object is created.
//        solo = new Solo(getInstrumentation(), getActivity());
//    }
//
//    public void testOnClickAsset() {
//
//    }
}
