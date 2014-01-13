package com.millicom.secondscreen.manager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;

public class AppConfigurationManager {

	private static AppConfigurationManager selfInstance;
	
	public static AppConfigurationManager getInstance() {
		if(selfInstance == null) {
			selfInstance = new AppConfigurationManager();
		}
		return selfInstance;
	}
	
	private int 		firstHourOfTVDay;

	/* Ad configuration */
	private boolean 	adsEnabled;
	private int 		cellCountBetweenAdCells;
	private List<Integer> adzerkAdFormats;
	private int 		adzerkNetworkId;
	private int 		adzerkSiteId;

	/* Google Analytics configuration */
	private boolean 	googleAnalyticsEnabled;
	private double 		googleAnalyticsSampleRate;
	private String 		googleAnalyticsTrackingId;

	public int getFirstHourOfTVDay() {
		return firstHourOfTVDay;
	}
	public void setFirstHourOfTVDay(int firstHourOfTVDay) {
		this.firstHourOfTVDay = firstHourOfTVDay;
	}
	public boolean isAdsEnabled() {
		return adsEnabled;
	}
	public void setAdsEnabled(boolean adsEnabled) {
		this.adsEnabled = adsEnabled;
	}
	public int getCellCountBetweenAdCells() {
		return cellCountBetweenAdCells;
	}
	public void setCellCountBetweenAdCells(int cellCountBetweenAdCells) {
		this.cellCountBetweenAdCells = cellCountBetweenAdCells;
	}
	public List<Integer> getAdzerkAdFormats() {
		return adzerkAdFormats;
	}
	public void setAdzerkAdFormats(List<Integer> adzerkAdFormats) {
		this.adzerkAdFormats = adzerkAdFormats;
	}
	public int getAdzerkNetworkId() {
		return adzerkNetworkId;
	}
	public void setAdzerkNetworkId(int adzerkNetworkId) {
		this.adzerkNetworkId = adzerkNetworkId;
	}
	public int getAdzerkSiteId() {
		return adzerkSiteId;
	}
	public void setAdzerkSiteId(int adzerkSiteId) {
		this.adzerkSiteId = adzerkSiteId;
	}
	public boolean isGoogleAnalyticsEnabled() {
		return googleAnalyticsEnabled;
	}
	public void setGoogleAnalyticsEnabled(boolean googleAnalyticsEnabled) {
		this.googleAnalyticsEnabled = googleAnalyticsEnabled;
	}
	public double getGoogleAnalyticsSampleRate() {
		return googleAnalyticsSampleRate;
	}
	public void setGoogleAnalyticsSampleRate(double googleAnalyticsSampleRate) {
		this.googleAnalyticsSampleRate = googleAnalyticsSampleRate;
	}
	public String getGoogleAnalyticsTrackingId() {
		return googleAnalyticsTrackingId;
	}
	public void setGoogleAnalyticsTrackingId(String googleAnalyticsTrackingId) {
		this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
	}
	
	public void updateConfiguration(JSONObject configurationJSONObject) {		
		if(configurationJSONObject != null) {
			
			List<Integer> adFormats = new ArrayList<Integer>();
			JSONArray adFormatsJson = configurationJSONObject.optJSONArray(Consts.JSON_KEY_CONFIGURATION_ADZERK_AD_FORMATS);
			try {
				for(int i = 0; i < adFormatsJson.length(); ++i) {
					int adFormat = adFormatsJson.getInt(i);
					adFormats.add(adFormat);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String firstHourOfTVDayString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_FIRST_HOUR_OF_TV_DAY);
			
			String adsEnabledString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADS_ENABLED);
			String cellCountBetweenAdCellsString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_CELL_COUNT_BETWEEN_AD_CELLS);
			String adzerkNetworkIdString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADZERK_NETWORK_ID);
			String adzerkSiteIdString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADZERK_SITE_ID); 
			
			String googleAnalyticsEnabledString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_ENABLED);
			String googleAnalyticsSampleRateString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_SAMPLE_RATE);
			String googleAnalyticsTrackingId = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_TRACKING_ID);
			
			int firstHourOfTVDay = Integer.parseInt(firstHourOfTVDayString);
			
			boolean adsEnabled = Boolean.parseBoolean(adsEnabledString);
			int cellCountBetweenAdCells = Integer.parseInt(cellCountBetweenAdCellsString);
			int adzerkNetworkId = Integer.parseInt(adzerkNetworkIdString);
			int adzerkSiteId = Integer.parseInt(adzerkSiteIdString);
			
			boolean googleAnalyticsEnabled = Boolean.parseBoolean(googleAnalyticsEnabledString);
			double googleAnalyticsSampleRate = Double.parseDouble(googleAnalyticsSampleRateString);
			
			getInstance().setAdzerkAdFormats(adFormats);
			getInstance().setFirstHourOfTVDay(firstHourOfTVDay);
			
			getInstance().setAdsEnabled(adsEnabled);
			getInstance().setCellCountBetweenAdCells(cellCountBetweenAdCells);
			getInstance().setAdzerkNetworkId(adzerkNetworkId);
			getInstance().setAdzerkSiteId(adzerkSiteId);
			
			getInstance().setGoogleAnalyticsEnabled(googleAnalyticsEnabled);
			getInstance().setGoogleAnalyticsSampleRate(googleAnalyticsSampleRate);
			getInstance().setGoogleAnalyticsTrackingId(googleAnalyticsTrackingId);
		}
	}
}
