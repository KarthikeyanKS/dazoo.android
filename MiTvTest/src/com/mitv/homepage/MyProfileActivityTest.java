package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.mitv.R;
import com.mitv.activities.UserProfileActivity;
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
public class MyProfileActivityTest extends ActivityInstrumentationTestCase2<UserProfileActivity> {
	final int TIMEOUT_SMALL = 1000;
	final int TIMEOUT_LARGE = 8000;
	
	private Solo solo;
    public MyProfileActivityTest() {
        super(UserProfileActivity.class);
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
//    	solo.sleep(TIMEOUT_LARGE);
    	solo.sleep(TIMEOUT_SMALL);
    	
    	solo.waitForView(solo.getView(R.id.tab_me));
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(R.id.myprofile_login_container_text);
    	solo.clickOnView(solo.getView(R.id.myprofile_login_container_text));
    	
    	solo.sleep(TIMEOUT_SMALL);
    	
    	solo.waitForView(R.id.mitvlogin_login_email_edittext);
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "test1@test.se");
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdqwe");
    	solo.sleep(TIMEOUT_SMALL);
    	solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
    	
    	solo.waitForView(solo.getView(R.id.myprofile_header_container));
    	solo.sleep(TIMEOUT_SMALL);
    }
    
    public void test_logout() throws Exception {
    	solo.sleep(TIMEOUT_SMALL);
    	solo.waitForView(solo.getView(R.id.tab_me));
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(R.id.myprofile_logout_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_logout_container));
    	
    	solo.sleep(TIMEOUT_SMALL);
    	
    	solo.waitForView(solo.getView(R.id.home_container));
    	solo.sleep(TIMEOUT_SMALL);
    }
}
