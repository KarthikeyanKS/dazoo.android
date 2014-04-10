
package com.mitv.models.gson.mitvapi;



import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannel;



public class TVBroadcastWithChannelInfoJSON 
	extends TVBroadcast 
{
	protected TVChannel channel;

	
	
	public TVChannel getChannel() 
	{
		return channel;
	}
}