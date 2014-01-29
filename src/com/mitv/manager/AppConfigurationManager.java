package com.mitv.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitv.Consts;

public class AppConfigurationManager {

	private static AppConfigurationManager selfInstance;

	public static AppConfigurationManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new AppConfigurationManager();
		}
		return selfInstance;
	}

	private int firstHourOfTVDay;
	private String welcomeToast;

	/* Ad configuration */
	private boolean adsEnabled;
//	private int cellCountBetweenAdCellsGuide;
//	private int cellCountBetweenAdCellsActivity;
//	private List<Integer> adzerkAdFormatsGuide;
//	private List<Integer> adzerkAdFormatsActivity;
	private HashMap<String, List<Integer>> fragmentToAdFormatsMap = new HashMap<String, List<Integer>>();
	private HashMap<String, Integer> fragmentToCellsBetweenAdCellsCount = new HashMap<String, Integer>();
	private int adzerkNetworkId;
	private int adzerkSiteId;

	/* Google Analytics configuration */
	private boolean googleAnalyticsEnabled;
	private double googleAnalyticsSampleRate;
	private String googleAnalyticsTrackingId;

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

//	public int getCellCountBetweenAdCellsGuide() {
//		return cellCountBetweenAdCellsGuide;
//	}
//
//	public void setCellCountBetweenAdCellsGuide(int cellCountBetweenAdCellsGuide) {
//		this.cellCountBetweenAdCellsGuide = cellCountBetweenAdCellsGuide;
//	}
//
//	public int getCellCountBetweenAdCellsActivity() {
//		return cellCountBetweenAdCellsActivity;
//	}
//
//	public void setCellCountBetweenAdCellsActivity(int cellCountBetweenAdCellsActivity) {
//		this.cellCountBetweenAdCellsActivity = cellCountBetweenAdCellsActivity;
//	}

