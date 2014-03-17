package com.mitv.homepage;

import java.util.Random;

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
	final private int TIMEOUT_SMALL = 1000;
	final private int TIMEOUT_LARGE = 8000;
	
	private Solo solo;
	private boolean loggedin = false;
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
    	if(loggedin) {
			logout();
		}
    	solo.finishOpenedActivities();
    }
    
    public void test_create_new_account() throws Exception {
    	final String email = generateEmail();
    	final String password = "asdqwe";
    	solo.waitForView(R.id.tab_me);
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(R.id.myprofile_login_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_signup_container));
    	
    	solo.waitForView(R.id.signin_container);
    	solo.clickOnView(solo.getView(R.id.signin_signup_email_container));
    	solo.waitForView(R.id.signin_container);
    	
    	solo.typeText((EditText)solo.getView(R.id.signup_firstname_edittext), "TestFirstName");
    	solo.typeText((EditText)solo.getView(R.id.signup_lastname_edittext), "TestLastName");
    	solo.typeText((EditText)solo.getView(R.id.signup_email_edittext), email);
    	solo.typeText((EditText)solo.getView(R.id.signup_password_edittext), password);
    	solo.clickOnView(solo.getView(R.id.signup_register_button));
    	
    	solo.waitForView(R.id.activity_container);
    	loggedin = true;
   
    	//Logout and test if we can login again.
    	logout();
    	solo.sleep(TIMEOUT_SMALL);
    	solo.waitForView(R.id.myprofile_login_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_login_container));
    	solo.waitForView(R.id.mitvlogin_login_email_edittext);
    	login(email, password);
    }
    /*
    public void test_login_and_out_new_account() throws Exception {
    	
    	solo.waitForView(R.id.tab_me);
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(R.id.myprofile_login_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_login_container));

    	solo.waitForView(R.id.mitvlogin_login_email_edittext);
    	login(email, password);
    	
    	solo.waitForView(R.id.tab_me);
    	logout();
    }
    */
    public void test_login_and_out_old_account() throws Exception {
    	
    	solo.waitForView(R.id.tab_me);
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	
    	solo.waitForView(R.id.myprofile_login_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_login_container));
    	
    	
    	solo.waitForView(R.id.mitvlogin_login_email_edittext);
    	login("test1@test.se", "asdqwe");

   
    	solo.waitForView(R.id.tab_me);
    	logout();
    }
    
    private String generateEmail() {
    	final String alphabet = "0123456789abcdefghijlkmnopcrstuvwxyz";
        final int N = alphabet.length();
        StringBuilder email = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            email.append(alphabet.charAt(r.nextInt(N)));
        }
        email.append("Test@test.se");
        return email.toString();
    }
    
    private void login(String email, String password) {
    	
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), email);
    	solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), password);
    	solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
    	
    	solo.waitForView(R.id.myprofile_header_container);
    	loggedin = true;
    }
    
    private void logout() {
    	solo.clickOnView(solo.getView(R.id.tab_me));
    	solo.waitForView(R.id.myprofile_logout_container);
    	solo.clickOnView(solo.getView(R.id.myprofile_logout_container));

    	solo.waitForView(R.id.activity_container);
    	loggedin = false;
    }
}
