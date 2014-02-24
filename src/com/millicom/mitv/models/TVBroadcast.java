
package com.millicom.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import android.content.Context;
import android.content.res.Resources;

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
public class TVBroadcast extends BroadcastJSON {
	@SuppressWarnings("unused")
	private static final String TAG = TVBroadcast.class.getName();

	private static final int NO_INT_VALUE_SET = -1;
	
	protected transient Calendar beginTimeCalendar;
	protected transient Calendar endTimeCalendar;
	private transient int durationInMinutes = NO_INT_VALUE_SET;
	
	/* IMPORTANT TO SET STRING TO NULL AND NOT EMPTY STRING */
	private transient String beginTimeDateRepresentation = null;
	private transient String beginTimeDayAndMonthRepresentation = null;
	private transient String beginTimeHourAndMinuteRepresentation = null;
	private transient String endTimeHourAndMinuteRepresentation = null;
	
	
	
	/**
	 * Lazy instantiated variable
	 * @return The begin time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getBeginTimeCalendar() 
	{
		if(beginTimeCalendar == null) 
		{	
			beginTimeCalendar = DateUtils.convertFromStringToCalendar(beginTime);
		}
		
		return beginTimeCalendar;
	}

	
	
	/**
	 * Lazy instantiated variable
	 * @return The end time of the broadcast, if available. Otherwise, the current time
	 */
	public Calendar getEndTimeCalendar()
	{
		if(endTimeCalendar == null)
		{	
			endTimeCalendar = DateUtils.convertFromStringToCalendar(endTime);
		}
		
		return endTimeCalendar;
	}

	
	
	public boolean isBroadcastCurrentlyAiring() 
	{
		Calendar now = Calendar.getInstance();
		
		boolean isRunning = getBeginTimeCalendar().before(now) && getEndTimeCalendar().after(now);		

		return isRunning;
	}

	
	
	public int getBroadcastDurationInMinutes() 
	{
		if(durationInMinutes == NO_INT_VALUE_SET)
		{		    
		    durationInMinutes = DateUtils.calculateDifferenceBetween(getBeginTimeCalendar(), getEndTimeCalendar(), Calendar.MINUTE, false, 0);
		}

	    return durationInMinutes;
	}
	
	
	
