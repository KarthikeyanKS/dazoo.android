
package com.mitv.models.gson.mitvapi.competitions;



import com.mitv.models.objects.mitvapi.ImageSetSize;

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
	
	/* These parameters should only used if the channelId cannot be matched on the local cache */
	protected String channel;
	protected ImageSetSize channelLogo;

    
    
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



	public String getChannel() 
	{
		if(channel == null)
		{
			channel = "";
			
			Log.w(TAG, "channel is null");
		}
		
		return channel;
	}



	public ImageSetSize getChannelLogo() 
	{
		if(channelLogo == null)
		{
			channelLogo = new ImageSetSize();
			
			Log.w(TAG, "channelLogo is null");
		}
		
		return channelLogo;
	}
}
