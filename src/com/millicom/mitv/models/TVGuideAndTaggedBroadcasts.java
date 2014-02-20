package com.millicom.mitv.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TVGuideAndTaggedBroadcasts {
	private TVGuide tvGuide;
	private HashMap<String, ArrayList<Broadcast>> mapTagToTaggedBroadcastForDate;
	
	public TVGuideAndTaggedBroadcasts(TVGuide tvGuide, HashMap<String, ArrayList<Broadcast>> mapTagToTaggedBroadcastForDate) {
		this.tvGuide = tvGuide;
		this.mapTagToTaggedBroadcastForDate = mapTagToTaggedBroadcastForDate;
	}

	public TVGuide getTvGuide() {
		return tvGuide;
	}

	public HashMap<String, ArrayList<Broadcast>> getMapTagToTaggedBroadcastForDate() {
		return mapTagToTaggedBroadcastForDate;
	}
}
