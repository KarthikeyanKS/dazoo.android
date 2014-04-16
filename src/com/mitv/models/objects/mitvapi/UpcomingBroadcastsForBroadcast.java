package com.mitv.models.objects.mitvapi;

import java.util.ArrayList;

public class UpcomingBroadcastsForBroadcast extends BroadcastWithRelatedBroadcasts {
	
	private String tvSeriesId;
	
	public UpcomingBroadcastsForBroadcast(String tvSeriesId, ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		super(relatedBroadcasts);
		this.tvSeriesId = tvSeriesId;
	}

	public String getTvSeriesId() {
		return tvSeriesId;
	}
	
	
}
