
package com.mitv.models;



import java.util.ArrayList;

import com.mitv.models.gson.AppConfigurationJSON;
import com.mitv.models.orm.AdzerkFormatForActivityORM;
import com.mitv.models.orm.AdzerkFormatForAndroidGuideORM;
import com.mitv.models.orm.AdzerkFormatORM;
import com.mitv.models.orm.AppConfigurationORM;



public class AppConfiguration 
	extends AppConfigurationJSON
{
	public AppConfiguration()
	{}
	
	
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
		 this.activityCellCountBetweenAdCells = ormData.getActivityCellCountBetweenAdCells();
		 this.cellCountBetweenAdCells = ormData.getCellCountBetweenAdCells();
		 this.facebookAppID = ormData.getFacebookAppID();
		 this.trackingDomain = ormData.getTrackingDomain();
	}
}