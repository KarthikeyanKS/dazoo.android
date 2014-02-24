
package com.millicom.mitv.utilities;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.millicom.mitv.ContentManager;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



public abstract class DateUtils 
{	
	private static final String TAG = DateUtils.class.getName();
	
	public static final long TOTAL_MILISECOUNDS_IN_ONE_MINUTE = 60000;
	public static final long TOTAL_MILISECOUNDS_IN_ONE_HOUR = TOTAL_MILISECOUNDS_IN_ONE_MINUTE*60;
	public static final long TOTAL_MILISECOUNDS_IN_ONE_DAY = TOTAL_MILISECOUNDS_IN_ONE_HOUR*24;
	
	/**
	 * Converts timestamp on GMT format to Local
	 * @param timestamp
	 * @return
	 */
	public static final long convertTimeStampToLocalTime(long timestamp) {
		Date gmtTime = new Date(timestamp);
		Date localTime = new Date(gmtTime.getTime() + TimeZone.getDefault().getOffset(gmtTime.getTime()));
		long localTimeLong = localTime.getTime();

		return localTimeLong;
	}
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	public static Calendar convertFromStringToCalendar(
			final String inputString)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		return convertFromStringToCalendar(inputString, context);
	}
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	private static Calendar convertFromStringToCalendar(
			final String inputString,
			final Context context)
	{
		Calendar cal = Calendar.getInstance();
		
		if (!TextUtils.isEmpty(inputString))
		{
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			
			Locale locale = context.getResources().getConfiguration().locale;
			
			SimpleDateFormat dateFormat = getSimpleDateFormatWith(Consts.ISO_DATE_FORMAT, timeZone, locale);
			
			try 
			{
				Date date = dateFormat.parse(inputString);
				
				cal.setTime(date);
				cal.setTimeZone(timeZone);
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
	private static int getCurrentHour(boolean showTimeOn24HourFormat) {
		Calendar now = Calendar.getInstance();
		int currentHour = now.get(Calendar.HOUR);
		if(showTimeOn24HourFormat) {
			currentHour = now.get(Calendar.HOUR_OF_DAY);
		}
		return currentHour;
	}
	
	private static int getCurrentHour(Context context) {
		boolean showTimeOn24HourFormat = showTimeOn24HourFormat(context);
		return getCurrentHour(showTimeOn24HourFormat);
	}
		
	public static int getCurrentHourUseDevice24HourSettings() {
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		return getCurrentHour(context);
	}
	
	public static int getCurrentHourOn24HourFormat() {
		return getCurrentHour(true);
	}
		
	
	/**
	 * Computes the total difference in minutes between the second Calendar object and the first Calendar object.
	 * Please note that the difference can be negative.
	 * It can optionally be set to always return the absolute difference.
	 * 
	 */
	public static int calculateDifferenceBetween(
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
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_DAY;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_DAY;
				break;
			}
			
			case Calendar.HOUR_OF_DAY:
			{
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
				break;
			}
			
			default:
			case Calendar.MINUTE:
			{
				beginTime = beginTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
				endTime = endTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
				break;
			}
				
		}
		
	    int difference = (int) (endTime - beginTime);
	    
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
	
	
	
	/**
	 * Checks if the input calendar is today's day
	 
	 */
	public static boolean isToday(final Calendar inputCalendar)
	{
		Calendar now = Calendar.getInstance();
		
		boolean isToday = (inputCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
				   		   inputCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
				   		   inputCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH));
		
		return isToday;
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
	 * Builds a string representation for the day of the week of the input calendar.
	 * The representation is localized using the current Locale from the context.
	 * If the day of the week is either today or tomorrow, a localized representation is returned instead.
	 * 
	 */
	private static String buildDayOfTheWeekAsString(
			final Calendar inputCalendar,
			final Context context)
	{
		String dayOfTheWeekAsString;
		
		Calendar now = Calendar.getInstance();
		
    	boolean isToday = false;
    	boolean isTomorrow = false;
    	
		isToday = (inputCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
				   inputCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
				   inputCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH));
 	
	 	if(isToday == false)
	 	{
	 		Calendar tomorrow = (Calendar) now.clone();

	 		tomorrow.add(Calendar.DAY_OF_MONTH, 1);

	 		isTomorrow = (inputCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
	 				inputCalendar.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH) &&
	 				inputCalendar.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH));
	 	}
	 			
		if (isToday)
		{
			dayOfTheWeekAsString = context.getResources().getString(R.string.today);
		}
		else if(isTomorrow)
		{
			dayOfTheWeekAsString = context.getResources().getString(R.string.tomorrow);
		}
		else
		{
			Locale locale = context.getResources().getConfiguration().locale;
			
			dayOfTheWeekAsString = inputCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
		}
		
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
		String pattern = Consts.DATE_FORMAT_DATE;
		
		TimeZone timeZone = inputCalendar.getTimeZone();
		
		Locale locale = context.getResources().getConfiguration().locale;
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, timeZone, locale);
		
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
	
	public static boolean showTimeOn24HourFormat() {
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		return showTimeOn24HourFormat(context);
	}
	
	private static boolean showTimeOn24HourFormat(Context context) {
		boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
		return is24HourFormat;
	}
	
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
			boolean showTimeOn24HourFormat = showTimeOn24HourFormat(context);
			if(showTimeOn24HourFormat)
			{
				pattern = Consts.DATE_FORMAT_HOUR_AND_MINUTE;
			}
			else 
			{
				pattern = Consts.DATE_FORMAT_HOUR_AND_MINUTE_WITH_AM_PM;
			}
		}
		else
		{
			pattern = Consts.DATE_FORMAT_HOUR_AND_MINUTE;
		}
		
		TimeZone timeZone = inputCalendar.getTimeZone();
		
		Locale locale = context.getResources().getConfiguration().locale;
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, timeZone, locale);
		
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
		String pattern = Consts.DATE_FORMAT_DAY_AND_MONTH;
		
		TimeZone timeZone = inputCalendar.getTimeZone();
		
		Locale locale = context.getResources().getConfiguration().locale;
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, timeZone, locale);
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a calendar from a specific date, sets the hour to the specified value and the minutes, seconds and milliseconds to zero
	 * 
	 */
	public static Calendar buildCalendarWithDateAndSpecificHour(
			final Calendar inputCalendar, 
			final int hour) 
	{
		Calendar calendar = (Calendar) inputCalendar.clone();
		
		int milisecondsValue = 0;
		int secondsValue = 0;
		int minutesValue = 0;
		int hoursValue = hour;
		
		int firstHourOfTheDay = ContentManager.sharedInstance().getFromStorageFirstHourOfTVDay();
		
		if(hour >= 0 && hour < firstHourOfTheDay) 
		{
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		if(hour < 0)
		{
			hoursValue = 0;
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, hoursValue);
		calendar.set(Calendar.MINUTE, minutesValue);
		calendar.set(Calendar.SECOND, secondsValue);
		calendar.set(Calendar.MILLISECOND, milisecondsValue);
		
		return calendar;
	}
	
	
	
	
	/**
	 * Generates a SimpleDateFormat instance with a set pattern, timeZone and locale
	 * 
	 */
	private static SimpleDateFormat getSimpleDateFormatWith(
			final String pattern,
			final TimeZone timeZone,
			final Locale locale) 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		
		dateFormat.setTimeZone(timeZone);
		
		return dateFormat;
	}
}