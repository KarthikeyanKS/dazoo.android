
package com.mitv.models.gson;



public class AdAdzerkJSON 
{
	/* Ids & keys */
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
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public AdAdzerkJSON()
	{}



	public String getUserKey() {
		return userKey;
	}



	public String getDivId() {
		return divId;
	}



	public String getAdId() {
		return adId;
	}



	public String getCreativeId() {
		return creativeId;
	}



	public String getFlightId() {
		return flightId;
	}



	public String getCampaignId() {
		return campaignId;
	}



	public String getClickUrl() {
		return clickUrl;
	}



	public String getImpressionUrl() {
		return impressionUrl;
	}



	public String getImageUrl() {
		return imageUrl;
	}



	public int getWidth() {
		return width;
	}



	public int getHeight() {
		return height;
	}
}
