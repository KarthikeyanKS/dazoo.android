package com.mitv.models.objects.mitvapi;

import java.util.ArrayList;

public class RepeatingBroadcastsForBroadcast extends BroadcastWithRelatedBroadcasts {
	
	private String programId;
	
	public RepeatingBroadcastsForBroadcast(String programId, ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		super(relatedBroadcasts);
		this.programId = programId;
	}

	public String getProgramId() {
		return programId;
	}
	
	
}
