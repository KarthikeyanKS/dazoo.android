package com.millicom.mitv.models.gson;

import java.util.Date;

public class TVBroadcastPrograms {
	
	TVChannel channel;
	Long beginTimeMillis;
	Date beginTime;
	Date endTime;
	String broadcastType;
	String shareUrl;
	
	public TVChannel getChannel() {
		return channel;
	}
	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public String getBroadcastType() {
		return broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	
	
}
