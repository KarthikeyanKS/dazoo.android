
package com.mitv.models.objects.mitvapi.competitions;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.gson.mitvapi.competitions.EventJSON;
import com.mitv.models.sql.NotificationSQLElement;
import com.mitv.utilities.DateUtils;



public class Event
	extends EventJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Event.class.getName();
	
	
	protected Calendar startCalendar;
	
	
	public Event()
	{
		if (broadcastDetails == null) {
			String channelId;
			String beginTime;
			long beginTimeMillis;
			String broadcastId;
			String endTime;
			EventBroadcastDetailsJSON sdf;
			
			channelId = "co_d9ff9f53-fe42-45d2-b532-e74932de5f82";
			beginTime = "2014-05-19T09:00:00Z";
			beginTimeMillis = 1400122800000l;
			broadcastId = "co_98693b86-0dbc-45e9-af4b-9547e620fb08"; //programid
			endTime = "2014-05-19T11:00:00Z";
			sdf = new EventBroadcastDetailsJSON(channelId, beginTime, beginTimeMillis, broadcastId, endTime);
			broadcastDetails = new ArrayList<EventBroadcastDetailsJSON>();
			broadcastDetails.add(sdf);
			
			channelId = "co_d9ff9f53-fe42-45d2-b532-e74932de5f82";
			beginTime = "2014-06-13T04:00:00Z";
			beginTimeMillis = 1402632000000l;
			broadcastId = "co_98693b86-0dbc-45e9-af4b-9547e620fb08"; //programid
			endTime = "2014-06-13T06:00:00Z";
			sdf = new EventBroadcastDetailsJSON(channelId, beginTime, beginTimeMillis, broadcastId, endTime);
			
			broadcastDetails.add(sdf);
		}
	}
	
	
	
	public Event(NotificationSQLElement item)
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
	
	
	
	public List<EventBroadcastDetails> getEventBroadcastDetails()
	{
		List<EventBroadcastDetails> list = new ArrayList<EventBroadcastDetails>();
		
		for (EventBroadcastDetailsJSON ev : broadcastDetails) 
		{
			EventBroadcastDetails element = new EventBroadcastDetails(ev);
			list.add(element);
		}
		
		return list;
	}
	
	
	
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
	
	
	public boolean hasStarted()
	{
		return live;
	}
	
	
	
	public boolean hasEnded()
	{
		return finished;
	}
	
	
	
	public String getScoreAsString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(homeGoals);
		sb.append(" - ");
		sb.append(awayGoals);
		
		return sb.toString();
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
	 * Maybe remove this if we get the timeMillis from BE!!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * @return
	 */
	public long getBeginTimeLocalInMillis() {
		long beginTimeMillis = this.getEventDateCalendarLocal().getTimeInMillis();
		
		return beginTimeMillis;
	}
	
}
