package com.millicom.mitv.models;

import java.util.ArrayList;

public class RepeatingBroadcastsForBroadcast extends BroadcastWithRelatedBroadcasts {
	
	public RepeatingBroadcastsForBroadcast(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		super(broadcast, relatedBroadcasts);
	}
}
