package com.millicom.mitv.models.gson;

import com.millicom.mitv.models.TVBroadcast;


public class TVBroadcastWithProgramAndChannelInfo extends TVBroadcast {
	
	private TVChannel channel;
		
	public TVChannel getChannel() {
		return channel;
	}
}
