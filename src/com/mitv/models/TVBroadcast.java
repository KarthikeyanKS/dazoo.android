
package com.mitv.models;



import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.BroadcastJSON;
import com.mitv.utilities.DateUtils;



/**
 * All variables in this class needs to be either transient or use the
 * @Expose(deserialize=false) annotation, else the JSON parsing using GSON will
 * fail.
 * 
 * @author consultant_hdme
 * 
 */
public class TVBroadcast 
	extends BroadcastJSON 
	implements GSONDataFieldValidation 
{
	@SuppressWarnings("unused")
	private static final String TAG = TVBroadcast.class.getName();

	private static final int NO_INT_VALUE_SET = -1;
	
	protected Calendar beginTimeCalendarLocal;
	protected Calendar endTimeCalendarLocal;
	private Integer durationInMinutes = NO_INT_VALUE_SET;
	
	/* IMPORTANT TO SET STRING TO NULL AND NOT EMPTY STRING */
	private String beginTimeDateRepresentation = null;
	private String beginTimeDayAndMonthRepresentation = null;
	private String beginTimeHourAndMinuteRepresentation = null;
	private String endTimeHourAndMinuteRepresentation = null;
	private String title = null;
	
	public String getTitle() {
		if (title == null) {
			if (program != null) {
				ProgramTypeEnum programType = program.getProgramType();

				switch (programType) {
				case TV_EPISODE: {
					TVSeries series = program.getSeries();
					if (series != null) {
						title = series.getName();
					}
					break;
				}
				case MOVIE:
				case SPORT:
				case OTHER: /* Default */
				default: {
					title = program.getTitle();
					break;
				}
				}
			}
		}
		return title;
	}
	
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
		Calendar now = DateUtils.getNow();
		
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
		Calendar now = DateUtils.getNow();
		
		Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(getBeginTimeCalendarLocal(), now, Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public Integer getRemainingMinutesUntilBroadcastEnds() 
	{	    
	    Calendar now = DateUtils.getNow();
		
	    Integer elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(now, getEndTimeCalendarLocal(), Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	
	public Calendar getBroadcastBeginTimeForNotification()
	{
		Calendar calendar = (Calendar) getBeginTimeCalendarLocal().clone();

		int minutesBeforeNotify = Constants.NOTIFY_MINUTES_BEFORE_THE_BROADCAST;
		
		calendar.add(Calendar.MINUTE, -minutesBeforeNotify);
		
		return calendar;
	}
	

	
	public boolean isBroadcastAiringInOrInLessThan(int minutes) 
	{
		Calendar nowWithIncrement = (Calendar) DateUtils.getNow().clone();
		nowWithIncrement.add(Calendar.MINUTE, minutes);
		
		boolean isBroadcastStartingInPeriod = getBeginTimeCalendarLocal().before(nowWithIncrement);
	    
	    return isBroadcastStartingInPeriod;
	}
	
	
	
	public boolean isEndTimeAfter(Calendar inputCalendar)
	{
		boolean isEndTimeAfterInputCalendar = getEndTimeCalendarLocal().after(inputCalendar);

		return isEndTimeAfterInputCalendar;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = DateUtils.getNow();

		boolean isAiring = getBeginTimeCalendarLocal().before(now) && getEndTimeCalendarLocal().after(now);

		return isAiring;
	}

	
	
	public boolean hasEnded()
	{
		Calendar now = DateUtils.getNow();
		
		boolean hasEnded = now.after(getEndTimeCalendarLocal());
		
		return hasEnded;
	}
	
	
	
	public boolean hasNotAiredYet()
	{
		Calendar now = DateUtils.getNow();
		
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
	
	
	public String getBeginTimeHourAndMinuteLocalAsString() 
	{
		if(beginTimeHourAndMinuteRepresentation == null)
		{
			beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendarLocal());
		}
		
		return beginTimeHourAndMinuteRepresentation;
	}
	

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
		
		if (dayOfTheWeekAsString.equalsIgnoreCase(SecondScreenApplication.sharedInstance().getApplicationContext().getString(R.string.today)) == false &&
			dayOfTheWeekAsString.equalsIgnoreCase(SecondScreenApplication.sharedInstance().getApplicationContext().getString(R.string.tomorrow)) == false) {
			String dayAndMonthString = getBeginTimeDayAndMonthAsString();
			sb.append(" ");
			sb.append(dayAndMonthString);
		}

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
		
		Calendar now = DateUtils.getNow();

		int daysLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarLocal(), Calendar.DAY_OF_MONTH, false, 0);

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
			int hoursLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarLocal(), Calendar.HOUR_OF_DAY, false, 0);

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
				int minutesLeft = DateUtils.calculateDifferenceBetween(now, getBeginTimeCalendarLocal(), Calendar.MINUTE, false, 0);

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
					sb.append(res.getString(R.string.search_result_broadcast_has_finished));
				}
			}
		}
		
		return sb.toString();
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beginTimeMillis == null) ? 0 : beginTimeMillis.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BroadcastJSON other = (BroadcastJSON) obj;
		if (beginTimeMillis == null) {
			if (other.getBeginTimeMillis() != null)
				return false;
		} else if (!beginTimeMillis.equals(other.getBeginTimeMillis()))
			return false;
		return true;
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
