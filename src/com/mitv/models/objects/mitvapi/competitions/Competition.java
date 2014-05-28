
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.CompetitionJSON;
import com.mitv.utilities.DateUtils;



public class Competition 
	extends CompetitionJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Competition.class.getName();
	
	
	protected Calendar beginCalendar;
	protected Calendar endCalendar;
	
	
	
	public Competition(){}
	
	
	
	public boolean hasBegun()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean hasBegun = getBeginTimeCalendarGMT().before(now);
		
		return hasBegun;
	}
	
	
	
	public boolean hasEnded()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean hasEnded = getEndTimeCalendarGMT().before(now);
		
		return hasEnded;
	}
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(startDate);
		
		return beginTimeCalendarGMT;
	}

	
	
	/**
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarGMT()
	{
		Calendar endTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(endDate);
		
		return endTimeCalendarGMT;
	}
	
	
	/**
	 * Lazy instantiated variable
	 * @return The start time of the competition, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarLocal() 
	{
		if(beginCalendar == null) 
		{	
			beginCalendar = getBeginTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			beginCalendar.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return beginCalendar;
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
