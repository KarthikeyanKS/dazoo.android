
package com.mitv.models.gson.mitvapi;

import android.util.Log;



public class UserLikeNextBroadcastJSON 
{
	private static final String	TAG	= UserLikeNextBroadcastJSON.class.getName();
	
	
	private String channelId;
	private Long beginTimeMillis;
	
	
	
	protected String getChannelIdString()
	{
		if(channelId == null)
		{
			channelId = "";
			
			Log.w(TAG, "channelId is null");
		}
		
		return channelId;
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
}
