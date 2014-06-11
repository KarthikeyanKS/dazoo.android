package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class Reminder_03_04_Test extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;
	private boolean loggedin = false;

	private Solo solo;
	public Reminder_03_04_Test() {
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
			logout();
		}
    	solo.finishOpenedActivities();
    }
	
	// 3.4.1 Reminder from broadcast page ...  Usecase 3.4.5 is covered in all of these tests
	public void test_01_reminder_for_broadcast() throws Exception{
		solo.clickOnView(solo.getView(R.id.tab_me));
		checkforLogin();
		//		login();
		//Click on Pelis to select a movie broadcast 
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		solo.waitForView(solo.getView(R.id.home_indicator));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));       
		solo.clickOnView((TextView)solo.getText("Pelis")); 
		
		validate_reminder();			
	}
	
	// 3.4.2 Reminder for upcoming shows -series
	public void test_02_reminder_for_series() throws Exception{

		//		login();
		//Click on Series to select a movie broadcast 
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));       
		solo.clickOnView((TextView)solo.getText("Series")); 
		
		validate_reminder();			
	}
	
	// 3.4.4 Reminder from activity
	public void test_04_reminder_from_activity() throws Exception{
		solo.clickOnView(solo.getView(R.id.tab_me));
		checkforLogin();
		login();
		//Click on activity to select the first in list and set reminder 
		solo.clickOnView(solo.getView(R.id.tab_activity));
		solo.searchText("Jueves");     

		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		
		//Get broadcast title and time 
		TextView broadcastTitleView = (TextView) solo.getView(R.id.block_feed_liked_title_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastTitle = (String) broadcastTitleView.getText();
		System.out.println("K: broadcastTitle: -    "+broadcastTitle);
		
		TextView broadcastTimeView = (TextView) solo.getView(R.id.block_feed_liked_time_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastTime = (String) broadcastTimeView.getText();
		System.out.println("K: Time : -    "+broadcastTime);
		
		TextView broadcastChannelView = (TextView) solo.getView(R.id.block_feed_liked_channel_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastChannel = (String) broadcastChannelView.getText();
		System.out.println("K: Time : -    "+broadcastChannel);
		
		
		//Go to tab_me to validate the reminder
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		// click on Recordatorios
		assertTrue(solo.waitForView((TextView)solo.getText("Recordatorios"))); 
		solo.clickOnView((TextView)solo.getText("Recordatorios"));     
		solo.waitForText("Recordatorios");
		
		// verify the reminder list has 1 element
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		ListView lv1 = (ListView) solo.getView(R.id.listview);
		assertTrue(lv1.getChildCount()>=1);
		
		TextView titleToVerifyView = (TextView) solo.getView(R.id.row_reminders_text_title_tv);
		String titleToVerify = (String) titleToVerifyView.getText();
		System.out.println("K: Title to verify:--  "+titleToVerify);
		
		TextView timeToVerifyView = (TextView) solo.getView(R.id.row_reminders_text_time_tv);
		String timeToVerify = (String) timeToVerifyView.getText();
		System.out.println("K: Time to verify:--  "+timeToVerify);
		
		TextView channelToVerifyView = (TextView) solo.getView(R.id.row_reminders_text_channel_tv);
		String channelToVerify = (String) channelToVerifyView.getText();
		System.out.println("K: Time to verify:--  "+channelToVerify);
		
		assertTrue(broadcastTitle.equals(titleToVerify));
		assertTrue(broadcastTime.contains(timeToVerify));
		assertTrue(broadcastChannel.contains(channelToVerify));
	
		//Verify No & Si button
		solo.clickOnView((TextView)solo.getView(R.id.row_reminders_notification_iv));
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
		
		solo.clickOnView((TextView)solo.getView(R.id.row_reminders_notification_iv));
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		
		solo.goBack();
		logout();
	}
		
	public void login(){
		solo.clickOnView(solo.getView(R.id.tab_me));		
		solo.clickOnText(java.util.regex.Pattern.quote("Mi perfil"));       
		//verify the landing page in mi perfil
		assertTrue(solo.waitForView(solo.getView(R.id.myprofile_login_container_text)));
		
		//Click the signin link Inicia sesion
		solo.clickOnView(solo.getView(R.id.myprofile_login_container_text));       

		//verify the landing page in Inicia sesion
		assertTrue(solo.waitForView(R.id.mitvlogin_login_email_edittext));
		
		// Enter the credentials and click signin button
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "testLike@test.com");
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdfgh");
		solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
		loggedin = true;
	}
	
	public void validate_reminder(){
		//click Manana
		solo.clickOnView(solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(2);
		solo.sleep(TIMEOUT_SMALL);
		solo.clickOnText(java.util.regex.Pattern.quote("Pelis"));

		assertTrue(solo.waitForView((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)));
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		
		// verify landing page - broadcast details
		solo.waitForView((solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		TextView broadcastTitleView = (TextView) solo.getView(R.id.block_broadcastpage_broadcast_details_title_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastTitle = (String) broadcastTitleView.getText();
		System.out.println("K: broadcastTitle: -    "+broadcastTitle);
		
		TextView broadcastTimeView = (TextView) solo.getView(R.id.block_broadcastpage_broadcast_details_time_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastTime = (String) broadcastTimeView.getText();
		System.out.println("K: Time : -    "+broadcastTime);
		
		// click reminder button
		solo.waitForView(solo.getView(R.id.element_social_buttons_reminder));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		//Verify No & Si button
				solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
				solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
				solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
				solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));

		
		// go to profile page and verify the reminder
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		// click on Recordatorios
		assertTrue(solo.waitForView((TextView)solo.getText("Recordatorios"))); 
		solo.clickOnView((TextView)solo.getText("Recordatorios"));     
		solo.waitForText("Recordatorios");
		
		// verify the reminder list has 1 element
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		ListView lv1 = (ListView) solo.getView(R.id.listview);
		assertTrue(lv1.getChildCount()>=1);
		
		TextView titleToVerifyView = (TextView) solo.getView(R.id.row_reminders_text_title_tv);
		String titleToVerify = (String) titleToVerifyView.getText();
		System.out.println("K: Title to verify:--  "+titleToVerify);
		
		TextView timeToVerifyView = (TextView) solo.getView(R.id.row_reminders_text_time_tv);
		String timeToVerify = (String) timeToVerifyView.getText();
		System.out.println("K: Time to verify:--  "+timeToVerify);
		
		assertTrue(broadcastTitle.equals(titleToVerify));
		assertTrue(broadcastTime.contains(timeToVerify));
	
		//Verify No & Si button
		solo.clickOnView((TextView)solo.getView(R.id.row_reminders_notification_iv));
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
		
		solo.clickOnView((TextView)solo.getView(R.id.row_reminders_notification_iv));
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		
		solo.goBack();

	}
	
	public void checkforLogin(){
		if(!solo.waitForText(java.util.regex.Pattern.quote("Mi perfil"))){
			logout();  
			loggedin = false;
			solo.clickOnView(solo.getView(R.id.tab_me));
		}
	}
	 
	public void logout(){
		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));  
		loggedin=false;
	}
}

