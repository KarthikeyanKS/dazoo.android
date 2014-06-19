
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.models.objects.mitvapi.ImageSetSize;



public class TVChannelJSON
{
	private static final String TAG = TVChannelJSON.class.getName();
	
	
	protected String channelId;
	protected String name;
	protected ImageSetSize logo;
	
	
	
	public String getChannelIdString() 
	{
		if(channelId == null)
		{
			channelId = "";
			
			Log.w(TAG, "channelId is null");
		}
		
		return channelId;
	}
	
	
	
	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}

	
	
	public ImageSetSize getLogo() 
	{
		if(logo == null)
		{
			logo = new ImageSetSize();
			
			Log.w(TAG, "logo is null");
		}
		
		return logo;
	}
}