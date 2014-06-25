
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannel;



public class TVBroadcastWithChannelInfoJSON 
	extends TVBroadcast
{
	private static final String TAG = TVBroadcastWithChannelInfoJSON.class.getName();
	
	
	protected TVChannel channel;

	
	
	public TVChannel getChannel() 
	{
		if(channel == null)
		{
			channel = new TVChannel();
			
			Log.w(TAG, "channel is null");
		}
		
		return channel;
	}
}