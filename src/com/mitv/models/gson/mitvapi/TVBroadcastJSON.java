
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
}
