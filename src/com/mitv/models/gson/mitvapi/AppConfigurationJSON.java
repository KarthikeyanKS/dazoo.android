
package com.mitv.models.gson.mitvapi;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;



public class AppConfigurationJSON
	implements JsonDeserializer<AppConfigurationJSON>
{
	protected int firstHourOfDay;
	protected String welcomeToast;

	/* Ad configuration */
	protected boolean adsEnabled;
	protected int adzerkNetworkId;
	protected int adzerkSiteId;
	protected int adzerkLevel;
	protected List<Integer> adzerkFormats;
	protected List<Integer> adzerkFormatsForActivity;
	protected List<Integer> adzerkFormatsForAndroidGuide;
	
	
	/* Google Analytics configuration */
	protected double googleAnalyticsSampleRate;
	protected boolean googleAnalyticsEnabled;
	protected String googleAnalyticsTrackingId;
	
	/* Disqus comments configuration */
	protected boolean areDisqusCommentsEnabled;
	
	/* Cell count configuration */
	protected int feedActivityCellCountBetweenAdCells;
	protected int guideFragmentCellCountBetweenAdCells;
	
	
	/* Facebook configuration */
	protected String facebookAppID;
	
	
	/* Tracking configuration */
	protected String trackingDomain;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public AppConfigurationJSON()
	{
		this.adzerkFormatsForAndroidGuide = new ArrayList<Integer>();
		this.adzerkFormatsForActivity = new ArrayList<Integer>();
		this.adzerkFormats = new ArrayList<Integer>();
	}

	
	
	@Override
	public AppConfigurationJSON deserialize(
			JsonElement jsonTopElement, 
			Type type,
			JsonDeserializationContext jsonDeserializationContext) 
					throws JsonParseException 
	{
		JsonObject jsonObject = jsonTopElement.getAsJsonObject();
		
		JsonElement jsonElement = jsonObject.get("adzerkNetworkId");
		
		adzerkNetworkId = jsonElement.getAsInt();
		
		jsonElement = jsonObject.get("android.googleAnalyticsSampleRate");
		
		googleAnalyticsSampleRate = jsonElement.getAsInt();
		
		jsonElement = jsonObject.get("android.googleAnalyticsEnabled");
		
		googleAnalyticsEnabled = jsonElement.getAsBoolean();

		jsonElement = jsonObject.get("android.adsEnabled");
		
		adsEnabled = jsonElement.getAsBoolean();
		
		jsonElement = jsonObject.get("android.activity.cellCountBetweenAdCells");
		
		feedActivityCellCountBetweenAdCells = jsonElement.getAsInt();
		
		jsonElement = jsonObject.get("adzerkSiteId");
		
		adzerkSiteId = jsonElement.getAsInt();
		
		JsonElement jsonAndroidGuideAdzerkFormatsElement = jsonObject.get("android.guide.adzerkFormats");
		
		boolean isJsonArray = jsonAndroidGuideAdzerkFormatsElement.isJsonArray();
		
		if(isJsonArray)
		{
			JsonArray elementAsArray = jsonAndroidGuideAdzerkFormatsElement.getAsJsonArray();
			
			for(JsonElement element : elementAsArray)
			{
				int adzerkFormatElementAsInt = element.getAsInt();
				
				Integer adzerkFormatElementAsInteger = Integer.valueOf(adzerkFormatElementAsInt);
				
				adzerkFormatsForAndroidGuide.add(adzerkFormatElementAsInteger);
			}
		}
		else
		{
			String elementAsInt = jsonAndroidGuideAdzerkFormatsElement.getAsString();
			
			Integer elementAsInteger = Integer.valueOf(elementAsInt);
			
			adzerkFormatsForAndroidGuide.add(elementAsInteger);
		}
		
		JsonElement jsonAndroidActivityAdzerkFormatsElement = jsonObject.get("android.activity.adzerkFormats");
		
		isJsonArray = jsonAndroidActivityAdzerkFormatsElement.isJsonArray();
		
		if(isJsonArray)
		{
			JsonArray elementAsArray = jsonAndroidActivityAdzerkFormatsElement.getAsJsonArray();
			
			for(JsonElement element : elementAsArray)
			{
				int adzerkFormatElementAsInt = element.getAsInt();
				
				Integer adzerkFormatElementAsInteger = Integer.valueOf(adzerkFormatElementAsInt);
				
				adzerkFormatsForActivity.add(adzerkFormatElementAsInteger);
			}
		}
		else
		{
			int elementAsInt = jsonAndroidActivityAdzerkFormatsElement.getAsInt();
			
			Integer elementAsInteger = Integer.valueOf(elementAsInt);
			
			adzerkFormatsForActivity.add(elementAsInteger);
		}
		
		JsonElement jsonAdzerkFormatsElement = jsonObject.get("adzerkFormats");
		
		isJsonArray = jsonAdzerkFormatsElement.isJsonArray();
		
		if(isJsonArray)
		{
			JsonArray elementAsArray = jsonAdzerkFormatsElement.getAsJsonArray();
			
			for(JsonElement element : elementAsArray)
			{
				int adzerkFormatElementAsInt = element.getAsInt();
				
				Integer adzerkFormatElementAsInteger = Integer.valueOf(adzerkFormatElementAsInt);
				
				adzerkFormats.add(adzerkFormatElementAsInteger);
			}
		}
		else
		{
			int elementAsInt = jsonAdzerkFormatsElement.getAsInt();
			
			Integer elementAsInteger = Integer.valueOf(elementAsInt);
			
			adzerkFormats.add(elementAsInteger);
		}
		
		boolean hasWelcomeToast = jsonObject.has("welcomeToast");
				
		if(hasWelcomeToast)
		{
			jsonObject.get("welcomeToast");
			
			welcomeToast = jsonElement.getAsString();
		}
		else
		{
			welcomeToast = "";
		}
		
		jsonElement = jsonObject.get("adzerkLevel");
		
		adzerkLevel = jsonElement.getAsInt();
		
		jsonElement = jsonObject.get("facebook.appID");
		
		facebookAppID = jsonElement.getAsString();
		
		jsonElement = jsonObject.get("tracking.domain");
		
		trackingDomain = jsonElement.getAsString();
		
		jsonElement = jsonObject.get("firstHourOfDay");
		
		firstHourOfDay = jsonElement.getAsInt();
		
		jsonElement = jsonObject.get("android.googleAnalyticsTrackingId");
		
		googleAnalyticsTrackingId = jsonElement.getAsString();
		
		jsonElement = jsonObject.get("android.guide.cellCountBetweenAdCells");
		
		guideFragmentCellCountBetweenAdCells = jsonElement.getAsInt();
		
		if(jsonObject.has("android.comments.disqus.enabled"))
		{
			jsonElement = jsonObject.get("android.comments.disqus.enabled");
		
			areDisqusCommentsEnabled = jsonElement.getAsBoolean();
		}
		else
		{
			areDisqusCommentsEnabled = false;
		}
		
		return this;
	}



	public int getFirstHourOfDay() {
		return firstHourOfDay;
	}



	public String getWelcomeToast() {
		return welcomeToast;
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



	public boolean isAdsEnabled() {
		return adsEnabled;
	}



	public List<Integer> getAdzerkFormatsForAndroidGuide() {
		return adzerkFormatsForAndroidGuide;
	}



	public List<Integer> getAdzerkFormatsForActivity() {
		return adzerkFormatsForActivity;
	}



	public List<Integer> getAdzerkFormats() {
		return adzerkFormats;
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


	public String getFacebookAppID() {
		return facebookAppID;
	}



	public String getTrackingDomain() {
		return trackingDomain;
	}



	public int getFeedActivityCellCountBetweenAdCells() {
		return feedActivityCellCountBetweenAdCells;
	}



	public int getGuideFragmentCellCountBetweenAdCells() {
		return guideFragmentCellCountBetweenAdCells;
	}



	public boolean areDisqusCommentsEnabled() {
		return areDisqusCommentsEnabled;
	}	
}