package com.millicom.mitv.models.gson;

import java.util.ArrayList;

public class TVChannelGuide extends TVChannel {
	private ArrayList<Broadcast> broadcasts;

	public ArrayList<Broadcast> getBroadcasts() {
		return broadcasts;
	}

	
	//TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */
	public int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TVDate date) {
		//TODO implement or delete me
		return 0;
	}
}
