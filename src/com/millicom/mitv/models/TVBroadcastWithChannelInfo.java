package com.millicom.mitv.models;

import java.util.Calendar;

import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfoJSON;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVProgram;

public class TVBroadcastWithChannelInfo extends TVBroadcastWithChannelInfoJSON {
	
	public TVBroadcastWithChannelInfo(TVBroadcast broadcast) {
		this.program = broadcast.getProgram();
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
		this.broadcastType = broadcast.getBroadcastType();
		this.shareUrl = broadcast.getShareUrl();
		this.beginTimeCalendar = broadcast.getBeginTimeCalendar();
		this.endTimeCalendar = broadcast.getEndTimeCalendar();
	}
	
	public void setChannel(TVChannel channel) 
	{
		this.channel = channel;
	}
	
	
	public void setProgram(TVProgram program) 
	{
		this.program = program;
	}
	
	
	public void setBeginTimeCalendar(Calendar beginTimeCalendar) 
	{
		this.beginTimeCalendar = beginTimeCalendar;
	}

	
	public void setEndTimeCalendar(Calendar endTimeCalendar) 
	{
		this.endTimeCalendar = endTimeCalendar;
	}
}
