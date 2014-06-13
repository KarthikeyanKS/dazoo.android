
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
import com.mitv.Constants;



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
	
	/* Competitions configuration */
	protected boolean areCompetitionsEnabled;
	
	protected boolean preventRateAppDialog;
	
	/* Cell count configuration */
	protected int feedActivityCellCountBetweenAdCells;
	protected int guideFragmentCellCountBetweenAdCells;
	
	protected int competitionPageReloadInterval;
	protected int competitionTeamPageReloadInterval;
	protected int competitionEventPageReloadInterval;
	protected int competitionEventPageHighlightReloadInterval;
	
	
	
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
		
		
		boolean hasCompetitionPageReloadInterval= jsonObject.has("android.competition.page.reload.interval");
		
		if(hasCompetitionPageReloadInterval)
		{
			jsonElement = jsonObject.get("android.competition.page.reload.interval");
			
			String competitionPageReloadIntervalAsString = jsonElement.getAsString();
			
			try
			{
				competitionPageReloadInterval = Integer.parseInt(competitionPageReloadIntervalAsString);
			}
			catch(NumberFormatException nfex)
			{
				competitionPageReloadInterval = Constants.COMPETITION_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
			}
		}
		else
		{
			competitionPageReloadInterval = Constants.COMPETITION_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
		}
		
		
		boolean hasCompetitionEventPageReloadInterval= jsonObject.has("android.event.page.reload.interval");
		
		if(hasCompetitionEventPageReloadInterval)
		{
			jsonElement = jsonObject.get("android.event.page.reload.interval");
			
			String competitionEventPageReloadIntervalAsString = jsonElement.getAsString();
			
			try
			{
				competitionEventPageReloadInterval = Integer.parseInt(competitionEventPageReloadIntervalAsString);
			}
			catch(NumberFormatException nfex)
			{
				competitionEventPageReloadInterval = Constants.COMPETITION_EVENT_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
			}
		}
		else
		{
			competitionEventPageReloadInterval = Constants.COMPETITION_EVENT_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
		}
		
		
		boolean hasCompetitionTeamPageReloadInterval= jsonObject.has("android.team.page.reload.interval");
		
		if(hasCompetitionTeamPageReloadInterval)
		{
			jsonElement = jsonObject.get("android.team.page.reload.interval");
			
			String competitionTeamPageReloadIntervalAsString = jsonElement.getAsString();
			
			try
			{
				competitionTeamPageReloadInterval = Integer.parseInt(competitionTeamPageReloadIntervalAsString);
			}
			catch(NumberFormatException nfex)
			{
				competitionTeamPageReloadInterval = Constants.COMPETITION_TEAM_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
			}
		}
		else
		{
			competitionTeamPageReloadInterval = Constants.COMPETITION_TEAM_PAGE_DEFAULT_RELOAD_TIME_IN_SECONDS;
		}
		

		boolean hasCompetitionEventPageHighlightReloadInterval= jsonObject.has("android.highlight.page.reload.interval");
		
		if(hasCompetitionEventPageHighlightReloadInterval)
		{
			jsonElement = jsonObject.get("android.highlight.page.reload.interval");
			
			String competitionEventPageHighlightReloadIntervalAsString = jsonElement.getAsString();
			
			try
			{
				competitionEventPageHighlightReloadInterval = Integer.parseInt(competitionEventPageHighlightReloadIntervalAsString);
			}
			catch(NumberFormatException nfex)
			{
				competitionEventPageHighlightReloadInterval = Constants.COMPETITION_EVENT_PAGE_HIGHLIGHTS_DEFAULT_RELOAD_TIME_IN_SECONDS;
			}
		}
		else
		{
			competitionEventPageHighlightReloadInterval = Constants.COMPETITION_EVENT_PAGE_HIGHLIGHTS_DEFAULT_RELOAD_TIME_IN_SECONDS;
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

	public boolean isPreventingRateAppDialog() {
		return preventRateAppDialog;
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



	public boolean isPreventRateAppDialog() {
		return preventRateAppDialog;
	}



	public int getCompetitionPageReloadInterval() {
		return competitionPageReloadInterval;
	}



	public int getCompetitionTeamPageReloadInterval() {
		return competitionTeamPageReloadInterval;
	}



	public int getCompetitionEventPageReloadInterval() {
		return competitionEventPageReloadInterval;
	}



	public int getCompetitionEventPageHighlightReloadInterval() {
		return competitionEventPageHighlightReloadInterval;
	}
}