
package com.mitv.models;



import java.util.ArrayList;

import com.mitv.models.gson.TVFeedItemJSON;



public class TVFeedItem
	extends TVFeedItemJSON
{
	protected ArrayList<TVBroadcastWithChannelInfo> broadcastsAsArrayList;
	
	public ArrayList<TVBroadcastWithChannelInfo> getBroadcasts() {
		if (broadcastsAsArrayList == null) {
			broadcastsAsArrayList = new ArrayList<TVBroadcastWithChannelInfo>(broadcasts);
		}
		return broadcastsAsArrayList;
	}
}