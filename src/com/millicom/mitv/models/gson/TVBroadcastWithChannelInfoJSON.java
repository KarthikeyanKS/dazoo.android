
package com.millicom.mitv.models.gson;



import com.millicom.mitv.models.TVBroadcast;



public class TVBroadcastWithChannelInfoJSON 
	extends TVBroadcast 
{
	protected TVChannel channel;

	public TVChannel getChannel() 
	{
		return channel;
	}
}
