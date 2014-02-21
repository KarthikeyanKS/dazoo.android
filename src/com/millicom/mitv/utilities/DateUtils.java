
package com.millicom.mitv.utilities;



import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.text.TextUtils;
import android.util.Log;
import com.millicom.mitv.ContentManager;
import com.mitv.Consts;



public abstract class DateUtils 
{	
	private static final String TAG = DateUtils.class.getName();
	
	private static float TOTAL_MILISECOUNDS_IN_ONE_MINUTE = 60000;
	
	
	
	public static Calendar convertFromStringToCalendar(final String inputString)
	{
		Calendar cal = Calendar.getInstance();
		
		if (!TextUtils.isEmpty(inputString)) 
		{
			SimpleDateFormat dfmInput = getDateFormat(Consts.ISO_DATE_FORMAT);
			
			try 
			{
				cal.setTime(dfmInput.parse(inputString));
			} 
			catch (ParseException e) 
			{
				Log.w(TAG, e.getMessage(), e);
			}
		}
		
		return cal;
	}
	
	
	
	public static Calendar convertFromStringToCalendar(final long inputTimeMilliseconds)
	{
		Calendar cal = Calendar.getInstance();
		
		if (inputTimeMilliseconds > 0) 
		{
			cal.setTimeInMillis(inputTimeMilliseconds);
		}
		
		return cal;
	}
	
	
	
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
	
	
	
	public static String getHourAndMinuteCompositionAsString(Calendar inputCalendar) 
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
	 * Create a SimpleDateFormat object using a string format, the string format could look like this:
	 * "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getDateFormat(final String format) 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return dateFormat;
	}
}
