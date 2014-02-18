package com.millicom.mitv.models.gson;


public class TVChannel {

	private String channelId;
	private String name;
	private Logo logo;
	private transient TVChannelId tvChannelIdObject;
	
	public TVChannelId getChannelId() {
		if(tvChannelIdObject == null) {
			tvChannelIdObject = new TVChannelId(channelId);
		}
		return tvChannelIdObject;
	}
	
	public String getName() {
		return name;
	}

	public Logo getLogo() {
		return logo;
	}


}
