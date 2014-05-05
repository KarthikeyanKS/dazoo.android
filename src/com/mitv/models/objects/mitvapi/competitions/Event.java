
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
