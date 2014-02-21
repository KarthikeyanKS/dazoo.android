package com.millicom.mitv.models;

import java.util.ArrayList;

import com.millicom.mitv.models.gson.TVSearchResultJSON;

public class TVSearchResult extends TVSearchResultJSON {

	public TVBroadcastWithChannelInfo getNextBroadcast() {
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = null;
		
		ArrayList<TVBroadcastWithChannelInfo> broadcasts = getEntity().getBroadcasts();
		
		if(broadcasts != null) {
			if(broadcasts.size() > 0) {
				broadcastWithChannelInfo = broadcasts.get(0);
			}
		}
	
		return broadcastWithChannelInfo;
	}
}
