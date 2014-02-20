package com.millicom.mitv.models.gson;

import com.millicom.mitv.models.Broadcast;


public class TVBroadcastWithProgramAndChannelInfo extends Broadcast {
	
	private TVChannel channel;
		
	public TVChannel getChannel() {
		return channel;
	}
}
