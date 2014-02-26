package com.millicom.mitv.models;

import java.util.ArrayList;

/**
 * The purpose of this class is to act as a super class to the classes
 * UpcomingBroadcastsForBroascast and RepeatingBroadcastsForBroadcasts
 * @author consultant_hdme
 *
 */
public class BroadcastWithRelatedBroadcasts {
	protected ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts;
	
	public BroadcastWithRelatedBroadcasts(ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		this.relatedBroadcasts = relatedBroadcasts;
	}
	
	public ArrayList<TVBroadcastWithChannelInfo> getRelatedBroadcasts() {
		return relatedBroadcasts;
	}
}
