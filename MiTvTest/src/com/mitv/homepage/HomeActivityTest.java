package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;

	private Solo solo;
	public HomeActivityTest() {
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
	
	public void test_swipe_to_switch() throws Exception{
		
		
		solo.waitForView(solo.getView(R.id.tab_tv_guide));
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		
		solo.waitForView(solo.getView(R.id.tvguide_table_listview));
		
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Pelis").isSelected());
		
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Mundial").isSelected());
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Series").isSelected());
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Deportes").isSelected());
		
		
		solo.scrollToSide(solo.LEFT, (float) 0.9);
		assertTrue(solo.getText("Series").isSelected());
		
	}

	public void test_click_to_switch() throws Exception{
		
		solo.clickOnView(solo.getText("Series"));
		assertTrue(solo.getText("Series").isSelected());
		
		
		solo.clickOnView(solo.getText("Deportes"));
		assertTrue(solo.getText("Deportes").isSelected());
		
		
		solo.clickOnView(solo.getText("Pelis"));
		assertTrue(solo.getText("Pelis").isSelected());
		
	}
}
