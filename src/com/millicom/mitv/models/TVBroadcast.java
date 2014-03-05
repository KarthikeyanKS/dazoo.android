
package com.millicom.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.BroadcastJSON;
import com.millicom.mitv.utilities.DateUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



/**
 * All variables in this class needs to be either transient or use the
 * @Expose(deserialize=false) annotation, else the JSON parsing using GSON will
 * fail.
 * 
 * @author consultant_hdme
 * 
 */
public class TVBroadcast extends BroadcastJSON implements GSONDataFieldValidation {
	@SuppressWarnings("unused")
	private static final String TAG = TVBroadcast.class.getName();

	private static final int NO_INT_VALUE_SET = -1;
	
	protected transient Calendar beginTimeCalendarLocal;
	protected transient Calendar endTimeCalendarLocal;
	private transient Integer durationInMinutes = NO_INT_VALUE_SET;
	
	/* IMPORTANT TO SET STRING TO NULL AND NOT EMPTY STRING */
	private transient String beginTimeDateRepresentation = null;
	private transient String beginTimeDayAndMonthRepresentation = null;
	private transient String beginTimeHourAndMinuteRepresentation = null;
	private transient String endTimeHourAndMinuteRepresentation = null;
	
	
	
	/**
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarGMT() 
	{
		Calendar beginTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(beginTime);
		return beginTimeCalendarGMT;
	}

	
	
	/**
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarGMT()
	{
		Calendar endTimeCalendarGMT = DateUtils.convertFromYearDateAndTimeStringToCalendar(endTime);
		return endTimeCalendarGMT;
	}

	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendarLocal() 
	{
		if(beginTimeCalendarLocal == null) 
		{	
			beginTimeCalendarLocal = getBeginTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			beginTimeCalendarLocal.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return beginTimeCalendarLocal;
	}
	
	/**
	 * Lazy instantiated variable
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendarLocal()
	{
		if(endTimeCalendarLocal == null)
		{	
			endTimeCalendarLocal = getEndTimeCalendarGMT();
			
			int timeZoneOffsetInMinutes = DateUtils.getTimeZoneOffsetInMinutes();
			endTimeCalendarLocal.add(Calendar.MINUTE, timeZoneOffsetInMinutes);
		}
		
		return endTimeCalendarLocal;
	}
	
	
	public boolean isBroadcastCurrentlyAiring() 
	{
		Calendar now = Calendar.getInstance();
		
		boolean isRunning = getBeginTimeCalendarLocal().before(now) && getEndTimeCalendarLocal().after(now);		

		return isRunning;
	}

	
	
	public Integer getBroadcastDurationInMinutes() 
	{
		if(durationInMinutes == NO_INT_VALUE_SET)
		{		    
		    durationInMinutes = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), getEndTimeCalendarLocal(), Calendar.MINUTE, false, 0);
		}

	    return durationInMinutes;
	}
	
	
	
	public Integer getElapsedMinutesSinceBroadcastStarted() 
	{
		Calendar now = Calendar.getInstance();
		
		Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), now, Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public Integer getRemainingMinutesUntilBroadcastEnds() 
	{	    
	    Calendar now = Calendar.getInstance();
		
	    Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(now, getEndTimeCalendarLocal(), Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	
	public Calendar getBroadcastBeginTimeForNotification()
	{
		Calendar calendar = (Calendar) getBeginTimeCalendarLocal().clone();

		int minutesBeforeNotify = Consts.NOTIFY_MINUTES_BEFORE_THE_BROADCAST2;
		
		calendar.add(Calendar.MINUTE, -minutesBeforeNotify);
		
		return calendar;
	}
	

	
	public boolean isBroadcastAiringInOrInLessThan(int minutes) 
	{
		Calendar beginTimeWithincrement = (Calendar) getBeginTimeCalendarLocal().clone();
		beginTimeWithincrement.add(Calendar.MINUTE, minutes);
		
		Calendar now = Calendar.getInstance();
				
		boolean isBroadcastStartingInPeriod = beginTimeWithincrement.before(now);
	    
	    return isBroadcastStartingInPeriod;
	}
	
	
	
	public boolean isEndTimeAfter(Calendar inputCalendar)
	{
		boolean isEndTimeAfterInputCalendar = getEndTimeCalendarLocal().after(inputCalendar);

		return isEndTimeAfterInputCalendar;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = Calendar.getInstance();

		boolean isAiring = getBeginTimeCalendarLocal().before(now) && getEndTimeCalendarLocal().after(now);

		return isAiring;
	}

	
	
	public boolean hasEnded()
	{
		Calendar now = Calendar.getInstance();
		
		boolean hasEnded = now.after(getEndTimeCalendarLocal());
		
		return hasEnded;
	}
	
	
	
	public boolean hasNotAiredYet()
	{
		Calendar now = Calendar.getInstance();
		
		boolean hasNotAiredYet = now.before(getBeginTimeCalendarLocal());
		
		return hasNotAiredYet;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "yyyy-MM-dd"
	 */
	public String getBeginTimeDateRepresentation() 
	{
		if(beginTimeDateRepresentation == null)
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
			beginTimeDateRepresentation = DateUtils.buildDateCompositionAsString(getBeginTimeCalendarLocal(), context);
		}
		
