
package com.mitv.models.gson.mitvapi;



import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.models.objects.mitvapi.TVProgram;



public class TVBroadcastJSON 
{
	protected TVProgram program;
	protected Long beginTimeMillis;
	protected String beginTime;
	protected String endTime;
	protected BroadcastTypeEnum broadcastType = BroadcastTypeEnum.UNKNOWN;
	protected String shareUrl;
	protected long eventId;
		
	
	
	public TVBroadcastJSON(){}
	
	
	
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
	
	public long getEventId() {
		eventId = 1657995;
		return eventId;
	}
}
