
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mitv.models.AppConfiguration;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class AppConfigurationORM
	extends AbstractOrmLiteClassWithAsyncSave<AppConfigurationORM> 
{
	private static final String TAG = AppConfigurationORM.class.getName();
	
	
	
	@DatabaseField()
	private int firstHourOfDay;
	
	@DatabaseField()
	private String welcomeToast;
	
	@DatabaseField()
	private boolean adsEnabled;
	
	@DatabaseField()
	private int adzerkNetworkId;
	
	@DatabaseField()
	private int adzerkSiteId;
	
	@DatabaseField()
	private int adzerkLevel;
	
	@ForeignCollectionField(eager = true)
	private ForeignCollection<AdzerkFormatORM> adzerkFormats;
	
	@ForeignCollectionField(eager = true)
	private ForeignCollection<AdzerkFormatForActivityORM> adzerkFormatsForActivity;
	
	@ForeignCollectionField(eager = true)
	private ForeignCollection<AdzerkFormatForAndroidGuideORM> adzerkFormatsForAndroidGuide;
	
	@DatabaseField()
	private double googleAnalyticsSampleRate;
	
	@DatabaseField()
	private boolean googleAnalyticsEnabled;
	
	@DatabaseField()
	private String googleAnalyticsTrackingId;
	
	@DatabaseField()
	private int feedActivityCellCountBetweenAdCells;
	
	@DatabaseField()
	private int guideFragmentCellCountBetweenAdCells;
	
	@DatabaseField()
	private String facebookAppID;
	
	@DatabaseField()
	private String trackingDomain;
	
	
	
	private AppConfigurationORM()
	{}
	
	
	
	public AppConfigurationORM(AppConfiguration appConfiguration)
	{
		 this.firstHourOfDay = appConfiguration.getFirstHourOfDay();
		 this.welcomeToast = appConfiguration.getWelcomeToast();
		 this.adsEnabled = appConfiguration.isAdsEnabled();
		 this.adzerkNetworkId = appConfiguration.getAdzerkNetworkId();
		 this.adzerkSiteId = appConfiguration.getAdzerkSiteId();
		 this.adzerkLevel = appConfiguration.getAdzerkLevel();
		 
		 try 
		 {
			 this.adzerkFormats = getDao().getEmptyForeignCollection("adzerkFormats");
		 } 
		 catch (SQLException e) 
		 {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }

		 for(Integer element : appConfiguration.getAdzerkFormats())
		 {
			 AdzerkFormatORM ormElement = new AdzerkFormatORM(element);

			 this.adzerkFormats.add(ormElement);
		 }

		 try 
		 {
			 this.adzerkFormatsForActivity = getDao().getEmptyForeignCollection("adzerkFormatsForActivity");
		 } 
		 catch (SQLException e) 
		 {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }

		 for(Integer element : appConfiguration.getAdzerkFormatsForActivity())
		 {
			 AdzerkFormatForActivityORM ormElement = new AdzerkFormatForActivityORM(element);

			 this.adzerkFormatsForActivity.add(ormElement);
		 }

		 try 
		 {
			 this.adzerkFormatsForAndroidGuide = getDao().getEmptyForeignCollection("adzerkFormatsForAndroidGuide");
		 } 
		 catch (SQLException e) 
		 {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		 
		 for(Integer element : appConfiguration.getAdzerkFormatsForAndroidGuide())
		 {
			 AdzerkFormatForAndroidGuideORM ormElement = new AdzerkFormatForAndroidGuideORM(element);
			 
			 this.adzerkFormatsForAndroidGuide.add(ormElement);
		 }
		 
		 this.googleAnalyticsSampleRate = appConfiguration.getGoogleAnalyticsSampleRate();
		 this.googleAnalyticsEnabled = appConfiguration.isGoogleAnalyticsEnabled();
		 this.googleAnalyticsTrackingId = appConfiguration.getGoogleAnalyticsTrackingId();
		 this.feedActivityCellCountBetweenAdCells = appConfiguration.getFeedActivityCellCountBetweenAdCells();
		 this.guideFragmentCellCountBetweenAdCells = appConfiguration.getGuideFragmentCellCountBetweenAdCells();
		 this.facebookAppID = appConfiguration.getFacebookAppID();
		 this.trackingDomain = appConfiguration.getTrackingDomain();
	}
	
	
	
	
	public static AppConfiguration getAppConfiguration() 
	{
		AppConfiguration data;
		
		AppConfigurationORM ormData = new AppConfigurationORM().getAppConfigurationORM();
		
		if(ormData != null)
		{
			data = new AppConfiguration(ormData);
			
			return data;
		}
		else
		{
			return null;
		}
	}
	

	
	private AppConfigurationORM getAppConfigurationORM() 
	{
		List<AppConfigurationORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<AppConfigurationORM, Integer> queryBuilder = (QueryBuilder<AppConfigurationORM, Integer>) getDao().queryBuilder();
			
			data = (List<AppConfigurationORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		if(data != null && data.isEmpty() == false)
		{
			return data.get(0);
		}
		else
		{
			return null;
		}
	}


	
	public int getFirstHourOfDay() {
		return firstHourOfDay;
	}



	public String getWelcomeToast() {
		return welcomeToast;
	}



	public boolean isAdsEnabled() {
		return adsEnabled;
	}



	public int getAdzerkNetworkId() {
		return adzerkNetworkId;
	}



	public int getAdzerkSiteId() {
		return adzerkSiteId;
	}



	public int getAdzerkLevel() {
		return adzerkLevel;
	}



	public ForeignCollection<AdzerkFormatORM> getAdzerkFormats() 
	{
		return adzerkFormats;
	}



	public ForeignCollection<AdzerkFormatForActivityORM> getAdzerkFormatsForActivity() 
	{
		return adzerkFormatsForActivity;
	}



	public ForeignCollection<AdzerkFormatForAndroidGuideORM> getAdzerkFormatsForAndroidGuide() 
	{
		return adzerkFormatsForAndroidGuide;
	}



	public double getGoogleAnalyticsSampleRate() {
		return googleAnalyticsSampleRate;
	}



	public boolean isGoogleAnalyticsEnabled() {
		return googleAnalyticsEnabled;
	}



	public String getGoogleAnalyticsTrackingId() {
		return googleAnalyticsTrackingId;
	}


	public int getFeedActivityCellCountBetweenAdCells() {
		return feedActivityCellCountBetweenAdCells;
	}


	public int getHomeActivityCellCountBetweenAdCells() {
		return guideFragmentCellCountBetweenAdCells;
	}



	public String getFacebookAppID() {
		return facebookAppID;
	}



	public String getTrackingDomain() {
		return trackingDomain;
	}
}
