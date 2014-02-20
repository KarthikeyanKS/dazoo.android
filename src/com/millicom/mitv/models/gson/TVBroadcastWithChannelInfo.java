package com.millicom.mitv.models.gson;

import com.millicom.mitv.models.Broadcast;

public class TVBroadcastWithChannelInfo extends Broadcast {
	protected TVChannel channel;

	public TVChannel getChannel() {
		return channel;
	}
}
