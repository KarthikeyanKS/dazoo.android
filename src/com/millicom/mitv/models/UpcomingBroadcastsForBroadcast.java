package com.millicom.mitv.models;

import java.util.ArrayList;

public class UpcomingBroadcastsForBroadcast extends BroadcastWithRelatedBroadcasts {
	
	public UpcomingBroadcastsForBroadcast(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		super(broadcast, relatedBroadcasts);
	}
}
