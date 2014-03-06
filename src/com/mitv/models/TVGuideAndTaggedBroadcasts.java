package com.mitv.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TVGuideAndTaggedBroadcasts {
	private TVGuide tvGuide;
	private HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate;
	
	public TVGuideAndTaggedBroadcasts(TVGuide tvGuide, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> mapTagToTaggedBroadcastForDate) {
		this.tvGuide = tvGuide;
		this.mapTagToTaggedBroadcastForDate = mapTagToTaggedBroadcastForDate;
	}

	public TVGuide getTvGuide() {
		return tvGuide;
	}

	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getMapTagToTaggedBroadcastForDate() {
		return mapTagToTaggedBroadcastForDate;
	}
}
