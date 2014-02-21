package com.millicom.mitv.models.dto;

import com.millicom.mitv.models.ImageSetSize;
import com.millicom.mitv.models.gson.TVChannel;

public class TVChannelDTO extends TVChannel {

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAllImageUrls(String imageUrl) {
		ImageSetSize images = new ImageSetSize(imageUrl, imageUrl, imageUrl);
		this.logo = images;
	}
}
