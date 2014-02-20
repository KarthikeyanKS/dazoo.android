package com.millicom.mitv.models.gson;


public class TVChannel {

	protected String channelId;
	protected String name;
	protected ImageSetSize logo;
	protected transient TVChannelId tvChannelIdObject;
	
	public TVChannelId getChannelId() {
		if(tvChannelIdObject == null) {
			tvChannelIdObject = new TVChannelId(channelId);
		}
		return tvChannelIdObject;
	}
	
	public String getName() {
		return name;
	}

	public ImageSetSize getLogo() {
		return logo;
	}


	//TODO determine if those are good methods
	/* Partially implemented method */
	public String getImageUrl() {
		//TODO use getImageURLForDensityDPI instead?
		//String imageUrl = logo.getImageURLForDensityDPI(densityDpi)
		
		return logo.getLarge();
	}
}
