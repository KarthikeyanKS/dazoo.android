package com.mitv.models.objects.mitvapi.competitions;

import java.util.Calendar;

import android.content.Context;

import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.sql.NotificationSQLElement;
import com.mitv.utilities.DateUtils;

public class EventBroadcastDetails extends EventBroadcastDetailsJSON {
	
	protected Calendar beginTimeCalendar;
	protected Calendar endTimeCalendar;
	
	
	
	public EventBroadcastDetails() {}
	
	public EventBroadcastDetails(EventBroadcastDetailsJSON ev) 
	{
		this.beginTime = ev.getBeginTime();
		this.beginTimeMillis = ev.getBeginTimeMillis();
		this.channelId = ev.getChannelId();
		this.endTime = ev.getEndTime();
		this.broadcastId = ev.getBroadcastId();
	}
	
	
	
	public EventBroadcastDetails(NotificationSQLElement item)
	{
//		TVChannel tvChannel = new TVChannel(item);
//		this.channel = tvChannel;
//		
//		TVProgram tvProgram = new TVProgram(item);
//		this.program = tvProgram;
//		
//		String broadcastTypeAsString = item.getBroadcastType();
//		
//		this.broadcastType = BroadcastTypeEnum.getBroadcastTypeEnumFromStringRepresentation(broadcastTypeAsString);
//		this.beginTimeMillis = item.getBroadcastBeginTimeInMilliseconds();
//		this.beginTime = item.getBroadcastBeginTime();
//		this.endTime = item.getBroadcastEndTime();
//		this.shareUrl = item.getShareUrl();
	}
	
	
	/**
	 * Returns the date of begin time for an event broadcast.
	 * 
	 * @return Calendar
	 */
	public Calendar getEventBroadcastBeginTimeLocal() {		
		beginTimeCalendar = getEventBroadcastCalendar(beginTime);
		
		return beginTimeCalendar;
	}
	
	
	
	/**
	 * Returns the date of end time for an event broadcast.
	 * 
	 * @return Calendar
	 */
	public Calendar getEventBroadcastEndTimeLocal() {
		endTimeCalendar = getEventBroadcastCalendar(endTime);
		
		return endTimeCalendar;
	}
	
	
	
	/**
	 * Get local time of begin and end time of an event.
	 * Taking time zone offset into account.
	 * 
	 * @param date
	 * @return
	 */
	private Calendar getEventBroadcastCalendar(String date) {
		Calendar cal = DateUtils.convertFromYearDateAndTimeStringToCalendar(date);
		
		int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
		
		cal.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		
		return cal;
	}
	
	
	
	/**
	 * Returns the total time in minutes for an events broadcast.
	 * 
	 * @return int
	 */
	public Integer getTotalAiringTimeInMinutes() {
		int totalMinutes = DateUtils.calculateDifferenceBetween(
				beginTimeCalendar, endTimeCalendar, Calendar.MINUTE, false, 0);
		
		return totalMinutes;
	}
	
	
	
	public TVChannelId getTVChannelIdForEventBroadcast() {
		TVChannelId tvChannelId = new TVChannelId(this.getChannelId());
		
		return tvChannelId;
	}
	
	
	
	public TVChannel getTVChannelForEventBroadcast() {
		TVChannelId tvChannelId = getTVChannelIdForEventBroadcast();
		
		TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
		
		return tvChannel;
	}
	
	
	
	public String getChannelName() {
		TVChannel tvChannel = getTVChannelForEventBroadcast();
		
		return tvChannel.getName();
	}
	
	
	
	public String getImageUrl() {
		TVChannel tvChannel = getTVChannelForEventBroadcast();
		
		return tvChannel.getImageUrl();
	}
	
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		String beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(this.getEventBroadcastBeginTimeLocal());
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	
	
	public String getBeginTimeDateRepresentation() 
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
		String beginTimeDateRepresentation = DateUtils.buildDateCompositionAsString(this.getEventBroadcastBeginTimeLocal(), context);
		
		return beginTimeDateRepresentation;
	}
	
	
	
	public boolean isEventAiringInOrInLessThan(int minutes) 
	{
		Calendar nowWithIncrement = (Calendar) DateUtils.getNow().clone();
		nowWithIncrement.add(Calendar.MINUTE, minutes);
		
		boolean isBroadcastStartingInPeriod = this.getEventBroadcastBeginTimeLocal().before(nowWithIncrement);
	    
	    return isBroadcastStartingInPeriod;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = DateUtils.getNow();

		boolean isAiring = this.getEventBroadcastBeginTimeLocal().before(now) && this.getEventBroadcastEndTimeLocal().after(now);

		return isAiring;
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
