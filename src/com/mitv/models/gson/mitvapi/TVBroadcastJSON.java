
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.models.objects.mitvapi.TVProgram;



public class TVBroadcastJSON 
{
	private static final String TAG = TVBroadcastJSON.class.getName();
	
	
	protected TVProgram program;
	protected Long beginTimeMillis;
	protected String beginTime;
	protected String endTime;
	protected BroadcastTypeEnum broadcastType;
	protected String shareUrl;
	protected Long eventId;
		
	
	
	public TVBroadcastJSON(){}
	
	
	
	public TVProgram getProgram() 
	{
		if(program == null)
		{
			program = new TVProgram();
			
			Log.w(TAG, "program is null");
		}
		
		return program;
	}
	
	
	
	public Long getBeginTimeMillis() 
	{
		if(beginTimeMillis == null)
		{
			beginTimeMillis = Long.valueOf(0);
			
			Log.w(TAG, "beginTimeMillis is null");
		}
		
		return beginTimeMillis;
	}
	
	
	
	public String getBeginTime()
	{
		if(beginTime == null)
		{
			beginTime = "";
			
			Log.w(TAG, "beginTime is null");
		}
		
		return beginTime;
	}
	
	
	
	public String getEndTime()
	{
		if(endTime == null)
		{
			endTime = "";
			
			Log.w(TAG, "endTime is null");
		}
		
		return endTime;
	}
	
	
	
	public BroadcastTypeEnum getBroadcastType()
	{
		if(broadcastType == null)
		{
			broadcastType = BroadcastTypeEnum.UNKNOWN;
			
			Log.w(TAG, "broadcastType is null");
		}
		
		return broadcastType;
	}
	
	
	
	public String getShareUrl() 
	{
		if(shareUrl == null)
		{
			shareUrl = "";
			
			Log.w(TAG, "shareUrl is null");
		}
		
		return shareUrl;
	}
	
	
	
	public Long getEventId() 
	{
		if(eventId == null)
		{
			eventId = Long.valueOf(0);
			
			Log.w(TAG, "eventId is null");
		}
		
		return eventId;
	}
}
