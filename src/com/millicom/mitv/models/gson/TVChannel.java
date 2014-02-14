package com.millicom.mitv.models.gson;

import java.io.Serializable;

public class TVChannel implements Serializable{

	
	private static final long serialVersionUID = -7486862284235656332L;
	
	private TVChannelId channelId;
	private String name;
	private Logo logo;
	
	public TVChannelId getChannelId() {
		return channelId;
	}
	public String getName() {
		return name;
	}
	public Logo getLogo() {
		return logo;
	}
	
	
}
