
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Event.class.getName();
	
	
	protected Calendar startCalendar;
	
	
	public Event(){}
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getStartTimeCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(startDate);
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The start time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getStartTimeCalendarLocal() 
	{
		if(startCalendar == null)
		{	
			startCalendar = getStartTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			startCalendar.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return startCalendar;
	}
	
	
	
	public boolean isTheSameDayAs(Event other)
	{
		Calendar beginTime1 = this.getStartTimeCalendarLocal();
		Calendar beginTime2 = other.getStartTimeCalendarLocal();
		
		return DateUtils.areCalendarsTheSameTVAiringDay(beginTime1, beginTime2);
	}
	
	
	
	public boolean isBeginTimeTodayOrTomorrow()
	{
		Calendar now = DateUtils.getNow();
		
		Calendar beginTime = this.getStartTimeCalendarLocal();
		
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
	public String getBeginTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getStartTimeCalendarLocal());
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getBeginTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getStartTimeCalendarLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		
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
		
		if (eventId == null) 
		{
			if (other.eventId != null) 
			{
				return false;
			}
		} 
		else if (!eventId.equals(other.eventId)) 
		{
			return false;
		}
		
		return true;
	}
}
