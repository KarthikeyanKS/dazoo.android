
package com.mitv.models.gson.mitvapi;



import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannel;



public class TVChannelGuideJSON 
	extends TVChannel
{
	private static final String TAG = TVChannelGuideJSON.class.getName();
	
	
	private List<TVBroadcast> broadcasts;

	
	
	public List<TVBroadcast> getBroadcasts() 
	{
		if(broadcasts == null)
		{
			broadcasts = Collections.emptyList();
			
			Log.w(TAG, "broadcasts is null");
		}
		
		return broadcasts;
	}
	
	
	
	public boolean hasBroadcasts() 
	{
		boolean hasBroadcasts = (getBroadcasts().isEmpty() == false);
		
		return hasBroadcasts;
	}
}