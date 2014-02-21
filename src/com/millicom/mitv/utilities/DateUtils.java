
package com.millicom.mitv.utilities;



import java.text.NumberFormat;
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
	
	public static long TOTAL_MILISECOUNDS_IN_ONE_MINUTE = 60000;
	
	
	
	/**
	 * Converts a string input to a Calendar object
	 * The input string format should be in the format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 */
	public static Calendar convertFromStringToCalendar(final String inputString)
	{
		Calendar cal = Calendar.getInstance();
		
		if (!TextUtils.isEmpty(inputString))
		{
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			
			Locale locale = SecondScreenApplication.getCurrentLocale();
			
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
	 * Computes the total difference in minutes between the second Calendar object and the first Calendar object.
	 * Please note that the difference can be negative.
	 * It can optionally be set to always return the absolute difference.
	 */
	public static int calculateDifferenceInMinutesBetween(
			final Calendar beginTimeCalendar, 
			final Calendar endTimeCalendar,
			final boolean useAbslouteDifference,
			final int defaultValueIfNegative)
	{
		float endTimeInMinutes = endTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
		
		float beginTimeInMinute = beginTimeCalendar.getTimeInMillis() / TOTAL_MILISECOUNDS_IN_ONE_MINUTE;
		
	    int differenceInMinutes = (int) (endTimeInMinutes - beginTimeInMinute);
	    
	    if(differenceInMinutes < 0 && 
	       useAbslouteDifference == false)
	    {
	    	Log.w(TAG, "The calculated time difference is negative.");
	    }
	    else
	    {
	    	differenceInMinutes = (int) differenceInMinutes;
	    }
	    
	    if(useAbslouteDifference)
	    {
	    	differenceInMinutes = Math.abs(differenceInMinutes);
	    }
	    
	    return differenceInMinutes;
	}
	
	
	
	/**
	 * Builds a string representation with the hour and minute for the input calendar. 
	 */
	public static String getHourAndMinuteCompositionAsString(final Calendar inputCalendar) 
	{
		int hoursValue = inputCalendar.get(Calendar.HOUR_OF_DAY);
		int minutesValue = inputCalendar.get(Calendar.MINUTE);
		
		NumberFormat nfWith2Digits = NumberFormat.getInstance();
		nfWith2Digits.setMinimumIntegerDigits(2);
		nfWith2Digits.setMaximumIntegerDigits(2);
		nfWith2Digits.setMinimumFractionDigits(0);
		nfWith2Digits.setMaximumFractionDigits(0);
		
		StringBuilder sb = new StringBuilder();
		sb.append(nfWith2Digits.format(hoursValue));
		sb.append(Consts.TVDATE_DATE_HOUR_MINUTE_SEPARATOR);
		sb.append(nfWith2Digits.format(minutesValue));
		
		return sb.toString();
	}
	
	
	
	/**
	 * Builds a string representation for the day of the week of the input calendar.
	 * The representation is localized using the current Locale from the context.
	 * If the day of the week is either today or tomorrow, a localized representation is returned instead.
	 */
	public static String getDayOfTheWeekAsString(final Calendar inputCalendar)
	{
		String dayOfTheWeekAsString;
		
		Calendar now = Calendar.getInstance();
		
    	boolean isToday = false;
    	boolean isYesterday = false;
    	boolean isTomorrow = false;
    	
		isToday = (inputCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
				   inputCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
				   inputCalendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH));
 	
	 	if(isToday == false)
	 	{
	 		Calendar yesterday = (Calendar) now.clone();
	 		
	 		yesterday.add(Calendar.DAY_OF_MONTH, -1);
	     	
	     	isYesterday = (inputCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
	     				   inputCalendar.get(Calendar.MONTH) == yesterday.get(Calendar.MONTH) &&
	     				   inputCalendar.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH));
	     	
	     	if(isYesterday == false)
	     	{
	     		Calendar tomorrow = (Calendar) now.clone();
	     		
	     		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		        	
	     		isTomorrow = (inputCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
	     					  inputCalendar.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH) &&
	     					  inputCalendar.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH));
	     	}
	     	// No need for else
	 	}
	 			
		if (isToday)
		{
			dayOfTheWeekAsString = SecondScreenApplication.getInstance().getResources().getString(R.string.today);
		}
		else if(isTomorrow)
		{
			dayOfTheWeekAsString = SecondScreenApplication.getInstance().getResources().getString(R.string.tomorrow);
		}
		else
		{
			Locale locale = SecondScreenApplication.getCurrentLocale();
			
			dayOfTheWeekAsString = inputCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
		}
		
		return dayOfTheWeekAsString;
	}
	
	
	
	public static String getTimeOfDayAsString(final Calendar inputCalendar)
	{
		Context context = SecondScreenApplication.getInstance().getApplicationContext();
		
		boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
		
		String pattern;
		
		if(is24HourFormat)
		{
			pattern = "HH:mm";
		}
		else 
		{
			pattern = "HH:mm a";
		}
		
		TimeZone timeZone = inputCalendar.getTimeZone();
		
		Locale locale = SecondScreenApplication.getCurrentLocale();
		
		SimpleDateFormat formatter = getSimpleDateFormatWith(pattern, timeZone, locale);
		
		String timeOfDayAsString = formatter.format(inputCalendar.getTime());
		
		return timeOfDayAsString;
	}
	
	
	
	/**
	 * Builds a calendar from a specific date, sets the hour to the specified value and the minutes, seconds and milliseconds to zero
	 * 
	 */
	public static Calendar buildCalendarWithDateAndSpecificHour(
			final Date date, 
			final int hour) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int milisecondsValue = 0;
		int secondsValue = 0;
		int minutesValue = 0;
		int hoursValue = 0;
		
		int firstHourOfTheDay = ContentManager.sharedInstance().getFromStorageAppConfiguration().getFirstHourOfDay();
		
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
	 * Converts a string input to a Calendar object
	 * The input string format should be in the format: "yyyy-MM-dd'T'HH:mm:ss'Z'"
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