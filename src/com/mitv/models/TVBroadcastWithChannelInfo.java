
package com.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVBroadcastWithChannelInfoJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class TVBroadcastWithChannelInfo 
	extends TVBroadcastWithChannelInfoJSON 
	implements GSONDataFieldValidation
{
	private static final String TAG = TVBroadcastWithChannelInfoJSON.class.getName();
	
	
	public TVBroadcastWithChannelInfo(){}

	
	
	public TVBroadcastWithChannelInfo(TVBroadcast broadcast)
	{
		this.program = broadcast.getProgram();
		this.beginTimeMillis = broadcast.getBeginTimeMillis();
		this.beginTime = broadcast.getBeginTime();
		this.endTime = broadcast.getEndTime();
		this.broadcastType = broadcast.getBroadcastType();
		this.shareUrl = broadcast.getShareUrl();
		this.beginTimeCalendarLocal = broadcast.getBeginTimeCalendarLocal();
		this.endTimeCalendarLocal = broadcast.getEndTimeCalendarLocal();
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
		this.beginTimeCalendarLocal = beginTimeCalendar;
	}

	
	
	public void setEndTimeCalendar(Calendar endTimeCalendar) 
	{
		this.endTimeCalendarLocal = endTimeCalendar;
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


	@Override
	public boolean areDataFieldsValid() {
		boolean broadcastFieldsOK = super.areDataFieldsValid();
		if(!broadcastFieldsOK) {
			super.areDataFieldsValid();
		}
		TVChannel tvChannel = getChannel();
		boolean channelFieldsOK = tvChannel.areDataFieldsValid();
		
		boolean areDataFieldsValid = broadcastFieldsOK && channelFieldsOK;
		Log.d(TAG, String.format("broadcastFieldsOK: %s, channelFieldsOK> %s", broadcastFieldsOK ? "ok" : "fail", channelFieldsOK ? "ok" : "fail"));
		return areDataFieldsValid;
	}
	
	
}