	public int getElapsedMinutesSinceBroadcastStarted() 
	{
		Calendar now = Calendar.getInstance();
		
	    int elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(getBeginTimeCalendar(), now, Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public int getRemainingMinutesUntilBroadcastEnds() 
	{	    
	    Calendar now = Calendar.getInstance();
		
	    int elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceBetween(now, getEndTimeCalendar(), Calendar.MINUTE, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	
	public Calendar getBroadcastBeginTimeForNotification()
	{
		Calendar calendar = (Calendar) getBeginTimeCalendar().clone();

		int minutesBeforeNotify = Consts.NOTIFY_MINUTES_BEFORE_THE_BROADCAST2;
		
		calendar.add(Calendar.MINUTE, -minutesBeforeNotify);
		
		return calendar;
	}
	

	
	public boolean isBroadcastAiringInOrInLessThan(int minutes) 
	{
		Calendar beginTimeWithincrement = (Calendar) getBeginTimeCalendar().clone();
		beginTimeWithincrement.add(Calendar.MINUTE, minutes);
		
		Calendar now = Calendar.getInstance();
				
		boolean isBroadcastStartingInPeriod = beginTimeWithincrement.before(now);
	    
	    return isBroadcastStartingInPeriod;
	}
	
	
	
	public boolean isEndTimeAfter(Calendar inputCalendar)
	{
		boolean isEndTimeAfterInputCalendar = getEndTimeCalendar().after(inputCalendar);

		return isEndTimeAfterInputCalendar;
	}
	
	
	
	public boolean isAiring() 
	{
		Calendar now = Calendar.getInstance();

		boolean isAiring = getBeginTimeCalendar().before(now) && getEndTimeCalendar().after(now);

		return isAiring;
	}

	
	
	public boolean hasEnded()
	{
		Calendar now = Calendar.getInstance();
		
		boolean hasEnded = now.after(getEndTimeCalendar());
		
		return hasEnded;
	}
	
	
	
	public boolean hasNotAiredYet()
	{
		Calendar now = Calendar.getInstance();
		
		boolean hasNotAiredYet = now.before(getBeginTimeCalendar());
		
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
			
			beginTimeDateRepresentation = DateUtils.buildDateCompositionAsString(getBeginTimeCalendar(), context);
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
			beginTimeDayAndMonthRepresentation = DateUtils.buildDayAndMonthCompositionAsString(getBeginTimeCalendar());
		}
		
		return beginTimeDayAndMonthRepresentation;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "HH:mm" or "HH:mm a"
	 */
	public String getBeginTimeHourAndMinuteAsString()
	{	
		if(beginTimeHourAndMinuteRepresentation == null)
		{
			beginTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendar());
		}
		
		return beginTimeHourAndMinuteRepresentation;
	}
	
	
	/*
	 * Returns a string representation of the end time calendar in the format "HH:mm" or "HH:mm a"
	 */
	public String getEndTimeHourAndMinuteAsString() 
	{
		if(endTimeHourAndMinuteRepresentation == null)
		{
			endTimeHourAndMinuteRepresentation = DateUtils.getHourAndMinuteCompositionAsString(getEndTimeCalendar());
		}
		
		return endTimeHourAndMinuteRepresentation;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekAsString() 
	{	
		return DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendar());
	}

	
	
	/*
	 * Returns a string representation of the begin time calendar day of the week, with a localized representation if the day
	 * is today or tomorrow (per comparison with the current time)
	 */
	public String getBeginTimeDayOfTheWeekWithHourAndMinuteAsString()
	{	
		String dayOfTheWeekAsString = DateUtils.buildDayOfTheWeekAsString(getBeginTimeCalendar());
		
		String timeOfDayAsString = getBeginTimeHourAndMinuteAsString();
		
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

		int daysLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendar(), now, Calendar.DAY_OF_MONTH, false, 0);

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
			int hoursLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendar(), now, Calendar.HOUR_OF_DAY, false, 0);

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
				int minutesLeft = DateUtils.calculateDifferenceBetween(getBeginTimeCalendar(), now, Calendar.MINUTE, false, 0);

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
	 * STATIC UTILITY METHODS
	 */
	
	public static ArrayList<TVBroadcast> getBroadcastsFromPosition(
			final ArrayList<TVBroadcast> broadcasts, 
			final int startIndex) 
	{
		return getBroadcastsFromPosition(broadcasts, startIndex, broadcasts.size());
	}
	
	

	public static ArrayList<TVBroadcast> getBroadcastsFromPosition(
			final ArrayList<TVBroadcast> broadcasts, 
			final int startIndex,
			final int maximumBrodacasts) 
	{
		ArrayList<TVBroadcast> broadcastsToReturn = new ArrayList<TVBroadcast>(maximumBrodacasts);

		if(startIndex >= 0 && 
		   startIndex < broadcasts.size())
		{
			for(int i=startIndex; i<broadcasts.size(); i++)
			{
				if(broadcastsToReturn.size() < maximumBrodacasts)
				{
					TVBroadcast broadcast = broadcasts.get(i);
					
					broadcastsToReturn.add(broadcast);
				}
				else
				{
					break;
				}
			}
		}
		
		return broadcastsToReturn;
	}
	
	
	
	public static int getClosestBroadcastIndex(
			final ArrayList<TVBroadcast> broadcasts, 
			final int hour, 
			final TVDate tvDate,
			final int defaultValueIfNotFound)
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		Calendar tvDateWithHourCalendar = DateUtils.buildCalendarWithDateAndSpecificHour(tvDate.getDateCalendar(), hour);
		
		for(int i=0; i<broadcasts.size(); i++)
		{
			TVBroadcast broadcast = broadcasts.get(i);
			
			boolean isEndTimeAfterTvDateWithHour = broadcast.isEndTimeAfter(tvDateWithHourCalendar);
			
			if(isEndTimeAfterTvDateWithHour)
			{
				closestIndexFound = i;
				break;
			}
		}
		
		return closestIndexFound;
	}
	
	
	
	/**
	 * WARNING WARNING! DUPLICATION OF CODE!
	 * IF YOU CHANGE THIS METHOD YOU MUST CHANGE
	 * ITS SIBLING METHOD HAVING THE SAME NAME.
	 * 
	 * SIBLING METHOD IS IN TVBROADCAST CLASS
	 * WITH TVCHANNEL INFO THAT SUBCLASSES TVBROADCAST
	 */
	public static int getClosestBroadcastIndex(
			final ArrayList<TVBroadcast> broadcasts,
			final int defaultValueIfNotFound) 
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		for(int i=0; i<broadcasts.size(); i++)
		{
			TVBroadcast broadcast = broadcasts.get(i);
			
			boolean hasNotAiredYetOrIsAiring = broadcast.hasNotAiredYet() || broadcast.isAiring();
			
			if(hasNotAiredYetOrIsAiring)
			{
				closestIndexFound = i;
				break;
			}
		}
		
		return closestIndexFound;
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
}
