package com.millicom.mitv.models;

import java.util.ArrayList;

/**
 * The purpose of this class is to act as a super class to the classes
 * UpcomingBroadcastsForBroascast and RepeatingBroadcastsForBroadcasts
 * @author consultant_hdme
 *
 */
public class BroadcastWithRelatedBroadcasts {
	protected TVBroadcastWithChannelInfo broadcast;
	protected ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts;
	
	public BroadcastWithRelatedBroadcasts(TVBroadcastWithChannelInfo broadcast, ArrayList<TVBroadcastWithChannelInfo> relatedBroadcasts) {
		this.broadcast = broadcast;
		this.relatedBroadcasts = relatedBroadcasts;
	}
	
	public TVBroadcastWithChannelInfo getBroadcast() {
		return broadcast;
	}
	public ArrayList<TVBroadcastWithChannelInfo> getRelatedBroadcasts() {
		return relatedBroadcasts;
	}
	
	
}
