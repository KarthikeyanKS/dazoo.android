package com.millicom.mitv.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TVGuideAndTaggedBroadcasts {
	private TVGuide tvGuide;
	private HashMap<String, ArrayList<TVBroadcast>> mapTagToTaggedBroadcastForDate;
	
	public TVGuideAndTaggedBroadcasts(TVGuide tvGuide, HashMap<String, ArrayList<TVBroadcast>> mapTagToTaggedBroadcastForDate) {
		this.tvGuide = tvGuide;
		this.mapTagToTaggedBroadcastForDate = mapTagToTaggedBroadcastForDate;
	}

	public TVGuide getTvGuide() {
		return tvGuide;
	}

	public HashMap<String, ArrayList<TVBroadcast>> getMapTagToTaggedBroadcastForDate() {
		return mapTagToTaggedBroadcastForDate;
	}
}