//	public List<Integer> getAdzerkAdFormatsGuide() {
//		return adzerkAdFormatsGuide;
//	}
//
//	public void setAdzerkAdFormatsGuide(List<Integer> adzerkAdFormatsGuide) {
//		this.adzerkAdFormatsGuide = adzerkAdFormatsGuide;
//	}
//
//	public List<Integer> getAdzerkAdFormatsActivity() {
//		return adzerkAdFormatsActivity;
//	}
//
//	public void setAdzerkAdFormatsActivity(List<Integer> adzerkAdFormatsActivity) {
//		this.adzerkAdFormatsActivity = adzerkAdFormatsActivity;
//	}

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
	
	public void setWelcomeToast(String welcomeToast) {
		this.welcomeToast = welcomeToast;
	}
	
	public String getWelcomeToast() {
		return welcomeToast;
	}

	public List<Integer> adFormatsFromJSON(JSONObject configurationJSONObject, String adFormatsJSONKey) {
		List<Integer> adFormatsGuide = new ArrayList<Integer>();
		try {
			JSONArray adFormatsJson = configurationJSONObject.optJSONArray(adFormatsJSONKey);
			if (adFormatsJson != null) {
				for (int i = 0; i < adFormatsJson.length(); ++i) {
					int adFormat = adFormatsJson.getInt(i);
					adFormatsGuide.add(adFormat);
				}
			} else {
				Integer adFormat = configurationJSONObject.optInt(adFormatsJSONKey);
				adFormatsGuide.add(adFormat);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return adFormatsGuide;
	}

	public int getCellsBetweenAdCellsCountForFragment(String fragmentName) {
		Integer cellCountBetweenAdCellsObj = fragmentToCellsBetweenAdCellsCount.get(fragmentName);
		int cellCountBetweenAdCells = -1;
		if(cellCountBetweenAdCellsObj != null) {
			cellCountBetweenAdCells = cellCountBetweenAdCellsObj.intValue();
		}
		return cellCountBetweenAdCells;
	}
	
	public List<Integer> getAdFormatsForFragment(String fragmentName) {
		List<Integer> adFormats = fragmentToAdFormatsMap.get(fragmentName);
		return adFormats;
	}
	
	private String jsonKeyForFragment(String fragment, String base) {
		String jsonKey = String.format(Locale.getDefault(), base, fragment);
		return jsonKey;
	}
	
	private String jsonKeyForAdFormatsForFragment(String fragment) {
		String jsonKey = jsonKeyForFragment(fragment, Consts.JSON_KEY_CONFIGURATION_ADZERK_AD_FORMATS_BASE);
		return jsonKey;
	}
	
	private String jsonKeyForCellCountBetweenAdCellsForFragment(String fragment) {
		String jsonKey = jsonKeyForFragment(fragment, Consts.JSON_KEY_CONFIGURATION_CELLS_BETWEEN_AD_CELLS_BASE);
		return jsonKey;
	}
	
	private String jsonKeyAdFormatsGuide() {
		String jsonKeyAdFormatsGuide = jsonKeyForAdFormatsForFragment(Consts.JSON_AND_FRAGMENT_KEY_GUIDE);
		return jsonKeyAdFormatsGuide;
	}
	
	private String jsonKeyAdFormatsActivity() {
		String jsonKeyAdFormatsActivity = jsonKeyForAdFormatsForFragment(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY);
		return jsonKeyAdFormatsActivity;
	}
	
	public void updateConfiguration(JSONObject configurationJSONObject) {
		if (configurationJSONObject != null) {
			String jsonKeyAdFormatsGuide = jsonKeyAdFormatsGuide();
			String jsonKeyAdFormatsActivity = jsonKeyAdFormatsActivity();
			
			String jsonKeyCellCountBetweenAdCellsGuide = jsonKeyForCellCountBetweenAdCellsForFragment(Consts.JSON_AND_FRAGMENT_KEY_GUIDE);
			String jsonKeyCellCountBetweenAdCellsActivity = jsonKeyForCellCountBetweenAdCellsForFragment(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY);
			
			List<Integer> adFormatsGuide = adFormatsFromJSON(configurationJSONObject, jsonKeyAdFormatsGuide);
//			getInstance().setAdzerkAdFormatsGuide(adFormatsGuide);
			List<Integer> adFormatsActivity = adFormatsFromJSON(configurationJSONObject, jsonKeyAdFormatsActivity);
//			getInstance().setAdzerkAdFormatsActivity(adFormatsActivity);
			
			fragmentToAdFormatsMap.put(Consts.JSON_AND_FRAGMENT_KEY_GUIDE, adFormatsGuide);
			fragmentToAdFormatsMap.put(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, adFormatsActivity);

			String firstHourOfTVDayString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_FIRST_HOUR_OF_TV_DAY);

			String adsEnabledString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADS_ENABLED);
			String adzerkNetworkIdString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADZERK_NETWORK_ID);
			String adzerkSiteIdString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_ADZERK_SITE_ID);

			String googleAnalyticsEnabledString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_ENABLED);
			String googleAnalyticsSampleRateString = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_SAMPLE_RATE);
			String googleAnalyticsTrackingId = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_GOOGLE_ANALYTICS_TRACKING_ID);

			int firstHourOfTVDay = Integer.parseInt(firstHourOfTVDayString);
			
			String welcomeToast = configurationJSONObject.optString(Consts.JSON_KEY_CONFIGURATION_WELCOME_TOAST);

			boolean adsEnabled = Boolean.parseBoolean(adsEnabledString);
			int adzerkNetworkId = Integer.parseInt(adzerkNetworkIdString);
			int adzerkSiteId = Integer.parseInt(adzerkSiteIdString);

			boolean googleAnalyticsEnabled = Boolean.parseBoolean(googleAnalyticsEnabledString);
			double googleAnalyticsSampleRate = Double.parseDouble(googleAnalyticsSampleRateString);

			/* Ordinary cell count per ad cell for each view with ads in them */
			String cellCountBetweenAdCellsGuideString = configurationJSONObject.optString(jsonKeyCellCountBetweenAdCellsGuide);
			int cellCountBetweenAdCellsGuide = Integer.parseInt(cellCountBetweenAdCellsGuideString);
//			getInstance().setCellCountBetweenAdCellsGuide(cellCountBetweenAdCellsGuide);
			String cellCountBetweenAdCellsActivityString = configurationJSONObject.optString(jsonKeyCellCountBetweenAdCellsActivity);
			int cellCountBetweenAdCellsActivity = Integer.parseInt(cellCountBetweenAdCellsActivityString);
//			getInstance().setCellCountBetweenAdCellsActivity(cellCountBetweenAdCellsActivity);
			fragmentToCellsBetweenAdCellsCount.put(Consts.JSON_AND_FRAGMENT_KEY_GUIDE, cellCountBetweenAdCellsGuide);
			fragmentToCellsBetweenAdCellsCount.put(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, cellCountBetweenAdCellsActivity);

			getInstance().setFirstHourOfTVDay(firstHourOfTVDay);
			getInstance().setWelcomeToast(welcomeToast);

			getInstance().setAdsEnabled(adsEnabled);
			getInstance().setAdzerkNetworkId(adzerkNetworkId);
			getInstance().setAdzerkSiteId(adzerkSiteId);

			getInstance().setGoogleAnalyticsEnabled(googleAnalyticsEnabled);
			getInstance().setGoogleAnalyticsSampleRate(googleAnalyticsSampleRate);
			getInstance().setGoogleAnalyticsTrackingId(googleAnalyticsTrackingId);
		}
	}
}
