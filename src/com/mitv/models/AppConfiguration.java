
package com.mitv.models;



import com.mitv.models.gson.AppConfigurationJSON;
import com.mitv.models.orm.AppConfigurationORM;



public class AppConfiguration 
	extends AppConfigurationJSON
{
	public AppConfiguration(){}
	
	public AppConfiguration(AppConfigurationORM ormData)
	{
		 this.firstHourOfDay = ormData.getFirstHourOfDay();
		 this.welcomeToast = ormData.getWelcomeToast();
		 this.adsEnabled = ormData.isAdsEnabled();
		 this.adzerkNetworkId = ormData.getAdzerkNetworkId();
		 this.adzerkSiteId = ormData.getAdzerkSiteId();
		 this.adzerkLevel = ormData.getAdzerkLevel();
		 this.adzerkFormats = ormData.getAdzerkFormats();
		 this.adzerkFormatsForActivity =ormData.getAdzerkFormatsForActivity();
		 this.adzerkFormatsForAndroidGuide = ormData.getAdzerkFormatsForAndroidGuide();
		 this.googleAnalyticsSampleRate = ormData.getGoogleAnalyticsSampleRate();
		 this.googleAnalyticsEnabled = ormData.isGoogleAnalyticsEnabled();
		 this.googleAnalyticsTrackingId = ormData.getGoogleAnalyticsTrackingId();
		 this.activityCellCountBetweenAdCells = ormData.getActivityCellCountBetweenAdCells();
		 this.cellCountBetweenAdCells = ormData.getCellCountBetweenAdCells();
		 this.facebookAppID = ormData.getFacebookAppID();
		 this.trackingDomain = ormData.getTrackingDomain();
	}
}