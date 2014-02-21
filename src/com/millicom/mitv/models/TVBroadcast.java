
package com.millicom.mitv.models;



import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import com.millicom.mitv.models.gson.BroadcastJSON;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVDate;
import com.millicom.mitv.utilities.DateUtils;
import com.mitv.Consts;



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
{
	@SuppressWarnings("unused")
	private static final String TAG = TVBroadcast.class.getName();

	private static final int NO_INT_VALUE_SET = -1;
	private static final String NO_STRING_VALUE_SET = null;
	
	protected transient Calendar beginTimeCalendar;
	protected transient Calendar endTimeCalendar;
	private transient int durationInMinutes = NO_INT_VALUE_SET;
	private transient String beginTimeDateRepresentation = NO_STRING_VALUE_SET;
	
	
	
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
		    durationInMinutes = DateUtils.calculateDifferenceInMinutesBetween(getBeginTimeCalendar(), getEndTimeCalendar(), false, 0);
		}

	    return durationInMinutes;
	}
	
	
	
	public int getElapsedMinutesSinceBroadcastStarted() 
	{
		Calendar now = Calendar.getInstance();
		
	    int elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceInMinutesBetween(getBeginTimeCalendar(), now, false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	public int getRemainingMinutesUntilBroadcastEnds() 
	{	    
	    Calendar now = Calendar.getInstance();
		
	    int elapsedMinutesSinceBroadcastStarted = DateUtils.calculateDifferenceInMinutesBetween(now, getEndTimeCalendar(), false, 0);
	    
	    return elapsedMinutesSinceBroadcastStarted;
	}
	
	
	
	
	public Calendar getBroadcastBeginTimeForNotification()
	{
		Calendar calendar = (Calendar) getBeginTimeCalendar().clone();

		int minutesBeforeNotify = Consts.NOTIFY_MINUTES_BEFORE_THE_BROADCAST2;
		
		calendar.add(Calendar.MINUTE, -minutesBeforeNotify);
		
		return calendar;
	}
	
	
	
	/*
	 * Returns a string representation of the begin time calendar in the format "yyyy-MM-dd"
	 */
	public String getBeginTimeDateRepresentation() 
	{
		if(beginTimeDateRepresentation == NO_STRING_VALUE_SET)
		{
			int yearValue = getBeginTimeCalendar().get(Calendar.YEAR);
			int monthValue = getBeginTimeCalendar().get(Calendar.MONTH)+1;  // In Java, months start at index 0, so we add 1 to the value
			int dayValue = getBeginTimeCalendar().get(Calendar.DAY_OF_MONTH);
			
			NumberFormat nfWith2Digits = NumberFormat.getInstance();
			nfWith2Digits.setMinimumIntegerDigits(2);
			nfWith2Digits.setMaximumIntegerDigits(2);
			nfWith2Digits.setMinimumFractionDigits(0);
			nfWith2Digits.setMaximumFractionDigits(0);
			
			StringBuilder sb = new StringBuilder();
			sb.append(yearValue);
			sb.append(Consts.TVDATE_DATE_FORMAT_SEPARATOR);
			sb.append(nfWith2Digits.format(monthValue));
			sb.append(Consts.TVDATE_DATE_FORMAT_SEPARATOR);
			sb.append(nfWith2Digits.format(dayValue));
			
			beginTimeDateRepresentation = sb.toString();
		}
		
		return beginTimeDateRepresentation;
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
	
	
	
	public static int getClosestBroadcastIndex(
			final ArrayList<TVBroadcast> broadcasts, 
			final int hour, 
			final TVDate tvDate,
			final int defaultValueIfNotFound)
	{
		int closestIndexFound = defaultValueIfNotFound;
		
		Calendar tvDateWithHourCalendar = DateUtils.buildCalendarWithDateAndSpecificHour(tvDate.getDate(), hour);
		
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
	
	
	
	public String getBeginTimeHourAndMinuteCompositionAsString()
	{		
		return DateUtils.getHourAndMinuteCompositionAsString(getBeginTimeCalendar());
	}
	


	public String getBeginTimeDayOfTheWeekAsString() 
	{
		return DateUtils.getDayOfTheWeekAsString(getBeginTimeCalendar());
	}

	
	
	public String getBeginTimeDayOfTheWeekAndTimeAsString()
	{
		String dayOfTheWeekAsString = DateUtils.getDayOfTheWeekAsString(getBeginTimeCalendar());
		
		String timeOfDayAsString = DateUtils.getTimeOfDayAsString(getBeginTimeCalendar());
		
		StringBuilder sb = new StringBuilder();
		sb.append(dayOfTheWeekAsString);
		sb.append(", ");
		sb.append(timeOfDayAsString);
		
		return sb.toString();
	}
	
	
	
	public static ArrayList<TVBroadcast> getBroadcastsStartingFromPosition(
			int indexOfNearestBroadcast, 
			ArrayList<TVBroadcast> broadcasts, 
			int howMany) 
	{
		// TODO implement or delete me
		return null;
	}
	


	
	// TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */
	
	public String getBeginTimeStringLocalDayMonth() {
		// TODO implement or delete me
		return null;
	}

	public String getBeginTimeStringLocalHourAndMinute() {
		// TODO implement or delete me
		return null;
	}
	
	
	public String getEndTimeStringLocal() 
	{
		// TODO implement or delete me
		return null;
	}

	
	public TVChannel getChannel() 
	{
		// TODO implement or delete me
		return null;
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
