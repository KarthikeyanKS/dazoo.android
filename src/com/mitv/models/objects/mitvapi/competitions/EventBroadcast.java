
package com.mitv.models.objects.mitvapi.competitions;



import java.util.Calendar;

import android.util.Log;

import com.mitv.managers.ContentManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.utilities.DateUtils;



public class EventBroadcast 
	extends EventBroadcastJSON
{
	private static final String TAG = EventBroadcast.class.getName();
	
	
	
	protected Calendar beginTimeCalendar;
	protected Calendar endTimeCalendar;
	
	
	
	public EventBroadcast(){}
	
	
	
	public EventBroadcast(EventBroadcastJSON ev) 
	{
		this.eventBroadcastId = ev.getEventBroadcastId();
		this.programId = ev.getProgramId();
		this.beginTime = ev.getBeginTime();
		this.beginTimeMillis = ev.getBeginTimeMillis();
		this.endTime = ev.getEndTime();
		this.channelId = ev.getChannelId();
	}

	
	
	/**
	 * Returns the date of begin time for an event broadcast.
	 * 
	 * @return Calendar
	 */
	public Calendar getEventBroadcastBeginTimeLocal()
	{	
		if(beginTimeCalendar == null)
		{	
			beginTimeCalendar = getEventBroadcastBeginTimeGMT();
			
			beginTimeCalendar = DateUtils.setTimeZoneAndOffsetToLocal(beginTimeCalendar);
		}
		
		return beginTimeCalendar;
	}
	
	
	
	public Calendar getEventBroadcastBeginTimeGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(beginTime);
		
		return beginTimeCalendarGMT;
	}
	
	
	
	/**
	 * Returns the date of end time for an event broadcast.
	 * 
	 * @return Calendar
	 */
	public Calendar getEventBroadcastEndTimeLocal() 
	{
		if(endTimeCalendar == null)
		{	
			endTimeCalendar = getEventBroadcastEndTimeGMT();
			
			endTimeCalendar = DateUtils.setTimeZoneAndOffsetToLocal(endTimeCalendar);
		}
		
		return endTimeCalendar;
	}
	
	
	
	public Calendar getEventBroadcastEndTimeGMT() 
	{
		Calendar endTimeCalendarGMT = DateUtils.convertISO8601StringToCalendar(endTime);
		
		return endTimeCalendarGMT;
	}

	
	
	/**
	 * Returns the total time in minutes for an events broadcast.
	 * 
	 * @return int
	 */
	public Integer getTotalAiringTimeInMinutes() 
	{
		int totalMinutes = DateUtils.calculateDifferenceBetween(beginTimeCalendar, endTimeCalendar, Calendar.MINUTE, false, 0);
		
		return totalMinutes;
	}
	
	
	
	public TVChannelId getTVChannelIdForEventBroadcast()
	{
		TVChannelId tvChannelId = new TVChannelId(this.getChannelId());
		
		return tvChannelId;
	}
	
	
	
	private TVChannel getTVChannelForEventBroadcast() 
	{
		TVChannelId tvChannelId = getTVChannelIdForEventBroadcast();
		
		TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
		
		if(tvChannel == null)
		{
			Log.w(TAG, "TVChannel is null");
		}
		
		return tvChannel;
	}
	
	
	
	public String getChannelName() 
	{
		String channelName;
		
		TVChannel tvChannel = getTVChannelForEventBroadcast();
		
		if(tvChannel != null)
		{
			channelName = tvChannel.getName();
		}
		else
		{
			channelName = "";
		}
		
		return channelName;
	}
	
	
	
	
	public String getChannelLogoUrl() 
	{
		String imageURL;
		
		TVChannel tvChannel = getTVChannelForEventBroadcast();
		
		if(tvChannel != null)
		{
			imageURL = tvChannel.getLogo().getImageURLForDeviceDensityDPI();
		}
		else
		{
			imageURL = "";
		}
		
		return imageURL;
	}
	
	
	
	public String getImageUrl() 
	{
		String imageURL;
		
		TVChannel tvChannel = getTVChannelForEventBroadcast();
		
		if(tvChannel != null)
		{
			imageURL = tvChannel.getImageUrl();
		}
		else
		{
			imageURL = "";
		}
		
		return imageURL;
	}
	
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(this.getEventBroadcastBeginTimeLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	
	
	public String getBeginTimeDateRepresentation() 
	{	
		String beginTimeDateRepresentation = DateUtils.buildDateCompositionAsString(this.getEventBroadcastBeginTimeLocal());
		
		return beginTimeDateRepresentation;
	}
	
	
	
	public boolean isEventAiringInLessThan(int minutes) 
	{
		Calendar eventBroadcastBeginTimeGMT = getEventBroadcastBeginTimeGMT();
		
		Calendar eventBroadcastBeginTimeGMTWithNegativeIncrement = (Calendar) (eventBroadcastBeginTimeGMT.clone());

		eventBroadcastBeginTimeGMTWithNegativeIncrement.add(Calendar.MINUTE, -minutes);
		
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean isNowAfter = now.after(eventBroadcastBeginTimeGMTWithNegativeIncrement);
	    boolean isNowBefore = now.before(eventBroadcastBeginTimeGMT);
		
	    boolean isEventAiringInPeriod = (isNowAfter && isNowBefore);
	    
	    return isEventAiringInPeriod;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();

		boolean isAiring = this.getEventBroadcastBeginTimeGMT().before(now) && this.getEventBroadcastEndTimeGMT().after(now);

		return isAiring;
	}
	
	
	
	public boolean hasEnded() 
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		boolean hasEnded = now.after(this.getEventBroadcastEndTimeGMT());
		
		return hasEnded;
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getEventTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(this.getEventBroadcastBeginTimeLocal());
	}
	
	
	
	/**
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getEventTimeDayAndMonthAsString() 
	{
		String beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(this.getEventBroadcastBeginTimeLocal(), false);
		
		return beginTimeDayAndMonthRepresentation;
	}
}
