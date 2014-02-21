
package com.millicom.mitv.models.dto;



import java.util.Calendar;

import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVProgram;



public class TVBroadcastWithChannelInfoDTO 
	extends TVBroadcastWithChannelInfo 
{
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
