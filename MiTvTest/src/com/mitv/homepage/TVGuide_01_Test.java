package com.mitv.homepage;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class TVGuide_01_Test extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;

	private Solo solo;
	public TVGuide_01_Test() {
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
	}
	
	@Override
    protected void tearDown() throws Exception {
    	solo.finishOpenedActivities();
    }
	
	
// 1.1, 1.3
	public void test_overview_browseByDay() throws Exception{
		
		for(int i=1;i<8;i++){
		solo.waitForView(solo.getView(R.id.tab_tv_guide));
		solo.clickOnView(solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(i);
		solo.clickOnText(java.util.regex.Pattern.quote("Todos"));
		assertTrue(solo.waitForView(solo.getView(R.id.tvguide_table_listview)));
		perform();
		}
	}
	
// 1.4
	public void test_channelOverview() throws Exception{
		
		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		solo.sleep(TIMEOUT_LARGE);
		solo.waitForView(solo.getView(R.id.tvguide_table_listview));
		solo.clickInList(2);
		solo.sleep(TIMEOUT_SMALL);
		
		//Full broadcast view
		assertTrue(solo.waitForView(solo.getView(R.id.listview)));
		
		//Drilling down to broadcast-in-air
		solo.clickOnView(solo.getView(R.id.channelpage_list_item_title_tv));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_block_container_layout)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.goBack();
		
		//changing date
		solo.clickOnView(solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(2);
		assertTrue(solo.waitForView(solo.getView(R.id.listview)));
	
	}
	
// 1.5.1
	public void test_movie_broadcast() throws Exception{

		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
		solo.waitForView(solo.getView(R.id.tvguide_table_listview));
		solo.clickOnText(java.util.regex.Pattern.quote("Pelis"));
		
		//Click the first movie result
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		solo.sleep(TIMEOUT_SMALL);
		
		//verify the movie details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		solo.goBack();
		
		//changing date
		solo.clickOnView((TextView)solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(3);
		
		solo.clickOnText(java.util.regex.Pattern.quote("Pelis"));
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		
		solo.sleep(TIMEOUT_SMALL);		
		//verify the movie details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		solo.goBack();
	}
	

// 1.5.2.1
	public void test_series_broadcast() throws Exception{

		solo.waitForView(solo.getView(R.id.tab_tv_guide));
		solo.waitForView(solo.getView(R.id.tvguide_table_listview));
		solo.clickOnText(java.util.regex.Pattern.quote("Series"));
		solo.sleep(TIMEOUT_SMALL);

		//Click the first series result
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(1));
		solo.sleep(TIMEOUT_SMALL);
		
		//verify the series details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		solo.goBack();
		
		//changing date
		solo.clickOnView(solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(3);
		
		solo.clickOnText(java.util.regex.Pattern.quote("Series"));
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		
		solo.sleep(TIMEOUT_SMALL);		
		//verify the series details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		solo.goBack();
	
	}
	
// 1.5.2.2 - shore more on tv series
	public void test_series_showmore() throws Exception{

		solo.waitForView(solo.getView(R.id.tab_tv_guide));
		solo.waitForView(solo.getView(R.id.tvguide_table_listview));
		solo.clickOnText(java.util.regex.Pattern.quote("Series"));
		solo.sleep(TIMEOUT_SMALL);

		//Click the first series result
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		solo.sleep(TIMEOUT_SMALL);
		
		//verify the series details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.scrollDown();
		assertTrue(solo.waitForView((TextView)solo.getView(R.id.block_tripple_broadcast_more_textview)));
		
		//clicking Ver mas Proximos
		solo.clickOnText("Ver m");
		solo.scrollDown();
		
		//verifying more page
		//Clicking the 4th in list
		solo.clickInList(4);
		//verify the series details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.scrollDown();		
			
	}
	
// 1.5.3 , 1.5.5 - Live sport event broadcast details, more events
	public void test_sport_broadcast() throws Exception{

		solo.waitForView(solo.getView(R.id.home_indicator));
		solo.waitForView(solo.getView(R.id.tab_tv_guide));
		solo.clickOnView(solo.getView(R.id.layout_actionbar_dropdown_list_date_header_name));
		solo.clickInList(2);
		
		solo.clickOnText(java.util.regex.Pattern.quote("Deportes"));
		solo.sleep(TIMEOUT_SMALL);

		//Click the first sports result
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(0));
		solo.sleep(TIMEOUT_SMALL);
		
		//verify the sports details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.scrollDown();
		assertTrue(solo.waitForView((TextView)solo.getView(R.id.block_tripple_broadcast_more_textview)));	
	}
	
// 1.5.4 - "other" broadcast details
	public void test_other_broadcast() throws Exception{

		solo.waitForView(solo.getView(R.id.home_indicator));
//			assertTrue(solo.searchText("Ni"));
		solo.clickOnText(java.util.regex.Pattern.quote("Ni"));       
		solo.sleep(TIMEOUT_SMALL);

		//Click the second other result
		solo.clickOnView(((ListView)solo.getView(R.id.fragment_tvguide_type_tag_listview)).getChildAt(1));
		solo.sleep(TIMEOUT_SMALL);
		
		//verify "other" details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.scrollDown();
		assertTrue(solo.waitForView((TextView)solo.getView(R.id.block_tripple_broadcast_more_textview)));
		
		//clicking Ver mas Proximos
		solo.clickOnText("Ver m");
		solo.scrollDown();
		
		//verifying more page
		//Clicking the 4th in list
		solo.clickInList(4);
		//verify "other" details
		assertTrue(solo.waitForView(solo.getView(R.id.block_broadcastpage_broadcast_details_container)));
		assertTrue(solo.waitForView(solo.getView(R.id.broacastpage_repetitions)));
		
		solo.scrollDown();		
			
	}
	
	public void perform() throws Exception{
		
		solo.clickOnText(java.util.regex.Pattern.quote("Pelis"));
		assertTrue(solo.getText("Pelis").isSelected());
		
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Mundial").isSelected());
		
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Series").isSelected());
		
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		assertTrue(solo.getText("Deportes").isSelected());
		solo.scrollToSide(solo.RIGHT, (float) 0.9);
		
		
		solo.scrollToSide(solo.LEFT, (float) 0.9);
		solo.scrollToSide(solo.LEFT, (float) 0.9);
		assertTrue(solo.getText("Series").isSelected());
		
		solo.clickOnText(java.util.regex.Pattern.quote("Todos"));
		assertTrue(solo.getText("Todos").isSelected());
		
	}
}
