
package com.millicom.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;

import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.models.gson.TVBroadcastWithChannelInfoJSON;
import com.mitv.notification.NotificationSQLElement;



public class TVBroadcastWithChannelInfo 
	extends TVBroadcastWithChannelInfoJSON 
{
	public TVBroadcastWithChannelInfo(){}

	
	
	public TVBroadcastWithChannelInfo(TVBroadcast broadcast)
	{
		this.program = broadcast.getProgram();
		
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		
		this.broadcastType = broadcast.getBroadcastType();
		
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
		
		this.beginTimeCalendarGMT = broadcast.getBeginTimeCalendarGMT();
		this.endTimeCalendarGMT = broadcast.getEndTimeCalendarGMT();
		
		this.shareUrl = broadcast.getShareUrl();
	}
	
	
	
	public TVBroadcastWithChannelInfo(NotificationSQLElement item)
	{
		TVChannel tvChannel = new TVChannel(item);
		this.channel = tvChannel;
		
		TVProgram tvProgram = new TVProgram(item);
		this.program = tvProgram;
		
		String broadcastTypeAsString = item.getBroadcastType();
		
		this.broadcastType = BroadcastTypeEnum.getBroadcastTypeEnumFromStringRepresentation(broadcastTypeAsString);
		this.beginTimeMillis = item.getBroadcastBeginTimeInMilliseconds();
		this.beginTime = item.getBroadcastBeginTime();
		this.endTime = item.getBroadcastEndTime();
		this.shareUrl = item.getShareUrl();
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
		this.beginTimeCalendarGMT = beginTimeCalendar;
	}

	
	
	public void setEndTimeCalendar(Calendar endTimeCalendar) 
	{
		this.endTimeCalendarGMT = endTimeCalendar;
	}
	
	
	
	/**
	 * WARNING WARNING! DUPLICATION OF CODE!
	 * IF YOU CHANGE THIS METHOD YOU MUST CHANGE
	 * ITS SIBLING METHOD HAVING THE SAME NAME.
	 * 
	 * SIBLING METHOD IS IN BROADCAST CLASS
	 */
	public static int getClosestBroadcastIndex(
			final ArrayList<TVBroadcastWithChannelInfo> broadcasts,
			final int defaultValueIfNotFound) 
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		if(broadcasts != null) {
			for(int i=0; i<broadcasts.size(); i++)
			{
				TVBroadcastWithChannelInfo broadcast = broadcasts.get(i);
				
				boolean hasNotAiredYetOrIsAiring = broadcast.hasNotAiredYet() || broadcast.isAiring();
				
				if(hasNotAiredYetOrIsAiring)
				{
					closestIndexFound = i;
					break;
				}
			}
		}
		return closestIndexFound;
	}
}
