
package com.mitv.models.objects.mitvapi.competitions;



import java.util.ArrayList;
import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Event.class.getName();
	
	
	protected Calendar startCalendar;
	
	
	public Event()
	{
		// TODO - Remove this
		if(this.broadcastDetails == null)
		{
			this.broadcastDetails = new ArrayList<EventBroadcastDetailsJSON>();
			
			EventBroadcastDetailsJSON eventBroadcastDetailsJSON1 = new EventBroadcastDetailsJSON("co_5184009a-0871-43bd-a99f-1dacc55215f4", "2014-05-07", "1380962800000");
			EventBroadcastDetailsJSON eventBroadcastDetailsJSON2 = new EventBroadcastDetailsJSON("co_892f1538-aac8-46cb-a8dc-44f4b0668d57", "2014-05-09", "5480962608032");
			EventBroadcastDetailsJSON eventBroadcastDetailsJSON3 = new EventBroadcastDetailsJSON("co_34b84cce-de14-4db8-abb6-06b09c51233a", "2014-05-10", "1380962800000");
			
			this.broadcastDetails.add(eventBroadcastDetailsJSON1);
			this.broadcastDetails.add(eventBroadcastDetailsJSON2);
			this.broadcastDetails.add(eventBroadcastDetailsJSON3);
		}
	}
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEventDateCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(eventDate);
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The start time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getEventDateCalendarLocal() 
	{
		if(startCalendar == null)
		{	
			startCalendar = getEventDateCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			startCalendar.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return startCalendar;
	}
	
	
	
	public boolean isTheSameDayAs(Event other)
	{
		Calendar beginTime1 = this.getEventDateCalendarLocal();
		Calendar beginTime2 = other.getEventDateCalendarLocal();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}
	
	
	
	public boolean isEventTimeTodayOrTomorrow()
	{
		Calendar now = DateUtils.getNow();
		
		Calendar beginTime = this.getEventDateCalendarLocal();
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - beginTime.get(Calendar.YEAR)) <= 1;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - beginTime.get(Calendar.MONTH)) <= 1;
    	boolean isSameDay = DateUtils.areCalendarsTheSameTVAiringDay(beginTime, now);
    	
		boolean isAiringToday = isCorrectYear && isCorrectMonth && isSameDay;
		boolean isAiringTomorrow = false;
		
		if (isAiringToday == false)
		{
			Calendar tomorrow = (Calendar) now.clone();
	 		
			tomorrow.add(Calendar.DAY_OF_MONTH, 1);

			isSameDay = DateUtils.areCalendarsTheSameTVAiringDay(beginTime, tomorrow);
	 		
			isAiringTomorrow = isCorrectYear && isCorrectMonth && isSameDay;
		}
		
		boolean isBeginTimeTodayOrTomorrow = (isAiringToday || isAiringTomorrow);
		
		return isBeginTimeTodayOrTomorrow;
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getEventTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getEventDateCalendarLocal());
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getEventTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getEventDateCalendarLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + (int) eventId;
		
		return result;
	}

	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		Event other = (Event) obj;
		
		if (eventId != other.eventId) 
		{
			return false;
		}
		
		return true;
	}
	
	
	
	/**
	 * Compares previous or next team with current team to check if they belongs to the same phase or not.
	 * 
	 * @param other
	 * @return
	 */
	public boolean isSamePhase(Event other) 
	{
		boolean isSamePhase = false;
		
		if (this.phaseId == other.getPhaseId()) 
		{
			isSamePhase = true;
		}
		
		return isSamePhase;
	}
}
