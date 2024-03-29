
package com.mitv.models.objects.mitvapi;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.mitv.Constants;
import com.mitv.models.gson.mitvapi.TVChannelGuideJSON;
import com.mitv.utilities.DateUtils;



public class TVChannelGuide 
	extends TVChannelGuideJSON
{
	public ArrayList<TVBroadcast> getCurrentAndTwoUpcomingBroadcastsUsingSelectedDayAndHour(
			final int selectedHour,
			final TVDate tvDate) 
	{
		return getCurrentAndUpcomingBroadcastsUsingSelectedDayAndHour(selectedHour, tvDate, Constants.TV_GUIDE_NEXT_PROGRAMS_NUMBER);
	}
	
	
	
	public ArrayList<TVBroadcast> getCurrentAndUpcomingBroadcastsUsingSelectedDayAndHour(
			final int selectedHour,
			final TVDate tvDate,
			int howManyIncludingCurrent)
	{
		int indexOfNearestBroadcast = getClosestBroadcastIndex(selectedHour, tvDate, -1);
		
		ArrayList<TVBroadcast> threeNextBroadcasts = getBroadcastsFromPosition(indexOfNearestBroadcast, howManyIncludingCurrent);
		
		return threeNextBroadcasts;
	}
	
	
	
	public List<TVBroadcast> getBroadcastsFromPosition(final int startIndex) 
	{
		return getBroadcastsFromPosition(startIndex, getBroadcasts().size());
	}
	
	
	
	/**
	 *  This method does not use the selected TV Hour, it uses the current time and returns
	 *  the current broadcast and the upcoming ones.
	 * @return
	 */
	public List<TVBroadcast> getCurrentAndUpcomingBroadcastsUsingCurrentTime() 
	{
		List<TVBroadcast> currentAndUpcomingbroadcasts = Collections.emptyList();
		
		int indexIfNotFound = -1;
		
		int indexOfNearestBroadcast = getClosestBroadcastIndex(indexIfNotFound);
		
		if (indexOfNearestBroadcast > indexIfNotFound) 
		{
			currentAndUpcomingbroadcasts = getBroadcastsFromPosition(indexOfNearestBroadcast);
		}
		
		return currentAndUpcomingbroadcasts;
	}
	
	
	
	public List<TVBroadcast> getBroadcastPlayingAtSimilarTimeAs(final TVBroadcast inputBroadcast)
	{
		final Calendar inputBegin = inputBroadcast.getBeginTimeCalendarGMT();
		
		ArrayList<TVBroadcast> airingBroadcasts = new ArrayList<TVBroadcast>();

		for(TVBroadcast broadcast : getBroadcasts())
		{
			Calendar broadcastBegin = broadcast.getBeginTimeCalendarGMT();
			Calendar broadcastEnd = broadcast.getEndTimeCalendarGMT();
	
			Calendar now = DateUtils.getNowWithGMTTimeZone();
			
			Calendar broadcastBeginWithMinutesSubtracted = (Calendar) broadcastBegin.clone();
			broadcastBeginWithMinutesSubtracted.add(Calendar.MINUTE, -30);
			
			Calendar broadcastBeginWithMinutesAdded = (Calendar) broadcastBegin.clone();
			broadcastBeginWithMinutesAdded.add(Calendar.MINUTE, 30);
					
			if(inputBegin.before(now))
			{	
				if(broadcastBeginWithMinutesSubtracted.before(inputBegin) && 
				   broadcastEnd.after(inputBegin))
				{
					airingBroadcasts.add(broadcast);
				}
			}
			else
			{	
				if(broadcastEnd.after(now) &&
				   broadcastBeginWithMinutesAdded.after(inputBegin) && 
				   broadcastBeginWithMinutesSubtracted.before(inputBegin))
				{
					airingBroadcasts.add(broadcast);
				}
			}
		}
		
		return airingBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcast> getBroadcastsFromPosition(
			final int startIndex,
			final int maximumBrodacasts) 
	{
		ArrayList<TVBroadcast> broadcastsToReturn = new ArrayList<TVBroadcast>(maximumBrodacasts);

		if(startIndex >= 0 && 
		   startIndex < getBroadcasts().size())
		{
			for(int i=startIndex; i<getBroadcasts().size(); i++)
			{
				if(broadcastsToReturn.size() < maximumBrodacasts)
				{
					TVBroadcast broadcast = getBroadcasts().get(i);
					
					broadcastsToReturn.add(broadcast);
				}
				else
				{
					break;
				}
			}
		}
		
		return broadcastsToReturn;
	}
	
	
	
	public int getClosestBroadcastIndex(
			final int selectedHour,
			final TVDate tvDate,
			final int defaultValueIfNotFound)
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		for(int i = 0; i < getBroadcasts().size(); ++i)
		{
			TVBroadcast broadcast = getBroadcasts().get(i);
		
			Calendar tvDateWithHourCalendarLocal = DateUtils.buildLocalCalendarWithTVDateAndSelectedHour(tvDate, selectedHour);
			
			boolean isEndTimeAfterTvDateWithHour = broadcast.isEndTimeAfter(tvDateWithHourCalendarLocal);
			
			if(isEndTimeAfterTvDateWithHour)
			{
				closestIndexFound = i;
				break;
			}
		}
		
		return closestIndexFound;
	}
	
	
	
	/**
	 * WARNING WARNING! DUPLICATION OF CODE!
	 * IF YOU CHANGE THIS METHOD YOU MUST CHANGE
	 * ITS SIBLING METHOD HAVING THE SAME NAME.
	 * 
	 * SIBLING METHOD IS IN TVBROADCAST CLASS
	 * WITH TVCHANNEL INFO THAT SUBCLASSES TVBROADCAST
	 */
	public int getClosestBroadcastIndex(final int defaultValueIfNotFound) 
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		for(int i=0; i<getBroadcasts().size(); i++)
		{
			TVBroadcast broadcast = getBroadcasts().get(i);
			
			boolean hasNotAiredYetOrIsAiring = broadcast.hasNotAiredYet() || broadcast.isAiring();
			
			if(hasNotAiredYetOrIsAiring)
			{
				closestIndexFound = i;
				break;
			}
		}
		
		return closestIndexFound;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TVChannelGuide other = (TVChannelGuide) obj;
		if (getChannelId() == null) {
			if (other.getChannelId() != null)
				return false;
		} else if (!getChannelId().equals(other.getChannelId()))
			return false;
		return true;
	}

}