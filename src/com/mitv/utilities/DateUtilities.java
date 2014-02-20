package com.mitv.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.text.TextUtils;
import android.util.Log;

import com.mitv.Consts;

public class DateUtilities {
	
	
	private static final String TAG = DateUtilities.class.getName();
	
	public static Calendar convertFromStringToCalendar(String inputString)
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
	
	
	
	public static Calendar convertFromStringToCalendar(long inputTimeMilliseconds)
	{
		Calendar cal = Calendar.getInstance();
		
		if (inputTimeMilliseconds > 0) 
		{
			cal.setTimeInMillis(inputTimeMilliseconds);
		}
		
		return cal;
	}
	
	
	/**
	 * Create a SimpleDateFormat object using a string format, the string format could look like this:
	 * "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getDateFormat(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return dateFormat;
	}
}
