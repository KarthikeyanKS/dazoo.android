
package com.mitv.models.gson.mitvapi.competitions;



import android.util.Log;



public class EventBroadcastJSON 
{
	private static final String TAG = EventBroadcastJSON.class.getName();
	
	
	protected String eventBroadcastId;
	protected String programId;
	protected String beginTime;
	protected long beginTimeMillis;
	protected String endTime;
	protected String channelId;

    
    
    public EventBroadcastJSON(){}



	public String getEventBroadcastId() 
	{
		if(eventBroadcastId == null)
		{
			eventBroadcastId = "";
			
			Log.w(TAG, "eventBroadcastId is null");
		}
		
		return eventBroadcastId;
	}



	public String getProgramId() 
	{
		if(programId == null)
		{
			programId = "";
			
			Log.w(TAG, "programId is null");
		}
		
		return programId;
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



	public long getBeginTimeMillis() 
	{
		return beginTimeMillis;
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



	public String getChannelId()
	{
		if(channelId == null)
		{
			channelId = "";
			
			Log.w(TAG, "channelId is null");
		}
		
		return channelId;
	}
}
