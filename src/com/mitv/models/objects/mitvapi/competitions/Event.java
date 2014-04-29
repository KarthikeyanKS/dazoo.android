
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
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
}
