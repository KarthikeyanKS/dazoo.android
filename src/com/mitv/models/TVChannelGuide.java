
package com.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;
import com.mitv.models.gson.TVChannelGuideJSON;
import com.mitv.utilities.DateUtils;
import com.mitv.Constants;



public class TVChannelGuide 
	extends TVChannelGuideJSON
{

	public boolean hasBroadcasts() {
		boolean hasBroadcasts = broadcasts != null && !broadcasts.isEmpty();
		return hasBroadcasts;
	}
	
	public ArrayList<TVBroadcast> getCurrentAndTwoUpcomingBroadcastsUsingSelectedDayAndHour(
			final int hour, 
			final TVDate tvDate) {
		return getCurrentAndUpcomingBroadcastsUsingSelectedDayAndHour(hour, tvDate, Constants.TV_GUIDE_NEXT_PROGRAMS_NUMBER);
	}
	
	public ArrayList<TVBroadcast> getCurrentAndUpcomingBroadcastsUsingSelectedDayAndHour(
			final int hour, 
			final TVDate tvDate,
			int howManyIncludingCurrent) {
		int indexOfNearestBroadcast = getClosestBroadcastIndex(hour, tvDate, 0);
		ArrayList<TVBroadcast> threeNextBroadcasts = getBroadcastsFromPosition(indexOfNearestBroadcast, howManyIncludingCurrent);
		return threeNextBroadcasts;
	}
	
	public ArrayList<TVBroadcast> getBroadcastsFromPosition(
			final int startIndex) 
	{
		return getBroadcastsFromPosition(startIndex, broadcasts.size());
	}
	
	/**
	 *  This method does not use the selected TV Hour, it uses the current time and returns
	 *  the current broadcast and the upcoming ones.
	 * @return
	 */
	public ArrayList<TVBroadcast> getCurrentAndUpcomingBroadcastsUsingCurrentTime() {
		ArrayList<TVBroadcast> currentAndUpcomingbroadcasts = null;
		
		int indexIfNotFound = -1;
		int indexOfNearestBroadcast = getClosestBroadcastIndex(indexIfNotFound);
		if (indexOfNearestBroadcast > indexIfNotFound) 
		{
			currentAndUpcomingbroadcasts = getBroadcastsFromPosition(indexOfNearestBroadcast);
		}
		
		return currentAndUpcomingbroadcasts;
	}
	

	public ArrayList<TVBroadcast> getBroadcastsFromPosition(
			final int startIndex,
			final int maximumBrodacasts) 
	{
		ArrayList<TVBroadcast> broadcastsToReturn = new ArrayList<TVBroadcast>(maximumBrodacasts);

		if(startIndex >= 0 && 
		   startIndex < broadcasts.size())
		{
			for(int i=startIndex; i<broadcasts.size(); i++)
			{
				if(broadcastsToReturn.size() < maximumBrodacasts)
				{
					TVBroadcast broadcast = broadcasts.get(i);
					
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
			final int hour, 
			final TVDate tvDate,
			final int defaultValueIfNotFound)
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		Calendar tvDateWithHourCalendar = DateUtils.buildCalendarWithTVDateAndSpecificHour(tvDate.getDateCalendar(), hour);
		
		for(int i = 0; i < broadcasts.size(); ++i)
		{
			TVBroadcast broadcast = broadcasts.get(i);
			
			boolean isEndTimeAfterTvDateWithHour = broadcast.isEndTimeAfter(tvDateWithHourCalendar);
			
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
	public int getClosestBroadcastIndex(
			final int defaultValueIfNotFound) 
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		for(int i=0; i<broadcasts.size(); i++)
		{
			TVBroadcast broadcast = broadcasts.get(i);
			
			boolean hasNotAiredYetOrIsAiring = broadcast.hasNotAiredYet() || broadcast.isAiring();
			
			if(hasNotAiredYetOrIsAiring)
			{
				closestIndexFound = i;
				break;
			}
		}
		
		return closestIndexFound;
	}	

}