package com.mitv;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.mitv.R;
import com.mitv.activities.SplashScreenActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.mitv.homepage.HomeActivityTest \
 * com.mitv.tests/android.test.InstrumentationTestRunner
 */
public class SplashScreenActivityTest extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
	final int TIMEOUT_SMALL = 1000;
	final int TIMEOUT_LARGE = 8000;
	
	private Solo solo;
    public SplashScreenActivityTest() {
        super(SplashScreenActivity.class);
    }

    
    @Override
    protected void setUp() throws Exception {
    	Config config = new Config();
    	config.shouldScroll = true;
    	config.timeout_small = TIMEOUT_SMALL;
    	config.timeout_large = TIMEOUT_LARGE;
    	config.useJavaScriptToClickWebElements = true;
    	
    	solo = new Solo(getInstrumentation(), getActivity());
    	//getActivity();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	solo.finishOpenedActivities();
    }
  
    
    public void test_startup() throws Exception {
    	solo.sleep(TIMEOUT_SMALL);
    	solo.waitForView(solo.getView(R.id.home_container));
    	solo.sleep(TIMEOUT_SMALL);
    	
    }
    
}
