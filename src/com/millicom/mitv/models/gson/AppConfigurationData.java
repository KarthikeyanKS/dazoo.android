package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class AppConfigurationData implements Serializable {//, JsonDeserializer<AppConfigurationData>{

	private static final long serialVersionUID = -1644608436707247798L;
	
	private int firstHourOfDay;
	private String welcomeToast;

	/* Ad configuration */
	private boolean adsEnabled;
	private HashMap<String, List<Integer>> fragmentToAdFormatsMap = new HashMap<String, List<Integer>>();
	private HashMap<String, Integer> fragmentToCellsBetweenAdCellsCount = new HashMap<String, Integer>();
	private int adzerkNetworkId;
	private int adzerkSiteId;

	/* Google Analytics configuration */
	private boolean googleAnalyticsEnabled;
	private double googleAnalyticsSampleRate;
	private String googleAnalyticsTrackingId;
	public int getFirstHourOfDay() {
		return firstHourOfDay;
	}
	public String getWelcomeToast() {
		return welcomeToast;
	}
	public boolean isAdsEnabled() {
		return adsEnabled;
	}
	public HashMap<String, List<Integer>> getFragmentToAdFormatsMap() {
		return fragmentToAdFormatsMap;
	}
	public HashMap<String, Integer> getFragmentToCellsBetweenAdCellsCount() {
		return fragmentToCellsBetweenAdCellsCount;
	}
	public int getAdzerkNetworkId() {
		return adzerkNetworkId;
	}
	public int getAdzerkSiteId() {
		return adzerkSiteId;
	}
	public boolean isGoogleAnalyticsEnabled() {
		return googleAnalyticsEnabled;
	}
	public double getGoogleAnalyticsSampleRate() {
		return googleAnalyticsSampleRate;
	}
	public String getGoogleAnalyticsTrackingId() {
		return googleAnalyticsTrackingId;
	}
	
//	@Override
//	public AppConfigurationData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//		return this;
//	}

	
}
