
package com.mitv.models.gson;



import com.mitv.models.TVBroadcast;
import com.mitv.models.TVChannel;



public class TVBroadcastWithChannelInfoJSON 
	extends TVBroadcast 
{
	protected TVChannel channel;

	
	
	public TVChannel getChannel() 
	{
		return channel;
	}
}