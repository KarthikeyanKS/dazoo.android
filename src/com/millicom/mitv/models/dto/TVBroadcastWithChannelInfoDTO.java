package com.millicom.mitv.models.dto;

import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;

public class TVBroadcastWithChannelInfoDTO extends TVBroadcastWithChannelInfo {

	public void setChannel(TVChannel channel) {
		this.channel = channel;
	}
}
