
package com.mitv.utilities;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVDate;



public abstract class DateUtils 
{	
	private static final String TAG = DateUtils.class.getName();
	
	
	public static final int TOTAL_MINUTES_IN_ONE_HOUR = 60;
	public static final long TOTAL_MILLISECONDS_IN_ONE_SECOND = 1000;
	public static final long TOTAL_MILLISECONDS_IN_ONE_MINUTE = TOTAL_MILLISECONDS_IN_ONE_SECOND*60;
	public static final long TOTAL_MILLISECONDS_IN_ONE_HOUR = TOTAL_MILLISECONDS_IN_ONE_MINUTE*60;
	public static final long TOTAL_MILLISECONDS_IN_ONE_DAY = TOTAL_MILLISECONDS_IN_ONE_HOUR*24;
	
	
	
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the ISO 8601 date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	public static Calendar convertISO8601StringToCalendar(final String inputString)
	{
		return convertFromStringToUTC0CalendarWithFormat(Constants.ISO_8601_DATE_FORMAT, inputString);
	}
	
	
	
	public static String convertFromCalendarToISO8601String(final Calendar inputCalendar) 
	{
		Date dateFromCalendar = inputCalendar.getTime();
		
		TimeZone timeZone = inputCalendar.getTimeZone();
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(Constants.ISO_8601_DATE_FORMAT, timeZone);
		
		String calendarStringRepresentation = formatter.format(dateFromCalendar);
		
		return calendarStringRepresentation;
	}
	
	
	
	/**
	 * Creates a Calendar object from the string representation of a TVDate. The calendar
	 * represents the start time of the TV Day. 
	 * The hour component is set to start hour of the TV days, provided from backend (but here read from cache).
	 * The minute, second and millisecond components are all set to 0.
	 * The input string format should be in the format: "yyyy-MM-dd"
	 */
	public static Calendar getCalendarForStartOfTVDay(final String inputString)
	{
		Calendar startOfTVDayCalendar = convertFromStringToUTC0CalendarWithFormat(Constants.DATE_FORMAT_DATE, inputString);
		
		int firstHourOfTVDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		startOfTVDayCalendar.set(Calendar.HOUR_OF_DAY, firstHourOfTVDay);
		startOfTVDayCalendar.set(Calendar.MINUTE, 0);
		startOfTVDayCalendar.set(Calendar.SECOND, 0);
		startOfTVDayCalendar.set(Calendar.MILLISECOND, 0);
		
		return startOfTVDayCalendar;
	}
	
	
	
