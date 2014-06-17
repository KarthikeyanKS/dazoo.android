package com.mitv.homepage;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class Like_03_03_02_Test extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;
	private Solo solo;
	private boolean loggedin = false;

	public Like_03_03_02_Test() {
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
			solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));   
			loggedin = false;
		}
		solo.finishOpenedActivities();
    }
	
	// 3.3.2 Like a movie from broadcast page
	public void test_02_like_movie() throws Exception{
		checkforLogin();
		//Click on Pelis to select a movie and click like 
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));       
		solo.clickOnView((TextView)solo.getText("Pelis")); 
		validate_like();			
	}
	
	
	// 3.3.3 Like a series from broadcast page
	public void test_03_like_series() throws Exception{
		checkforLogin();
		//Click on series tab and navigate to Series to select a tv series and click like 
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));
		solo.clickOnView((TextView)solo.getText("Series")); 
		validate_like();				
	}
	
	
	// 3.3.4 Like a sports from broadcast page
	public void test_04_like_sports() throws Exception{
		checkforLogin();
		//Click on sports tag to select a sports broadcast and click like    
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));
		solo.clickOnView((TextView)solo.getText("Deportes")); 
		
		solo.sleep(TIMEOUT_SMALL);
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)));
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		
		// verify landing page - broadcast details
		solo.waitForView((solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		
		solo.sleep(TIMEOUT_SMALL);
		TextView broadcastTitleView =  (TextView) solo.getView(R.id.block_broadcastpage_broadcast_extra_tv);
		String broadcastTitle = (String) broadcastTitleView.getText();
		System.out.println("K: broadcastTitle: -    "+broadcastTitle);
		
		// click like button
		solo.waitForView(solo.getView(R.id.element_social_buttons_like_view));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));
		
		// go to profile page and verify the liked movie
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		// click on Me gustan
		assertTrue(solo.waitForView((TextView)solo.getText("Me gustan"))); 
		solo.clickOnView((TextView)solo.getText("Me gustan"));     
		solo.waitForText("Me gustan");
		
		// verify the like list has 1 element
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		ListView lv1 = (ListView) solo.getView(R.id.listview);
		assertTrue(lv1.getChildCount()>=1);
		
		TextView titleToVerifyView = (TextView) solo.getView(R.id.row_likes_text_title_tv);
		String titleToVerify = (String) titleToVerifyView.getText();
		System.out.println("K: Title to verify:--  "+titleToVerify);
		
		Assert.assertTrue("Expected: "+broadcastTitle+" but actual: "+titleToVerify,broadcastTitle.contains(titleToVerify));
		
		//Verify No & Si button
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("No");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
		
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("Si");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		
	
	}
	
	
	// 3.3.5 Like a other from broadcast page
	public void test_05_like_other() throws Exception{
		checkforLogin();
		//Click on other tag to select a other type of broadcast and click like    
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));
		solo.clickOnText(java.util.regex.Pattern.quote("Ni"));       
		validate_like();	
	}
		
	public void checkforLogin() throws InterruptedException{
		solo.clickOnView(solo.getView(R.id.tab_me));
//		solo.waitForView(solo.getView(R.id.home_indicator));		
		
		if(!solo.waitForText(java.util.regex.Pattern.quote("Mi perfil"))){
			solo.clickOnView(solo.getView(R.id.tab_me));
			solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));   
			loggedin = false;
		}
			solo.clickOnView(solo.getView(R.id.tab_tv_guide));
			Thread.sleep(TIMEOUT_SMALL);
			login();
	}
		
	public void login() throws InterruptedException{
		solo.waitForView(solo.getView(R.id.home_indicator));
		
		solo.clickOnText(java.util.regex.Pattern.quote("Mi perfil"));       
		//verify the landing page in mi perfil
		assertTrue(solo.waitForView(solo.getView(R.id.myprofile_login_container_text)));
		
		//Click the signin link Inicia sesion
		solo.clickOnText(java.util.regex.Pattern.quote("Inicia"));       

		//verify the landing page in Inicia sesion
		assertTrue(solo.waitForView(R.id.mitvlogin_login_email_edittext));
		
		// Enter the credentials and click signin button
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "testLike@test.com");
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdfgh");
		solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));

		solo.sleep(TIMEOUT_LARGE);
		// click on Me gustan. verify the like list is empty
		assertTrue(solo.waitForView((TextView)solo.getText("Me gustan"))); 
		solo.clickOnView((TextView)solo.getText("Me gustan"));     
		ListView lv1 = (ListView) solo.getView(R.id.listview);
		solo.sleep(TIMEOUT_SMALL);
		
		while(lv1.getChildCount()>0){
			solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//			solo.clickOnButton("Si");
			solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));

			solo.sleep(TIMEOUT_SMALL);
			System.out.println("K: lv1 child count -- "+lv1.getChildCount());
		}
		
		assertTrue(lv1.getChildCount()==0);
		solo.waitForText("Me gustan");
		
		//Click on Programacion tab to reach home page 
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator)));
		loggedin = true;

	}
	
	public void validate_like(){
		
		solo.sleep(TIMEOUT_SMALL);
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)));
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		
		// verify landing page - broadcast details
		solo.waitForView((solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		TextView broadcastTitleView = (TextView) solo.getView(R.id.block_broadcastpage_broadcast_details_title_tv);
		solo.sleep(TIMEOUT_SMALL);
		String broadcastTitle = (String) broadcastTitleView.getText();
		System.out.println("K: broadcastTitle: -    "+broadcastTitle);
		
		// click like button
		solo.waitForView(solo.getView(R.id.element_social_buttons_like_view));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));
		
		// go to profile page and verify the liked movie
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		// click on Me gustan
		assertTrue(solo.waitForView((TextView)solo.getText("Me gustan"))); 
		solo.clickOnView((TextView)solo.getText("Me gustan"));     
		solo.waitForText("Me gustan");
		
		// verify the like list has 1 element
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		ListView lv1 = (ListView) solo.getView(R.id.listview);
		assertTrue(lv1.getChildCount()>=1);
		
		TextView titleToVerifyView = (TextView) solo.getView(R.id.row_likes_text_title_tv);
		String titleToVerify = (String) titleToVerifyView.getText();
		System.out.println("K: Title to verify:--  "+titleToVerify);
		
		assertTrue(broadcastTitle.equals(titleToVerify));
	
		//Verify No & Si button
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("No");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
		
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("Si");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		solo.sleep(TIMEOUT_SMALL);
		assertTrue(solo.waitForView(solo.getView(R.id.home_indicator))); 

	}
	
//	public void logout(){
//		solo.clickOnView(solo.getView(R.id.tab_me));
//		solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));    
//	}
}