		return beginTimeDateRepresentation;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "dd/MM"
	 */
	public String getBeginTimeDayAndMonthAsString() 
	{
		if(beginTimeDayAndMonthRepresentation == null)
		{
			beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getBeginTimeCalendarLocal());
		}
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "HH:mm" or "HH:mm a"
	 */
	private String getBeginTimeHourAndMinuteGMTAsString()
	{	
		if(beginTimeHourAndMinuteRepresentation == null)
		{
			beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		}
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	/*
	 * Returns a string representation of the end time calendar in the format "HH:mm" or "HH:mm a"
	 */
	private String getEndTimeHourAndMinuteGMTAsString() 
	{
		if(endTimeHourAndMinuteRepresentation == null)
		{
			endTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getEndTimeCalendarLocal());
		}
		
		return endTimeHourAndMinuteRepresentation;
	}
	
	//TODO NewArc finish me!
	public String getBeginTimeHourAndMinuteLocalAsString() {
		if(beginTimeHourAndMinuteRepresentation == null)
		{
			beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		}
		
		return beginTimeHourAndMinuteRepresentation;
	}
	

	//TODO NewArc finish me!
	public String getEndTimeHourAndMinuteLocalAsString() {
		if(endTimeHourAndMinuteRepresentation == null)
		{
			endTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getEndTimeCalendarLocal());
		}
		
		return endTimeHourAndMinuteRepresentation;
	}
	
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal());
	}

	
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekWithHourAndMinuteAsString()
	{	
		String dayOfTheWeekAsString = DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendarLocal());
		
		String timeOfDayAsString = getBeginTimeHourAndMinuteLocalAsString();
		
		StringBuilder sb = new StringBuilder();
		sb.append(dayOfTheWeekAsString);
		sb.append(", ");
		sb.append(timeOfDayAsString);
		
		return sb.toString();
	}
	
	
	
	/*
	 * Returns a string representation of the total amount of time remaining for the broadcast to begin.
	 * The representation can contain a formated number of days, hours, minutes, or if the broadcast is already finished.
	 * 
	 */
	public String getStartingTimeAsString()
	{
		Resources res = SecondScreenApplication.sharedInstance().getApplicationContext().getResources();
		
		StringBuilder sb = new StringBuilder();
		
		Calendar now = Calendar.getInstance();

		int daysLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), now, Calendar.DAY_OF_MONTH, false, 0);

		if(daysLeft > 0)
		{
			sb.append(res.getString(R.string.search_starts_in));
			sb.append(" ");
			sb.append(daysLeft);
			sb.append(" ");
			sb.append(res.getQuantityString(R.plurals.day, daysLeft));
		} 
		else 
		{
			int hoursLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), now, Calendar.HOUR_OF_DAY, false, 0);

			if(hoursLeft > 0) 
			{
				sb.append(res.getString(R.string.search_starts_in));
				sb.append(" ");
				sb.append(hoursLeft);
				sb.append(" ");
				sb.append(res.getQuantityString(R.plurals.hour, hoursLeft));
			} 
			else 
			{
				int minutesLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), now, Calendar.MINUTE, false, 0);

				if(minutesLeft > 0) 
				{
					sb.append(res.getString(R.string.search_starts_in));
					sb.append(" ");
					sb.append(minutesLeft);
					sb.append(" ");
					sb.append(res.getString(R.string.minutes));
				} 
				else 
				{
					// TODO NewArc - Move this into resources
					sb.append("Has finished");
				}
			}
		}
		
		return sb.toString();
	}
		

	/* 
	 * Comparison of the broadcasts is done using the begin time of each one 
	 * 
	 * */
	public static class BroadcastComparatorByTime 
		implements Comparator<TVBroadcast> 
	{
		@Override
		public int compare(TVBroadcast lhs, TVBroadcast rhs) 
		{
			long left = lhs.getBeginTimeMillis();
			long right = rhs.getBeginTimeMillis();

			if (left > right) 
			{
				return 1;
			} 
			else if (left < right) 
			{
				return -1;
			} 
			else 
			{
				String leftProgramName = lhs.getProgram().getTitle();
				String rightProgramName = rhs.getProgram().getTitle();
				return leftProgramName.compareTo(rightProgramName);
			}
		}
	}



	@Override
	public boolean areDataFieldsValid() {
		final int yearOf2000 = 2000;
		boolean tvProgramOk = true;
		if(getProgram() != null) {
			tvProgramOk = getProgram().areDataFieldsValid();
		}
		
		
		boolean broadcastFields = (tvProgramOk && (getBeginTimeMillis() != null) && 
				!TextUtils.isEmpty(getBeginTime()) && !TextUtils.isEmpty(getEndTime()) &&
				(getBroadcastType() != BroadcastTypeEnum.UNKNOWN) && !TextUtils.isEmpty(getShareUrl())
				);
		
		boolean additionalFieldsOk = (
//				getBeginTimeCalendarGMT() != null && (
//						getBeginTimeCalendarGMT().get(Calendar.YEAR) > yearOf2000)  && getEndTimeCalendarGMT() != null &&
//						(getEndTimeCalendarGMT().get(Calendar.YEAR) > yearOf2000) &&
						getBeginTimeCalendarLocal() != null && (getBeginTimeCalendarLocal().get(Calendar.YEAR) > yearOf2000)  && getEndTimeCalendarLocal() != null &&
						(getEndTimeCalendarLocal().get(Calendar.YEAR) > yearOf2000)  &&
						getBroadcastDurationInMinutes() != null &&
						!TextUtils.isEmpty(getBeginTimeDateRepresentation()) && !TextUtils.isEmpty(getBeginTimeDayAndMonthAsString()) &&
						!TextUtils.isEmpty(getBeginTimeHourAndMinuteLocalAsString()) && !TextUtils.isEmpty(getEndTimeHourAndMinuteLocalAsString())
						);
		
		boolean areDataFieldsValid = broadcastFields && additionalFieldsOk;
		
		return areDataFieldsValid;
	}
}