	/**
	 * Creates a Calendar object from the long representation of milliseconds. 
	 * The calendar represents the start time of the TV Day. 
	 * The hour component is set to start hour of the TV days, provided from backend (but here read from cache).
	 * The minute, second and millisecond components are all set to 0.
	 */
	public static Calendar getCalendarForStartOfTVDay(final long inputMilliseconds)
	{
		Calendar startOfTVDayCalendar = getNowWithGMTTimeZone();
		startOfTVDayCalendar.setTimeInMillis(inputMilliseconds);
		
		int firstHourOfTVDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		int startHour = startOfTVDayCalendar.get(Calendar.HOUR_OF_DAY);
		
		if(startHour >= 0 && startHour < firstHourOfTVDay) 
		{
			startOfTVDayCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		startOfTVDayCalendar.set(Calendar.HOUR_OF_DAY, firstHourOfTVDay);
		startOfTVDayCalendar.set(Calendar.MINUTE, 0);
		startOfTVDayCalendar.set(Calendar.SECOND, 0);
		startOfTVDayCalendar.set(Calendar.MILLISECOND, 0);
		
		return startOfTVDayCalendar;
	}
	
	
	
	/**
	 * Creates a Calendar object from the string representation of a TVDate. The calendar
	 * represents the end time of the TV Day. 
	 */
	public static Calendar getCalendarForEndOfTVDayUsingStartCalendar(final Calendar startOfTVDateCalendar)
	{
		/* Make a copy of the start calendar */
		Calendar endOfTVDayCalendar = (Calendar) startOfTVDateCalendar.clone();
		
		/* Increase day by one */
		endOfTVDayCalendar.add(Calendar.DATE, 1);
		/* Decrease minute and second and millisecond by one, by decreasing the millisecond by 1. 
		 * Please note that this REQUIRES that the minute, second and millisecond components have
		 * all been sent to 0 in the constructor for the startOfTVDay method */
		endOfTVDayCalendar.add(Calendar.MILLISECOND,- 1);
		
		return endOfTVDayCalendar;
	}
		
	
	
	private static Calendar convertFromStringToUTC0CalendarWithFormat(
			final String dateFormatString,
			final String inputString)
	{
		Calendar cal = getNowWithGMTTimeZone();
		
		if (!TextUtils.isEmpty(inputString))
		{
			TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
			
			SimpleDateFormat dateFormat = getSimpleDateFormatWith(dateFormatString, gmtTimeZone);
			
			try 
			{
				Date date = dateFormat.parse(inputString);
				
				cal.setTime(date);
				
				cal.setTimeZone(gmtTimeZone);
			} 
			catch (ParseException e) 
			{
				Log.w(TAG, e.getMessage(), e);
			}
		}
		else
		{
			Log.w(TAG, "Input calendar string is null or empty.");
		}
		
		return cal;
	}
	
	
	
	/**
	 * Get the current hour as int
	 * 
	 * @param
	 * @return current hour as int
	 */
	private static int getCurrentHour(boolean showTimeOn24HourFormat) 
	{
		Calendar now = getNowWithLocalTimezone();
		
		int currentHour = now.get(Calendar.HOUR);
		
		if(showTimeOn24HourFormat) 
		{
			currentHour = now.get(Calendar.HOUR_OF_DAY);
		}
		
		return currentHour;
	}
	
	
	
	private static int getCurrentHour(Context context) 
	{
		boolean showTimeOn24HourFormat = showTimeOn24HourFormat();
		
		return getCurrentHour(showTimeOn24HourFormat);
	}
		
	
	
	public static int getCurrentHourUseDevice24HourSettings()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		return getCurrentHour(context);
	}
	
	
	
	public static int getCurrentHourOn24HourFormat()
	{
		return getCurrentHour(true);
	}
		
	
	
	/**
	 * Computes the total difference in minutes between the second Calendar object and the first Calendar object.
	 * Please note that the difference can be negative.
	 * It can optionally be set to always return the absolute difference.
	 * 
	 */
	public static Integer calculateDifferenceBetween(
			final Calendar beginTimeCalendar, 
			final Calendar endTimeCalendar,
			final int differenceUnitType,
			final boolean useAbslouteDifference,
			final int defaultValueIfNegative)
	{
		long beginTime;
		long endTime;
		
		switch(differenceUnitType)
		{
			case Calendar.DAY_OF_MONTH:
			{
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_DAY;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_DAY;
				break;
			}
			
			case Calendar.HOUR_OF_DAY:
			{
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_HOUR;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_HOUR;
				break;
			}
			
			default:
			case Calendar.MINUTE:
			{
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_MINUTE;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILLISECONDS_IN_ONE_MINUTE;
				break;
			}
				
		}
		
		int differenceAsInt = (int) (endTime - beginTime);
	    Integer difference =  Integer.valueOf(differenceAsInt);
	    
	    if(difference < 0 &&
	       useAbslouteDifference == false)
	    {
	    	difference = defaultValueIfNegative;
	    	
	    	Log.w(TAG, "The calculated time difference (" + endTime + " minus " + beginTime + ") is negative.");
	    }
	    else
	    {
	    	difference = (int) difference;
	    }
	    
	    if(useAbslouteDifference)
	    {
	    	difference = Math.abs(difference);
	    }
	    
	    return difference;
	}
	
	
	
	public static boolean isTodayUsingCalendar(final Calendar inputCalendar)
	{
		Calendar now = getNowWithGMTTimeZone();
		
		boolean isToday = (inputCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
				   		   inputCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
				   		   inputCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH));
		
