package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class Like_03_03_01_Test extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;
	private boolean loggedin = false;
	private Solo solo;
	public Like_03_03_01_Test() {
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
	
	
	// 3.3.1 Prompt for signup on like from activity page
	public void test_prompt_signup_activity_page() throws Exception{
		
		solo.clickOnView(solo.getView(R.id.tab_me));
		checkforLogin();

		// verify singin container is displayed by default.
		solo.clickOnText(java.util.regex.Pattern.quote("Actividad"));   
		assertTrue(solo.waitForView(solo.getView(R.id.activity_not_logged_in_signin_container)));

		solo.clickOnText(java.util.regex.Pattern.quote("Mi perfil"));       

		//verify the landing page in mi perfil
		assertTrue(solo.waitForView(solo.getView(R.id.myprofile_login_container_text)));
		
		//Click the signin link Inicia sesion
		solo.clickOnText(java.util.regex.Pattern.quote("Inicia"));       

		//verify the landing page in Inicia sesion
		assertTrue(solo.waitForView(R.id.mitvlogin_login_email_edittext));
		
		// Enter the credentials and click signin button
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "testactivity@dontdelete.com");
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "asdfgh");
		solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));
		
		solo.sleep(TIMEOUT_LARGE);
		// click on Me gustan. verify the like list is empty
		assertTrue(solo.waitForView((TextView)solo.getText("Me gustan"))); 
		solo.clickOnView((TextView)solo.getText("Me gustan"));     

		ListView lv1 = (ListView) solo.getView(R.id.listview);
		while(lv1.getChildCount()>0){
			solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//			solo.clickOnButton("Si");
			solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));

			solo.sleep(TIMEOUT_SMALL);
			System.out.println("K: lv1 child count -- "+lv1.getChildCount());
		}
		
		assertTrue(lv1.getChildCount()==0);

		solo.waitForText("Me gustan");
		
//		//Click on activity tab and like the first program 
		solo.clickOnText(java.util.regex.Pattern.quote("Actividad"));       

		solo.clickOnView(((TextView)solo.getView(R.id.element_like_image_View)));		
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		// click on Me gustan
		assertTrue(solo.waitForView((TextView)solo.getText("Me gustan"))); 
		solo.clickOnView((TextView)solo.getText("Me gustan"));     
		solo.waitForText("Me gustan");
		
		// verify the like list has 1 element
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		lv1 = (ListView) solo.getView(R.id.listview);
		assertTrue(lv1.getChildCount()>=1);
		
		//Verify No & Si button
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("No");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_no));
		
		solo.clickOnView((TextView)solo.getView(R.id.row_likes_button_tv));
//		solo.clickOnButton("Si");
		solo.clickOnView(solo.getView(R.id.dialog_remove_notification_button_yes));
		
		//Ensure after clicking Si button, the list becomes empty
		lv1 = (ListView) solo.getView(R.id.listview);
		solo.sleep(TIMEOUT_SMALL);
		assertTrue(lv1.getChildCount()==0);
		
		solo.goBack();
		loggedin = true;			
	}
	
	
	// 3.3.1 Prompt for signup on like from broadcast page
	public void test_prompt_signup_broadcast_page() throws Exception{
		solo.clickOnView(solo.getView(R.id.tab_me));
		checkforLogin();

		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		solo.waitForView(solo.getView(R.id.home_indicator));       

		//verify the landing page in Programacion
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.tvguide_table_listview)));
		
		//Click the first program from first channel
		ListView lv1 = (ListView) solo.getView(R.id.tvguide_table_listview);
		assertTrue(lv1.getChildCount()>=0);
		solo.clickInList(0);
		
		solo.waitForView(solo.getView(R.id.listview));
		ListView broadcastList = (ListView) solo.getView(R.id.listview);
		solo.clickOnView(broadcastList.getChildAt(1));
		
		// verify social buttons container in detailed broadcast page
		solo.waitForView(solo.getView(R.id.element_social_buttons_like_view));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));

		// verify cancelar and registrate button
//		solo.clickOnButton("Cancelar");
		solo.clickOnView(solo.getView(R.id.dialog_prompt_signin_button_cancel));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_like_view));
//		solo.clickOnButton(java.util.regex.Pattern.quote("Reg"));   
		solo.clickOnView(solo.getView(R.id.dialog_prompt_signin_button_signin));
		assertTrue(solo.waitForView(solo.getView(R.id.signin_container)));
	}
	

	public void checkforLogin(){
		
		if(!solo.waitForText(java.util.regex.Pattern.quote("Mi perfil"))){
			solo.clickOnView(solo.getView(R.id.tab_me));
			solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));   
			loggedin = false;
			solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		}
	}
}
