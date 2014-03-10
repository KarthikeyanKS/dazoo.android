package com.mitv.homepage;

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
public class HomeActivityTest extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
	final int TIMEOUT_SMALL = 1000;
	final int TIMEOUT_LARGE = 8000;
	
	private Solo solo;
    public HomeActivityTest() {
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
  
    
    public void test_login() throws Exception {
    	
    	solo.sleep(TIMEOUT_LARGE);
    	
    	solo.waitForView(solo.getView(R.id.tab_me));
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(solo.getView(R.id.myprofile_login_container_text));
    	solo.clickOnView(solo.getView(R.id.myprofile_login_container_text));
    	
    	solo.waitForView(solo.getView(R.id.mitvlogin_login_email_edittext));
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "test1@test.se");
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdqwe");
    	solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
    	
    	solo.sleep(TIMEOUT_LARGE);
    	
    	
    }
    
}
