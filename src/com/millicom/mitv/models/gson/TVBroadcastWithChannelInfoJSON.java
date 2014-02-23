
package com.millicom.mitv.models.gson;



import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVChannel;



public class TVBroadcastWithChannelInfoJSON 
	extends TVBroadcast 
{
	protected TVChannel channel;

	
	
	public TVChannel getChannel() 
	{
		return channel;
	}
}