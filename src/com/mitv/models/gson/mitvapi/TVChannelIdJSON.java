package com.mitv.models.gson.mitvapi;

import android.util.Log;


public class TVChannelIdJSON 
{
	private static final String TAG = TVChannelIdJSON.class.getName();
	
	
	protected String channelId;

	
	
	public TVChannelIdJSON(){}

	
	
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