		return isToday;
	}
	
	
	
	
	/**
	 * Checks if TVDate is today, according to the rules
	 * @param tvDate
	 * @return
	 */
	public static boolean isTodayUsingTVDate(final TVDate tvDate)
	{
		/** Since we are passing the minutes offset in the webservice call, we should always use the local calendars when comparing dates */
		Calendar now = getNowWithLocalTimezone();
		
		Calendar startOfTVDay = tvDate.getStartOfTVDayCalendarLocal();
		
		Calendar endOfTVDay = tvDate.getEndOfTVDayCalendarLocal();
		
		boolean isNowAfterStartOfTVDay = now.after(startOfTVDay);
		boolean isNowEqualToTVDay = now.equals(startOfTVDay);
		boolean isNowBeforeEndOfTVDay = now.before(endOfTVDay);
		
		boolean isTVDateNow = (isNowAfterStartOfTVDay || isNowEqualToTVDay) && isNowBeforeEndOfTVDay;
		
		return isTVDateNow;
	}
	
		

	
	/**
	 * Builds a string representation for the day of the week of the input calendar.
	 * If the day of the week is either today or tomorrow, a localized representation is returned instead.
	 *
	 */
	public static String buildDayOfTheWeekAsString(final Calendar inputCalendar, boolean useExtendedDayNames)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		return buildDayOfTheWeekAsString(inputCalendar, context, useExtendedDayNames);
	}
	
	
	
	/**
	 * This method compares 2 calendar objects, returning true if they are in the same "broadcast day", or false otherwise
	 * 
	 * For example:
	 * According to our TVGuide a day is between 06:00AM to 05:59AM.
	 * For a show with airing time at 05:30 (is after 12 O'clock, so its a new day) but should
	 * show Today as airing time.
	 * 
	 * @param inputCalendar1:
	 * One of the calendar to compare
	 * 
	 * @param inputCalendar2:
	 * One of the calendar to compare
	 * 
	 * @return
	 */
	public static boolean areCalendarsTheSameTVAiringDay(
			final Calendar inputCalendar1, 
			final Calendar inputCalendar2) 
	{
		int firstHourOfTVDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		boolean isYearEqual = (inputCalendar1.get(Calendar.YEAR) - inputCalendar2.get(Calendar.YEAR)) <= 1;
    	boolean isMonthEqual = (inputCalendar1.get(Calendar.MONTH) - inputCalendar2.get(Calendar.MONTH)) <= 1;
    	
		boolean areCalendarsTheSameDayOfMonth = inputCalendar1.get(Calendar.DAY_OF_MONTH) == inputCalendar2.get(Calendar.DAY_OF_MONTH);
	
		boolean areCalendarsTheSamePeriod;
		
		if(areCalendarsTheSameDayOfMonth)
		{
			boolean correctHourPeriod1LowerBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) <= 23;
			boolean correctHourPeriod1UpperBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
			
			boolean correctHourPeriod1LowerBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) <= 23;
			boolean correctHourPeriod1UpperBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
			
			
			boolean correctHourPeriod2LowerBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) >= 0;
			boolean correctHourPeriod2UpperBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) < firstHourOfTVDay;
			
			boolean correctHourPeriod2LowerBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) >= 0;
			boolean correctHourPeriod2UpperBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) < firstHourOfTVDay;
			
			areCalendarsTheSamePeriod = (correctHourPeriod1LowerBound1 && correctHourPeriod1UpperBound1 && correctHourPeriod1LowerBound2 && correctHourPeriod1UpperBound2) ||
									    (correctHourPeriod2LowerBound1 && correctHourPeriod2UpperBound1 && correctHourPeriod2LowerBound2 && correctHourPeriod2UpperBound2);
		}
		else
		{
			if(inputCalendar2.after(inputCalendar1))
			{
				boolean isNextDayOfTheMonth = inputCalendar2.get(Calendar.DAY_OF_MONTH) == (inputCalendar1.get(Calendar.DAY_OF_MONTH) + 1);
				
				if(isNextDayOfTheMonth)
				{
					boolean correctHourLowerBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) <= 23;
					boolean correctHourUpperBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
					
					boolean correctHourLowerBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) >= 0;
					boolean correctHourUpperBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) < firstHourOfTVDay;
					
					areCalendarsTheSamePeriod = (correctHourLowerBound1 && correctHourUpperBound1 && correctHourLowerBound2 && correctHourUpperBound2);
				}
				else
				{
					areCalendarsTheSamePeriod = false;
				}
			}
			else
			{
				boolean isNextDayOfTheMonth = inputCalendar1.get(Calendar.DAY_OF_MONTH) == (inputCalendar2.get(Calendar.DAY_OF_MONTH) + 1);
				
				if(isNextDayOfTheMonth)
				{
					boolean correctHourLowerBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) >= 0;
					boolean correctHourUpperBound1 = inputCalendar1.get(Calendar.HOUR_OF_DAY) < firstHourOfTVDay;
					
					boolean correctHourLowerBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) <= 23;
					boolean correctHourUpperBound2 = inputCalendar2.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
					
					areCalendarsTheSamePeriod = (correctHourLowerBound1 && correctHourUpperBound1 && correctHourLowerBound2 && correctHourUpperBound2);
				}
				else
				{
					areCalendarsTheSamePeriod = false;
				}
			}
		}
		
		boolean areCalendarsTheSameTVDay = isYearEqual && isMonthEqual && areCalendarsTheSamePeriod;
		
		return areCalendarsTheSameTVDay;
	}
	
	
	
	
	private static String getDayOfWeekStringUsingFirstHourOfTVDay(Calendar inputCalendar) 
	{
		String dayOfTheWeekAsString = null;
		
		Locale locale = LanguageUtils.getCurrentLocale();
		
		int firstHourOfTVDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		int startHour = inputCalendar.get(Calendar.HOUR_OF_DAY);
		
		if(startHour >= 0 && startHour < firstHourOfTVDay) 
		{
			inputCalendar.add(Calendar.DAY_OF_MONTH, -1);
			
			dayOfTheWeekAsString = inputCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
		} 
		else 
		{
			dayOfTheWeekAsString = inputCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
		}
		
		return dayOfTheWeekAsString;
	}
	
	
	
	/**
	 * Builds a string representation for the day of the week of the input calendar.
	 * The representation is localized using the current Locale from the context.
	 * If the day of the week is either today or tomorrow, a localized representation is returned instead.
	 * 
	 */
	private static String buildDayOfTheWeekAsString(
			final Calendar inputCalendarOriginal,
			final Context context,
			final boolean useExtendedDayNames)
	{
		Calendar inputCalendar = (Calendar) inputCalendarOriginal.clone();
		
		String dayOfTheWeekAsString;
		
		Locale locale = LanguageUtils.getCurrentLocale();
		
		Calendar now = getNowWithLocalTimezone();
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - inputCalendar.get(Calendar.YEAR)) == 0;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - inputCalendar.get(Calendar.MONTH)) == 0;
    	boolean isSameDay = areCalendarsTheSameTVAiringDay(inputCalendar, now);
    	
		boolean isToday = isCorrectYear && isCorrectMonth && isSameDay;

		int firstHourOfDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		if (isToday)
		{
			if (useExtendedDayNames) 
			{
				int hour = inputCalendar.get(Calendar.HOUR_OF_DAY);

				int currentHour = now.get(Calendar.HOUR_OF_DAY);

				boolean isTonight = hour < firstHourOfDay;

				boolean isYesterday = currentHour < firstHourOfDay && isTonight == false;

				if (isTonight) 
				{
					dayOfTheWeekAsString = context.getString(R.string.tonight);
				}
				else if (isYesterday) 
				{
					dayOfTheWeekAsString = context.getString(R.string.yesterday);
				}
				else 
				{
					dayOfTheWeekAsString = context.getString(R.string.today);
				}
			}
			else 
			{
				dayOfTheWeekAsString = context.getString(R.string.today);
			}
		} 
		else 
		{
			Calendar tomorrow = (Calendar) now.clone();

			tomorrow.add(Calendar.DAY_OF_MONTH, 1);
			
			isCorrectYear = (tomorrow.get(Calendar.YEAR) - inputCalendar.get(Calendar.YEAR)) == 0;
	    	isCorrectMonth = (tomorrow.get(Calendar.MONTH) - inputCalendar.get(Calendar.MONTH)) == 0;
			isSameDay = areCalendarsTheSameTVAiringDay(inputCalendar, tomorrow);

			boolean isTomorrow = isCorrectYear && isCorrectMonth && isSameDay;

			if (isTomorrow) 
			{
				if (useExtendedDayNames) 
				{
					int hour = inputCalendar.get(Calendar.HOUR_OF_DAY);

					boolean isTomorrowNight = hour < firstHourOfDay;

					if (isTomorrowNight)
					{
						dayOfTheWeekAsString = context.getString(R.string.tomorrow_night);
					}
					else
					{
						dayOfTheWeekAsString = context.getString(R.string.tomorrow);
					}
				}
				else
				{
					dayOfTheWeekAsString = context.getString(R.string.tomorrow);
				}
			}
			else
			{
				Calendar yesterday = (Calendar) now.clone();
				
				yesterday.add(Calendar.DAY_OF_MONTH, -1);

				isCorrectYear = (yesterday.get(Calendar.YEAR) - inputCalendar.get(Calendar.YEAR)) == 0;
		    	isCorrectMonth = (yesterday.get(Calendar.MONTH) - inputCalendar.get(Calendar.MONTH)) == 0;
				isSameDay = areCalendarsTheSameTVAiringDay(inputCalendar, yesterday);
				
				boolean isYesterday = isCorrectYear && isCorrectMonth && isSameDay;
				
				if (isYesterday)
				{
					dayOfTheWeekAsString = context.getString(R.string.yesterday);
				}
				else 
				{
					dayOfTheWeekAsString = getDayOfWeekStringUsingFirstHourOfTVDay(inputCalendar);
				}
			}
		}
		
		/* The first character is always capitalized, per UX team request */
		dayOfTheWeekAsString = LanguageUtils.capitalize(dayOfTheWeekAsString, locale);
		
		return dayOfTheWeekAsString;
	}
	
	
	
	/**
	 * Builds a string representation of the year, month and day (yyyy-MM-dd) for the provided calendar. 
	 * 
	 */
	public static String buildDateCompositionAsString(final Calendar inputCalendar)
	{
		String pattern = Constants.DATE_FORMAT_DATE;
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, inputCalendar.getTimeZone());
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a string representation for the time of the day (HH:mm), from the input calendar.
	 * Using application context and always using the 24 hour setting of the phone
	 * 
	 */
	public static String getHourAndMinuteCompositionAsString(final Calendar inputCalendar)
	{ 
		String hourAndMinuteCompositionAsString = getHourAndMinuteCompositionAsString(inputCalendar, true);
		
		return hourAndMinuteCompositionAsString;
	}
	
	
	
	public static boolean showTimeOn24HourFormat() 
	{
		return true;
	}
	
	
	
	public static String getHourAndMinuteAsStringUsingHour(int hour) 
	{
		Calendar now = getNowWithGMTTimeZone();
		
		now.set(Calendar.HOUR_OF_DAY, hour);
		now.set(Calendar.MINUTE, 0);
		String hourMinuteString = getHourAndMinuteCompositionAsString(now, false);
		
		return hourMinuteString;
	}
	
	
	
	/**
	 * Builds a string representation for the time of the day (HH:mm), from the input calendar.
	 * It can be optionally set to either use or ignore the local device 24 hour setting.
	 * 
	 */
	private static String getHourAndMinuteCompositionAsString(
			final Calendar inputCalendar,
			final boolean use24HourSettingsSetOnDevice)
	{
		String pattern;
		
		if(use24HourSettingsSetOnDevice)
		{
			boolean showTimeOn24HourFormat = showTimeOn24HourFormat();
			
			if(showTimeOn24HourFormat)
			{
				pattern = Constants.DATE_FORMAT_HOUR_AND_MINUTE;
			}
			else 
			{
				pattern = Constants.DATE_FORMAT_HOUR_AND_MINUTE_WITH_AM_PM;
			}
		}
		else
		{
			pattern = Constants.DATE_FORMAT_HOUR_AND_MINUTE;
		}
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, inputCalendar.getTimeZone());
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a string representation for the day and month (dd/MM) of the provided calendar. 
	 * 
	 */
	public static String buildDayAndMonthCompositionAsString(
			final Calendar inputCalendar,
			final boolean useFirstHourOfTheDay)
	{
		String pattern = Constants.DATE_FORMAT_DAY_AND_MONTH;
				
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, inputCalendar.getTimeZone());
		
		Calendar calendar = (Calendar) inputCalendar.clone();
		
		if(useFirstHourOfTheDay)
		{
			int firstHourOfTVDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
	
			boolean correctHourLowerBound = calendar.get(Calendar.HOUR_OF_DAY) <= 23;
			boolean correctHourUpperBound = calendar.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
			
			boolean isCurrentDay = correctHourLowerBound && correctHourUpperBound;
	
			if(isCurrentDay == false)
			{
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}
		}
		
		Date date = calendar.getTime();
		
		String timeOfDayAsString = formatter.format(date);
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a calendar from a specific date, sets the hour to the specified value and the minutes, seconds and milliseconds to the current time values
	 * 
	 */
	public static Calendar buildLocalCalendarWithTVDateAndSelectedHour(
			final TVDate tvDate, 
			final int selectedHour) 
	{
		Calendar now = getNowWithLocalTimezone();
		
		Calendar startOfTVDate = (Calendar) tvDate.getStartOfTVDayCalendarLocal().clone();
		
		int currentHour = now.get(Calendar.HOUR_OF_DAY);
		int currentMinute = now.get(Calendar.MINUTE);		
				
		int hoursValue = selectedHour;
		
		int firstHourOfTheDay = ContentManager.sharedInstance().getCacheManager().getFirstHourOfTVDay();
		
		
//		if(currentHour >= 0 && currentHour < firstHourOfTheDay)
//		{
//			startOfTVDate.add(Calendar.DATE, -1);
//		}
		
		if(hoursValue >= 0 && hoursValue < firstHourOfTheDay) 
		{
			startOfTVDate.add(Calendar.DATE, 1);
		}
		
		if(hoursValue == firstHourOfTheDay)
		{
			if(currentHour == (hoursValue-1) && currentMinute < 60)
			{
				hoursValue = currentHour;
			}
		}
		
		if(hoursValue < 0)
		{
			hoursValue = 0;
		}
		
		startOfTVDate.set(Calendar.HOUR_OF_DAY, hoursValue);
		startOfTVDate.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
		startOfTVDate.set(Calendar.SECOND, now.get(Calendar.SECOND));
		
		return startOfTVDate;
	}
	
	
	
	/**
	 * This method does not take daylight time into consideration.
	 * Just returns the UTC + 0 time.
	 * 
	 * BE CAREFUL!
	 * 
	 * @return
	 */
	public static Calendar getNowWithGMTTimeZone()
	{
		Locale locale = LanguageUtils.getISO8601Locale();
		
		Calendar calendar = Calendar.getInstance(locale);
		
		TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
				
		calendar.setTimeZone(gmtTimeZone);
		
		return calendar;
	}
	
	
	
	/**
	 * This method should take daylight time into consideration.
	 * 
	 * BE CAREFUL ANYWAY!
	 * 
	 * @return
	 */
	public static Calendar getNowWithLocalTimezone()
	{
		Locale locale = Locale.getDefault();
		
		Calendar calendar = Calendar.getInstance(locale);
		
		calendar = setTimeZoneAndOffsetToLocal(calendar);
		
		return calendar;
	}
	
	
	
	public static Calendar setTimeZoneAndOffsetToLocal(Calendar calendar)
	{
		TimeZone deviceTimeZone = TimeZone.getDefault();
		
		calendar.setTimeZone(deviceTimeZone);
		
		return calendar;
	}
	
	
	
	/**
	 * This calculation of timezone offset takes in daylight time in consideration.
	 * 
	 * @return
	 */
	public static Integer getTimeZoneOffsetInMinutes() 
	{
		Integer timeZoneOffsetInMinutes = 0;
		
		TimeZone timeZone = TimeZone.getDefault();
		
		Calendar cal = DateUtils.getNowWithGMTTimeZone();
		
		int era = cal.get(Calendar.ERA);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int timeOfDayMillis = cal.get(Calendar.MILLISECOND);
		
		int offsetNEW = timeZone.getOffset(era, year, month, day, dayOfWeek, timeOfDayMillis);
		
		int  timeZoneOffsetInMinutesAlternative = (int)(offsetNEW / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
			
		timeZoneOffsetInMinutes = Integer.valueOf(timeZoneOffsetInMinutesAlternative);

		return timeZoneOffsetInMinutes;
	}
	
	
	
	/**
	 * Generates a SimpleDateFormat instance with a set pattern, timeZone and locale
	 * 
	 */
	private static SimpleDateFormat getSimpleDateFormatWith(
			final String pattern,
			final TimeZone timeZone) 
	{
		Locale locale = LanguageUtils.getCurrentLocale();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		
		if(timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		
		return dateFormat;
	}	
}