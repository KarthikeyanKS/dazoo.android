package com.mitv.models.objects.mitvapi.competitions;

import java.util.Calendar;

import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.utilities.DateUtils;

public class EventBroadcastDetails extends EventBroadcastDetailsJSON {
	
	protected Calendar beginTimeCalendar;
	protected Calendar endTimeCalendar;
	
	
	
	public EventBroadcastDetails() {}
	
	
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
	
	
	
	/**
	 * Returns the time in minutes the has been going on.
	 * 
	 * @return minutesInGame
	 */
	public int getMinutesInGame() {
		int minutesInGame = 0;
		long minutes = 0l;
		
		Calendar now = DateUtils.getNow();
		
		minutes = now.getTimeInMillis() - this.getEventBroadcastBeginTimeLocal().getTimeInMillis();
		
		minutesInGame = (int) (minutes / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
		return minutesInGame;
	}
	
	
	
	
	public String getMinutesInGameString() {
		int minutesinGame = getMinutesInGame();
		
		StringBuilder sb = new StringBuilder();
		sb.append(minutesinGame)
			.append("'");
		
		return sb.toString();
	}

}
