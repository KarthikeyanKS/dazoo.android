package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class ChannelPageActivityTest extends ActivityInstrumentationTestCase2<HomeActivity>{

	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;

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
    	solo.finishOpenedActivities();
    }
	
	public void test_reminder() throws Exception {
		solo.sleep(TIMEOUT_SMALL);	
		gotoChannelPage();
		
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		solo.sleep(TIMEOUT_SMALL);
		
		solo.waitForView(solo.getView(R.id.element_social_buttons_reminder));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		
		solo.waitForView(solo.getView(R.id.dialog_remove_notification_container));
		solo.clickOnView((solo.getView(R.id.dialog_remove_notification_button_no)));
		
		solo.waitForView(solo.getView(R.id.element_social_buttons_reminder));
		solo.clickOnView(solo.getView(R.id.element_social_buttons_reminder));
		
		solo.waitForView(solo.getView(R.id.dialog_remove_notification_container));
		solo.clickOnView((solo.getView(R.id.dialog_remove_notification_button_yes)));
		
		solo.sleep(TIMEOUT_SMALL);
	}
	
	public void test_share() throws Exception {
		solo.sleep(TIMEOUT_SMALL);
		gotoChannelPage();
		
		solo.clickOnView(solo.getView(R.id.element_social_buttons_share_button_container));
		solo.sleep(TIMEOUT_LARGE);
		solo.goBack();
		solo.sleep(TIMEOUT_SMALL);
		
	}
	
	private void gotoChannelPage() {
		solo.waitForView(solo.getView(R.id.item_container));
		solo.clickOnView(((ListView)solo.getView(R.id.tvguide_table_listview)).getChildAt(0));
		solo.sleep(TIMEOUT_SMALL);
		
		solo.waitForView(solo.getView(R.id.channelpage_channel_icon_container));
		solo.clickOnView(((ListView)solo.getView(R.id.listview)).getChildAt(2));
		solo.waitForView(solo.getView(R.id.block_broadcastpage_poster_container));
	}
 }
