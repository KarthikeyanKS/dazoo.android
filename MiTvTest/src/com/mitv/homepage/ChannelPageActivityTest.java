package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class ChannelPageActivityTest extends ActivityInstrumentationTestCase2<HomeActivity>{

	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;
	private boolean loggedin = false;

	private Solo solo;
	public ChannelPageActivityTest() {
		super(HomeActivity.class);
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
			solo.clickOnView(solo.getView(R.id.tab_me));
			solo.waitForView(R.id.myprofile_logout_container);
			solo.clickOnView(solo.getView(R.id.myprofile_logout_container));    	
			solo.waitForView(solo.getView(R.id.activity_container));
			loggedin = false;
		}
		solo.finishOpenedActivities();
	}

	public void test_reminder() throws Exception {
			
		gotoChannelPage();
		//TODO Loop through all programs
		solo.clickOnView(((ListView)solo.getView(R.id.listview)).getChildAt(2)); 	//Go to program nr2 (3rd item in the list)
		solo.waitForView(solo.getView(R.id.broadcast_scroll));

		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		

		solo.waitForView(solo.getView(R.id.element_social_buttons_reminder));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));

		solo.waitForView(solo.getView(R.id.dialog_remove_notification_container));
		solo.clickOnView((solo.getView(R.id.dialog_remove_notification_button_no)));

		

		String title = (String)((TextView)solo.getView(R.id.block_broadcastpage_broadcast_details_title_tv)).getText(); //Get the title of the program
		solo.waitForView(solo.getView(R.id.tab_me));
		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.waitForView(solo.getView(R.id.myprofile_reminders_container));
		solo.clickOnView(solo.getView(R.id.myprofile_reminders_container));
		solo.waitForText(title);
		solo.clickOnText(title);

		solo.waitForView(solo.getView(R.id.element_social_buttons_reminder));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));

		solo.waitForView(solo.getView(R.id.dialog_remove_notification_container));
		solo.clickOnView((solo.getView(R.id.dialog_remove_notification_button_yes)));

		
	}

	public void test_share() throws Exception {
		
		gotoChannelPage();
		//TODO Loop through all programs
		solo.clickOnView(((ListView)solo.getView(R.id.listview)).getChildAt(2)); 	//Go to program nr2 (3rd item in the list)
		solo.waitForView(solo.getView(R.id.broadcast_scroll));

		solo.clickOnView(solo.getView(R.id.element_social_buttons_share_button_container));

		solo.waitForText("Bluetooth");
		solo.waitForText("Email");
		solo.waitForText("Facebook");
		solo.waitForText("Gmail");
		solo.waitForText("Google+");
		solo.waitForText("Hangouts");
		solo.waitForText("Messaging");
		solo.waitForText("Twitter");

		solo.goBack();
		solo.waitForView(solo.getView(R.id.broadcast_scroll));
				
	}

	public void test_like() throws Exception {
		

		gotoChannelPage();
		//TODO Loop through all programs
		solo.clickOnView(((ListView)solo.getView(R.id.listview)).getChildAt(2)); 	//Go to program nr2 (3rd item in the list)
		solo.waitForView(solo.getView(R.id.broadcast_scroll));

		String title = (String)((TextView)solo.getView(R.id.block_broadcastpage_broadcast_details_title_tv)).getText(); //Get the title of the program

		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));
		solo.waitForView(solo.getView(R.id.dialog_prompt_signin_button_container));
		solo.clickOnView(solo.getView(R.id.dialog_prompt_signin_button_signin));

		login();

		solo.waitForText(title);
		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));
		solo.waitForView(solo.getView(R.id.dialog_remove_notification_button_container));
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));

		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.waitForView(solo.getView(R.id.myprofile_header_container));
		solo.clickOnView(solo.getView(R.id.myprofile_likes_container));

		solo.waitForText(title);
		
	}

	private void gotoChannelPage() {
		solo.waitForView(solo.getView(R.id.item_container));
		solo.clickOnView(((ListView)solo.getView(R.id.tvguide_table_listview)).getChildAt(0));
		

		solo.waitForView(solo.getView(R.id.channelpage_channel_icon_container));
	}

	private void login() {
		solo.waitForView(R.id.signin_login_btn);
		solo.clickOnView(solo.getView(R.id.signin_login_btn));  	
		   	
		solo.waitForView(R.id.mitvlogin_login_email_edittext);
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "test1@test.se");
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdqwe");
		solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
		
		loggedin = true;
	}
}
