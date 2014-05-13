
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;
import java.util.Date;

import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Event.class.getName();
	
	
	protected Calendar startCalendar;
	
	
	public Event()
	{}
	
	
	
	public boolean containsBroadcastDetails()
	{
		return (broadcastDetails != null);
	}
	
	
	
	public boolean containsTeamInfo()
	{
		if(homeTeamId == 0 || awayTeamId == 0)
		{
			return false;
		}
		else
		{
			return true;
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
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - beginTime.get(Calendar.YEAR)) == 0;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - beginTime.get(Calendar.MONTH)) == 0;
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
	
	
	
	/**
	 * Returns the time in minutes the has been going on.
	 * 
	 * @return minutesInGame
	 */
	public int countMinutesInGame(Calendar beginTime) {
		int minutesInGame = 0;
		long minutes = 0l;
		
		Calendar now = DateUtils.getNow();
		
		minutes = now.getTimeInMillis() - beginTime.getTimeInMillis();
		
		if (minutes > 0) {
			minutesInGame = (int) (minutes / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		}
		
		return minutesInGame;
	}
	
	
	
	public int totalMinutesOfEvent(Calendar beginTime, Calendar endTime) {
		int totalMinutes = 0;
		
		return totalMinutes;
	}
}
