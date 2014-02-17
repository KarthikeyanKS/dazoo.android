package com.mitv.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mitv.Consts;

public class AdzerkAd {

	/* Ids % keys */
	private String userKey;
	private String divId;
	private String adId;
	private String creativeId;
	private String flightId;
	private String campaignId;
	
	/* URLs */
	private String clickUrl;
	private String impressionUrl;
	private String imageUrl;
	
	/* Dimensions */
	private int width;
	private int height;
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	public String getDivId() {
		return divId;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public String getAdId() {
		return adId;
	}
	public void setAdId(String adId) {
		this.adId = adId;
	}
	public String getCreativeId() {
		return creativeId;
	}
	public void setCreativeId(String creativeId) {
		this.creativeId = creativeId;
	}
	public String getFlightId() {
		return flightId;
	}
	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}
	public String getImpressionUrl() {
		return impressionUrl;
	}
	public void setImpressionUrl(String impressionUrl) {
		this.impressionUrl = impressionUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public AdzerkAd(String divId, JSONObject jsonObjectForAd) {
		this.divId = divId;
		
		String userKey = "";
		String adId = "";
		String creativeId = "";
		String flightId = "";
		String campaignId = "";

		String clickUrl = "";
		String impressionUrl = "";
		String imageUrl = "";
		
		int width = 0;
		int height = 0;
		
		try {
			JSONObject userObject = jsonObjectForAd.getJSONObject(Consts.JSON_KEY_ADS_OBJECT_KEY_USER);

			if (userObject != null) {
				userKey = userObject.optString(Consts.JSON_KEY_ADS_USER_KEY);
			}

			JSONObject decisionsObject = jsonObjectForAd.getJSONObject(Consts.JSON_KEY_ADS_OBJECT_KEY_DECISIONS);

			if (decisionsObject != null) {
				JSONObject jsonAd = decisionsObject.getJSONObject(divId);

				if (jsonAd != null) {
					adId = jsonAd.optString(Consts.JSON_KEY_ADS_AD_ID);
					creativeId = jsonAd.optString(Consts.JSON_KEY_ADS_CREATIVE_ID);
					flightId = jsonAd.optString(Consts.JSON_KEY_ADS_FLIGHT_ID);
					campaignId = jsonAd.optString(Consts.JSON_KEY_ADS_CAMPAIGN_ID);

					clickUrl = jsonAd.optString(Consts.JSON_KEY_ADS_CLICK_URL);
					impressionUrl = jsonAd.optString(Consts.JSON_KEY_ADS_IMPRESSION_URL);

					JSONArray contentObjects = jsonAd.getJSONArray(Consts.JSON_KEY_ADS_SUB_OBJECT_KEY_CONTENTS);
					if (contentObjects != null) {
						if (contentObjects.length() > 0) {
							JSONObject contentsObject = (JSONObject) contentObjects.get(0);

							if (contentsObject != null) {
								JSONObject dataObject = contentsObject.getJSONObject(Consts.JSON_KEY_ADS_SUB_OBJECT_KEY_DATA);
								if (dataObject != null) {
									imageUrl = dataObject.optString(Consts.JSON_KEY_ADS_IMAGE_URL);
								}
							}

							String imageWidthString, imageHeightString;

							imageWidthString = jsonAd.optString(Consts.JSON_KEY_ADS_IMAGE_WIDTH);
							imageHeightString = jsonAd.optString(Consts.JSON_KEY_ADS_IMAGE_HEIGHT);

							width = Integer.parseInt(imageWidthString);
							height = Integer.parseInt(imageHeightString);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.setUserKey(userKey);
		this.setAdId(adId);
		this.setCreativeId(creativeId);
		this.setFlightId(flightId);

		this.setCampaignId(campaignId);
		this.setClickUrl(clickUrl);
		this.setImpressionUrl(impressionUrl);
		this.setImageUrl(imageUrl);
		
		this.setWidth(width);
		this.setHeight(height);
	}
}
