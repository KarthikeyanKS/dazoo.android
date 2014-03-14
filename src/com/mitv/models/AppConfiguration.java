
package com.mitv.models;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;

import com.mitv.Constants;
import com.mitv.activities.FeedActivity;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.base.BaseActivity;
import com.mitv.models.gson.AppConfigurationJSON;
import com.mitv.models.orm.AdzerkFormatForActivityORM;
import com.mitv.models.orm.AdzerkFormatForAndroidGuideORM;
import com.mitv.models.orm.AdzerkFormatORM;
import com.mitv.models.orm.AppConfigurationORM;



public class AppConfiguration 
	extends AppConfigurationJSON
{
	private HashMap<String, Integer> activityNameToCellCountBetweenAdCells;
	
	private void buildActivityToCellCountMap() {
		activityNameToCellCountBetweenAdCells = new HashMap<String, Integer>();
		
		String feedActivityName = Constants.JSON_AND_FRAGMENT_KEY_ACTIVITY;
		String guideFragmentName = Constants.ALL_CATEGORIES_TAG;
		
		activityNameToCellCountBetweenAdCells.put(feedActivityName, feedActivityCellCountBetweenAdCells);
		activityNameToCellCountBetweenAdCells.put(guideFragmentName, guideFragmentCellCountBetweenAdCells);
	}
	
	public Integer getCellCountBetweenAdCellsUsingActivityName(String key) {
		if(activityNameToCellCountBetweenAdCells == null) {
			buildActivityToCellCountMap();
		}
		
		Integer cellCountBetweenAdCells = activityNameToCellCountBetweenAdCells.get(key);
		
		if(cellCountBetweenAdCells == null) {
			cellCountBetweenAdCells = 0;
		}
			
		return cellCountBetweenAdCells;
	}
	
	public AppConfiguration(){}
	
	public AppConfiguration(AppConfigurationORM ormData)
	{
		 this.firstHourOfDay = ormData.getFirstHourOfDay();
		 this.welcomeToast = ormData.getWelcomeToast();
		 this.adsEnabled = ormData.isAdsEnabled();
		 this.adzerkNetworkId = ormData.getAdzerkNetworkId();
		 this.adzerkSiteId = ormData.getAdzerkSiteId();
		 this.adzerkLevel = ormData.getAdzerkLevel();
		 
		 this.adzerkFormats = new ArrayList<Integer>();
		 
		 for(AdzerkFormatORM ormElement : ormData.getAdzerkFormats())
		 {
			 Integer element = ormElement.getValue();
			 
			 this.adzerkFormats.add(element);
		 }
		 
		 this.adzerkFormatsForActivity = new ArrayList<Integer>();
		 
		 for(AdzerkFormatForActivityORM ormElement : ormData.getAdzerkFormatsForActivity())
		 {
			 Integer element = ormElement.getValue();
			 
			 this.adzerkFormatsForActivity.add(element);
		 }
		 
		 this.adzerkFormatsForAndroidGuide = new ArrayList<Integer>();
		 
		 for(AdzerkFormatForAndroidGuideORM ormElement : ormData.getAdzerkFormatsForAndroidGuide())
		 {
			 Integer element = ormElement.getValue();
			 
			 this.adzerkFormatsForAndroidGuide.add(element);
		 }
		 
		 this.googleAnalyticsSampleRate = ormData.getGoogleAnalyticsSampleRate();
		 this.googleAnalyticsEnabled = ormData.isGoogleAnalyticsEnabled();
		 this.googleAnalyticsTrackingId = ormData.getGoogleAnalyticsTrackingId();
		 this.feedActivityCellCountBetweenAdCells = ormData.getFeedActivityCellCountBetweenAdCells();
		 this.guideFragmentCellCountBetweenAdCells = ormData.getHomeActivityCellCountBetweenAdCells();
		 this.facebookAppID = ormData.getFacebookAppID();
		 this.trackingDomain = ormData.getTrackingDomain();
		 
		 buildActivityToCellCountMap();
	}
}