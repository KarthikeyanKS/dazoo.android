
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
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.models.TVDate;



public abstract class DateUtils 
{	
	private static final String TAG = DateUtils.class.getName();
	
	public static final int TOTAL_MINUTES_IN_ONE_HOUR = 60;
	public static final long TOTAL_MILLISECONDS_IN_ONE_MINUTE = 60000;
	public static final long TOTAL_MILLISECONDS_IN_ONE_HOUR = TOTAL_MILLISECONDS_IN_ONE_MINUTE*60;
	public static final long TOTAL_MILLISECONDS_IN_ONE_DAY = TOTAL_MILLISECONDS_IN_ONE_HOUR*24;
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the format: "yyyy-MM-dd"
	 * 
	 */
	public static Calendar convertFromYearAndDateStringToCalendar(final String inputString)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		return convertFromStringToCalendarWithFormat(Constants.DATE_FORMAT_DATE, inputString, context);
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
		Calendar startOfTVDayCalendar = convertFromYearAndDateStringToCalendar(inputString);
		int firstHourOfTVDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
		
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
		
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the ISO 8601 date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	public static Calendar convertFromYearDateAndTimeStringToCalendar(
			final String inputString)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		return convertFromYearDateAndTimeStringToCalendar(inputString, context);
	}
	
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the ISO 8601 date format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	private static Calendar convertFromYearDateAndTimeStringToCalendar(
			final String inputString,
			final Context context)
	{
		return convertFromStringToCalendarWithFormat(Constants.ISO_8601_DATE_FORMAT, inputString, context);
	}
	
	
	public static Integer getTimeZoneOffsetInMinutes()
	{
		Integer timeZoneOffsetInMinutes = 0;
		TimeZone timeZone = TimeZone.getDefault();
		
		if(timeZone != null)
		{
			int timeZoneOffsetInMinutesAsInt = (int) (timeZone.getRawOffset() / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);		
			timeZoneOffsetInMinutes = Integer.valueOf(timeZoneOffsetInMinutesAsInt);
		}
		else
		{
			Log.w(TAG, "TimeZone has null value.");
		}
		return timeZoneOffsetInMinutes;
	}
	
	
	
	private static Calendar convertFromStringToCalendarWithFormat(
			final String dateFormatString,
			final String inputString,
			final Context context)
	{
		Calendar cal = getNow();
		
		if (!TextUtils.isEmpty(inputString))
		{
			SimpleDateFormat dateFormat = getSimpleDateFormatWith(dateFormatString);
			
			try 
			{
				Date date = dateFormat.parse(inputString);
				
				cal.setTime(date);
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
		Calendar now = getNow();
		
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
		float beginTime;
		float endTime;
		
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
		
		int differenceAsInt = (int)(endTime - beginTime);
	    Integer difference =  Integer.valueOf(differenceAsInt);
	    
	    if(difference < 0 && 
	       useAbslouteDifference == false)
	    {
	    	Log.w(TAG, "The calculated time difference is negative.");
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
		Calendar now = getNow();
		
		boolean isToday = (inputCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
				   		   inputCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
				   		   inputCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH));
		
		return isToday;
	}
	
	/**
	 * Checks if TVDate is today, according to the 
	 * @param tvDate
	 * @return
	 */
	public static boolean isTodayUsingTVDate(final TVDate tvDate) {
		Calendar now = getNow();
		Calendar startOfTVDay = tvDate.getStartOfTVDayCalendar();
		Calendar endOfTVDay = tvDate.getEndOfTVDayCalendar();
		boolean a = startOfTVDay.before(now);
		boolean b = endOfTVDay.after(now);
		
//		Log.d(TAG, "mmm.. now: " + now);
//		Log.d(TAG, "mmm... startOfTVDay: " + startOfTVDay);
		
//		Log.d(TAG, "mmm... endOfTVDay: " + endOfTVDay);
		Log.d(TAG, "mmm.... startOfTVDay.before(now): " + a);
		Log.d(TAG, "mmm.... now.after(startOfTVDay)   : " + now.after(startOfTVDay));
		Log.d(TAG, "mmm..... endOfTVDay.after(now)   : " + b);	
		Log.d(TAG, "mmm..... now.before(endOfTVDay)   : " + now.before(endOfTVDay));
		Log.d(TAG, "mmm now.equals(startOfTVDay)   : " + now.equals(startOfTVDay));
		
		boolean isTVDateNow = (now.after(startOfTVDay) || now.equals(startOfTVDay)) && now.before(endOfTVDay);
		Log.d(TAG, "mmm.. isTVDateNow:  " + isTVDateNow);
		return isTVDateNow;

	}
	
	
	/**
	 * Builds a string representation for the day of the week of the input calendar.
	 * If the day of the week is either today or tomorrow, a localized representation is returned instead.
	 *
	 */
	public static String buildDayOfTheWeekAsString(final Calendar inputCalendar)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		return buildDayOfTheWeekAsString(inputCalendar, context);
	}
	
	/**
	 * This method is used for showing the right airing day for a show in Broadcast page.
	 * 
	 * For example:
	 * According to our TVGuide a day is between 06:00AM to 05:59AM.
	 * For a show with airing time at 05:30 (is after 12 O'clock, so its a new day) but should
	 * show Today as airing time.
	 * 
	 * @param inputCalendar:
	 * This calendar is the actual airing time for a show.
	 * 
	 * @param referencedTime:
	 * This calendar is the time we compare the input with to establish if the show is airing today,
	 * tomorror or another day after tomorror.
	 * 
	 * @return
	 */
	private static boolean isSameAiringDayTitle(Calendar inputCalendar, Calendar referencedTime) {
		int firstHourOfTVDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();

		boolean correctDay = inputCalendar.get(Calendar.DAY_OF_MONTH) == referencedTime.get(Calendar.DAY_OF_MONTH);
		boolean correctHourLowerBound = inputCalendar.get(Calendar.HOUR_OF_DAY) <= 23;
		boolean correctHourUpperBound = inputCalendar.get(Calendar.HOUR_OF_DAY) >= firstHourOfTVDay;
		boolean correctHour = correctHourLowerBound && correctHourUpperBound;

		boolean isCorrectDayIfBeforeTwelveAtNight = correctDay && correctHour;

		correctDay = inputCalendar.get(Calendar.DAY_OF_MONTH) == (referencedTime.get(Calendar.DAY_OF_MONTH) + 1);
		correctHourLowerBound = inputCalendar.get(Calendar.HOUR_OF_DAY) >= 0;
		correctHourUpperBound = inputCalendar.get(Calendar.HOUR_OF_DAY) < firstHourOfTVDay;
		correctHour = correctHourLowerBound && correctHourUpperBound;

		boolean isCorrectDayIfAfterTwelveAtNight = correctDay && correctHour;

		boolean isSameDayAsNow = isCorrectDayIfBeforeTwelveAtNight || isCorrectDayIfAfterTwelveAtNight;

		return isSameDayAsNow;
	}
	
	private static String getDayOfWeekStringUsingFirstHourOfTVDay(Calendar inputCalendar) {
		String dayOfTheWeekAsString = null;
		
		Locale locale = LanguageUtils.getCurrentLocale();
		int firstHourOfTVDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
		
		int startHour = inputCalendar.get(Calendar.HOUR_OF_DAY);
		if(startHour >= 0 && startHour < firstHourOfTVDay) {
			inputCalendar.add(Calendar.DAY_OF_MONTH, -1);
			dayOfTheWeekAsString = inputCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
		} else {
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
			final Context context)
	{
		Calendar inputCalendar = (Calendar) inputCalendarOriginal.clone();
		String dayOfTheWeekAsString;
		
		Locale locale = LanguageUtils.getCurrentLocale();
		
		Calendar now = getNow();
		
    	boolean isCorrectYear = (now.get(Calendar.YEAR) - inputCalendar.get(Calendar.YEAR)) <= 1;
    	boolean isCorrectMonth = (now.get(Calendar.MONTH) - inputCalendar.get(Calendar.MONTH)) <= 1;
    	boolean isSameDay = isSameAiringDayTitle(inputCalendar, now);
    	
		boolean isToday = isCorrectYear && isCorrectMonth && isSameDay;
		
		if (isToday) {
			dayOfTheWeekAsString = context.getString(R.string.today);
		} else {
			Calendar tomorrow = (Calendar) now.clone();
	 		tomorrow.add(Calendar.DAY_OF_MONTH, 1);

	 		isSameDay = isSameAiringDayTitle(inputCalendar, tomorrow);
	 		boolean isTomorrow = isCorrectYear && isCorrectMonth && isSameDay;
			
	 		if(isTomorrow) {
	 			dayOfTheWeekAsString = context.getString(R.string.tomorrow);
	 		} else {
	 			dayOfTheWeekAsString = getDayOfWeekStringUsingFirstHourOfTVDay(inputCalendar);
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
	public static String buildDateCompositionAsString(
			final Calendar inputCalendar,
			final Context context)
	{
		String pattern = Constants.DATE_FORMAT_DATE;
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern);
		
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
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		String hourAndMinuteCompositionAsString = getHourAndMinuteCompositionAsString(inputCalendar, true, context);
		return hourAndMinuteCompositionAsString;
	}
	
	public static String calendarToStringForDebug(final Calendar inputCalendar) {
		Date dateFromCalendar = inputCalendar.getTime();
		SimpleDateFormat formatter = getSimpleDateFormatWith(Constants.CALENDAR_TO_STRING_FOR_DEBUG);
		String toString = formatter.format(dateFromCalendar);
		
		return toString;
	}
	
	public static boolean showTimeOn24HourFormat() 
	{
		return true;
	}
	
	
	
//	private static boolean showTimeOn24HourFormat(Context context) 
//	{
//		boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
//		
//		return is24HourFormat;
//	}
	
	/**
	 * Builds a string representation for the time of the day (HH:mm), from the input calendar.
	 * It can be optionally set to either use or ignore the local device 24 hour setting.
	 * 
	 */
	private static String getHourAndMinuteCompositionAsString(
			final Calendar inputCalendar,
			final boolean use24HourSettingsSetOnDevice,
			final Context context)
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
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern);
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	/**
	 * Builds a string representation for the day and month (dd/MM) of the provided calendar. 
	 * 
	 */
	public static String buildDayAndMonthCompositionAsString(final Calendar inputCalendar)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		return buildDayAndMonthCompositionAsString(inputCalendar, context);
	}
	
	
	/**
	 * Builds a string representation for the day and month (dd/MM) of the provided calendar. 
	 * 
	 */
	private static String buildDayAndMonthCompositionAsString(
			final Calendar inputCalendar,
			final Context context)
	{
		String pattern = Constants.DATE_FORMAT_DAY_AND_MONTH;
				
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern);
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a calendar from a specific date, sets the hour to the specified value and the minutes, seconds and milliseconds to the current time values
	 * 
	 * 
	 */
	public static Calendar buildCalendarWithTVDateAndSpecificHour(
			final TVDate tvDate, 
			final int hour) 
	{
		Calendar now = getNow();
		
		Calendar startOfTVDate = (Calendar) tvDate.getStartOfTVDayCalendar().clone();
		
		int hoursValue = hour;
		
		int firstHourOfTheDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
		
		if(hour >= 0 && hour < firstHourOfTheDay) 
		{
			startOfTVDate.add(Calendar.DATE, 1);
		}
		
		if(hour < 0)
		{
			hoursValue = 0;
		}
		
		startOfTVDate.set(Calendar.HOUR_OF_DAY, hoursValue);
		startOfTVDate.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
		startOfTVDate.set(Calendar.SECOND, now.get(Calendar.SECOND));
		
		return startOfTVDate;
	}
	
	
	
	public static Calendar getNow()
	{
		Locale locale = LanguageUtils.getISO8601Locale();
		
		Calendar now = Calendar.getInstance(locale);
		
		return now;
	}
	
	
	/**
	 * Generates a SimpleDateFormat instance with a set pattern, timeZone and locale
	 * 
	 */
	private static SimpleDateFormat getSimpleDateFormatWith(final String pattern) 
	{
		Locale locale = LanguageUtils.getCurrentLocale();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		
		return dateFormat;
	}
}