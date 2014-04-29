
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.CompetitionJSON;
import com.mitv.utilities.DateUtils;



public class Competition 
	extends CompetitionJSON
{
	protected Calendar startCalendar;
	protected Calendar endCalendar;
	
	
	
	public Competition(){}
	
	
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(startDate);
		
		return beginTimeCalendarGMT;
	}

	
	
	/**
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarGMT()
	{
		Calendar endTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(endDate);
		
		return endTimeCalendarGMT;
	}
	
	
	/**
	 * Lazy instantiated variable
	 * @return The start time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getStartTimeCalendarLocal() 
	{
		if(startCalendar == null) 
		{	
			startCalendar = getBeginTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			startCalendar.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return startCalendar;
	}
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The end time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarLocal()
	{
		if(endCalendar == null)
		{	
			endCalendar = getEndTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			endCalendar.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return endCalendar;
	}
}
