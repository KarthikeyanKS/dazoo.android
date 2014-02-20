package com.millicom.mitv.models.gson;

import com.millicom.mitv.enums.BroadcastTypeEnum;

public class BroadcastJSON 
{
	protected TVProgram program;
	protected Long beginTimeMillis;
	protected String beginTime;
	protected String endTime;
	protected BroadcastTypeEnum broadcastType;
	protected String shareUrl;
		
	public TVProgram getProgram() {
		return program;
	}
	
	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}
	
	public String getBeginTime() {
		return beginTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public BroadcastTypeEnum getBroadcastType() {
		return broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
}