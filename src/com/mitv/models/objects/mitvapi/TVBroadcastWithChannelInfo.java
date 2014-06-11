
package com.mitv.models.objects.mitvapi;



import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVBroadcastWithChannelInfoJSON;



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
		this.beginTimeCalendarLocal = broadcast.getBeginTimeCalendarLocalForTagGeneration();
		this.endTimeCalendarLocal = broadcast.getEndTimeCalendarLocalForTagGeneration();
		this.isPopular = broadcast.isPopular();
		this.eventId = broadcast.getEventId();
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
		
		if(broadcasts != null) 
		{
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

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TVBroadcastWithChannelInfoJSON other = (TVBroadcastWithChannelInfoJSON) obj;
		if (channel == null) {
			if (other.getChannel() != null) {
				return false;
			}
		} else if (!channel.equals(other.getChannel())) {
			return false;
		}
		return true;
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
