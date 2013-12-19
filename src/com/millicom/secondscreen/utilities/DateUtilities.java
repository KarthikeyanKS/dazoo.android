package com.millicom.secondscreen.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.TvDate;

public class DateUtilities {

	private static final String	TAG	= "DateUtilities";
	//TODO use info from backend for TV day range when available
	private static final int LAST_HOUR_OF_DAY = 6;
	
	public static final String tvDateToYearNumber(String tvDate){
		SimpleDateFormat dfmInput = getDateFormat(Consts.TVDATE_DATE_FORMAT);
		SimpleDateFormat dfmOutput = getDateFormat("yyyy");
		
		long time = 0;
		if (tvDate != null && !tvDate.equals("")) {
			try {
				time = dfmInput.parse(tvDate).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		String output = dfmOutput.format(time);
		return output;
	}
	
	public static final long convertTimeStampToLocalTime(long timestamp) {
		Date gmtTime = new Date(timestamp);
		Date localTime = new Date(gmtTime.getTime() + TimeZone.getDefault().getOffset(gmtTime.getTime()));
		long localTimeLong = localTime.getTime();

		return localTimeLong;
	}
	
	//TODO verify that this uses correct time zone!
	public static final String tvDateToMonthNumber(String tvDate){
		SimpleDateFormat dfmInput = getDateFormat(Consts.TVDATE_DATE_FORMAT);
		SimpleDateFormat dfmOutput = getDateFormat("MM");
		
		long time = 0;
		if (tvDate != null && !tvDate.equals("")) {
			try {
				time = dfmInput.parse(tvDate).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		String output = dfmOutput.format(time);
		return output;
	}
	
	//TODO verify that this uses correct time zone!
	public static String timeStringUsingTvDateAndHour(TvDate tvDate, int hour) {
		SimpleDateFormat df = getDateFormat(Consts.ISO_DATE_FORMAT);
		Date date = dateFromTvDateAndHour(tvDate, hour);
		String timeNowStr = df.format(date);
		return timeNowStr;
	}
	
	public static long timeAsLongFromTvDateAndHour(TvDate tvDate, int hour) {
		long time = 0;
		Date date = dateFromTvDateAndHour(tvDate, hour);
		time = date.getTime();
		return time;
	}
	
	public static Date dateFromTvDateAndHour(TvDate tvDate, int hour) {
		String year = DateUtilities.tvDateToYearNumber(tvDate.getDate());
		String month = DateUtilities.tvDateToMonthNumber(tvDate.getDate());
		String day = DateUtilities.tvDateToDayNumber(tvDate.getDate());
				
		// current "today" is set to the date, selected by the user
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.valueOf(year));
		calendar.set(Calendar.MONTH,Integer.valueOf(month) -1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		
		if(hour >= 0 && hour < LAST_HOUR_OF_DAY) {
			/* Add one */
			int dayInt = Integer.valueOf(day) + 1;
			calendar.set(Calendar.DAY_OF_MONTH, dayInt);
		}
		
		Date date = calendar.getTime();
		
		return date;
	}
	
	public static final String tvDateToDayNumber(String tvDate){
		SimpleDateFormat dfmInput = getDateFormat(Consts.TVDATE_DATE_FORMAT);
		SimpleDateFormat dfmOutput = getDateFormat("dd");
		
		long time = 0;
		if (tvDate != null && !tvDate.equals("")) {
			try {
				time = dfmInput.parse(tvDate).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		String output = dfmOutput.format(time);
		return output;
	}
	
	//TODO scrap a lot of code in this and get current date using new Date() only?
	public static final String todayDateAsTvDate(){
		SimpleDateFormat dfmInput = getDateFormat(Consts.ISO_DATE_FORMAT);
		String dateNow = dfmInput.format(new Date());
		SimpleDateFormat dfmOutput = getDateFormat(Consts.TVDATE_DATE_FORMAT);
		
		long time = 0;
		if (dateNow != null && !dateNow.equals("")) {
			try {
				time = dfmInput.parse(dateNow).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String output = dfmOutput.format(time);
		return output;
	}
	
	//TODO verify that this uses correct time zone!
	public static final String isoDateStringToTvDateString(String date) throws ParseException{
		long time = isoStringToLong(date);
		String output = isoDateToTvDateString(time);
		return output;
	}
	
	public static final String isoDateToTvDateString(long time) throws ParseException{
		SimpleDateFormat dfmOutput = getDateFormat(Consts.TVDATE_DATE_FORMAT);
		String output = dfmOutput.format(time);
		return output;
	}
	
	//TODO verify that this uses correct time zone!
	public static boolean isTimeInFuture(String beginTime) throws ParseException {
		SimpleDateFormat df = getDateFormat(Consts.ISO_DATE_FORMAT);
		Date date = df.parse(beginTime);
		if (new Date().after(date)){
			return true;
		} 
		else return false;
	}
	
	public static boolean isTimeInFuture(long beginTime) throws ParseException {
		Date date = new Date(beginTime); 
		if (new Date().after(date)){
			return true;
		} 
		else return false;
	}

	//TODO verify that this uses correct time zone!
	public static Calendar getTimeFifteenMinBefore(String beginTime) throws ParseException {
		SimpleDateFormat df = getDateFormat(Consts.ISO_DATE_FORMAT);

		Date date = df.parse(beginTime);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, Consts.NOTIFY_MINUTES_BEFORE_THE_BROADCAST);

		return calendar;
	}

	/**
	 * Converts a TvDate date string YYYY-MM-DD to the user-friendly format DD/MM
	 */
	public static String tvDateStringToDatePickerString(String date) throws ParseException {
		long time = timeWithStringAndFormat(date, Consts.TVDATE_DATE_FORMAT);
		String output = tvDateStringToDatePickerString(time);
		return output;
	}
	
	public static String tvDateStringToDatePickerString(long time) throws ParseException {
		SimpleDateFormat dfmOutput = getDateFormat("dd/MM");
		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * Converts a iso-string to unix time
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss'Z'" format
	 * @return date string in "HH:mm" format
	 * @throws ParseException
	 */

	public static String isoStringToTimeString(String date) throws ParseException {
		long time = isoStringToLong(date);
		String output = timeToTimeString(time);
		return output;
	}
	
	public static SimpleDateFormat getDateFormat(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return dateFormat;
	}
	
	public static String timeToTimeString(long time) throws ParseException {
		SimpleDateFormat dfmOutput = getDateFormat("HH:mm");
		
		String output = dfmOutput.format(time);
		String out2 = dfmOutput.format(time);
		return output;
	}

	/**
	 * Converts a iso-string to unix time
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss'Z'" format
	 * @return date string in "HH" format
	 * @throws ParseException
	 */

	public static String isoStringToHourString(String date) throws ParseException {
		long time = isoStringToLong(date);
		SimpleDateFormat dfmOutput = getDateFormat("H");
		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * Converts a iso-string to date
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return date string in "EEE MMMM d, y" format
	 * @throws ParseException
	 */
	public static String isoStringToDateString(String date) throws ParseException {
		long time = isoStringToLong(date);
		SimpleDateFormat dfmOutput = getDateFormat("EEE MMMM d, y");

		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return date string in "EEEE MMMM d" format
	 * @throws ParseException
	 */
	public static String isoStringToWeekDateString(String date) throws ParseException {
		long time = isoStringToLong(date);
		SimpleDateFormat dfmOutput = getDateFormat("EEEE MMMM d");

		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * Converts a iso-string to date and time
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return date string in "EEE MMMM d, y HH:mm" format
	 * @throws ParseException
	 */
	public static String isoStringToDateAndTimeString(String date) throws ParseException {
		long time = isoStringToLong(date);
		SimpleDateFormat dfmOutput = getDateFormat("EEE MMMM d, y HH:mm");

		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * Converts a iso-string to date and time
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return date string in "EEE MMMM d, y HH:mm" format
	 * @throws ParseException
	 */
	public static String isoStringToDateShortAndTimeString(String date) throws ParseException {
		long time = isoStringToLong(date);

		SimpleDateFormat dfmOutput = getDateFormat("EEE dd/MM, y HH:mm");

		String output = dfmOutput.format(time);
		return output;
	}
	
	public static long timeWithStringAndFormat(String timeString, String format) throws ParseException {
		long time = 0;
		if (timeString != null && !timeString.equals("")) {
			SimpleDateFormat dfmInput = getDateFormat(format);
			time = dfmInput.parse(timeString).getTime();
		}
		return time;
	}
	
	public static long timeWithStringAndFormatAdjustedToCurrentTimeZone(String timeString, String format) throws ParseException {
		long time = 0;
		if (timeString != null && !timeString.equals("")) {
			SimpleDateFormat dfmInput = getDateFormat(format);
			long gmtTime = dfmInput.parse(timeString).getTime();
			time = timeAdjustedToCurrentTimeZone(gmtTime);
		}
		return time;
	}
	
	public static long timeAdjustedToCurrentTimeZone(long gmtTime) {
		TimeZone tz = TimeZone.getDefault();
		Date nowDate = new Date();
		int millisOffset = tz.getOffset(nowDate.getTime());
		long currentTimeZoneTime = gmtTime + millisOffset;
		
		return currentTimeZoneTime;
	}

	/**
	 * Converts a iso-string to long
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return long value
	 * @throws ParseException
	 */
	public static long isoStringToLong(String date) throws ParseException {
		long time = timeWithStringAndFormat(date, Consts.ISO_DATE_FORMAT);
		return time;
	}
	
	/**
	 * Converts a iso-string to long and adjusts the time to the current time zone.
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return long value
	 * @throws ParseException
	 */
	public static long isoStringToLongAdjustToCurrentTimeZone(String date) throws ParseException {
		long time = timeWithStringAndFormatAdjustedToCurrentTimeZone(date, Consts.ISO_DATE_FORMAT);
		return time;
	}
	

	/**
	 * Converts a date-string to long
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd" format
	 * @return long value of the date
	 * @throws ParseException
	 */
	public static long isoStringDatePartToLong(String date) throws ParseException {
		long time = timeWithStringAndFormat(date, Consts.TVDATE_DATE_FORMAT);
		return time;
	}

	/**
	 * Converts a date-string to long
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd HH:mm" format
	 * @return long value of the date
	 * @throws ParseException
	 */

	/**
	 * Gets the time that the current day started
	 * 
	 * @return
	 */
	public static long getCurrentDayTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * Gets the time that the specific day started
	 * 
	 * @return
	 */
	public static long getDay(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * Gets the time of the day, i.e. 23:14 or 11:15 PM
	 * 
	 * @param context
	 * @param time
	 * @return
	 */
	public static String getTimeOfDayFormatted(Context context, long time) {
		String formatString;
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			formatString = "HH:mm";
		} else {
			formatString = "HH:mm a";
		}
		SimpleDateFormat formatter = getDateFormat(formatString);
		Date date = new Date(time);
		return formatter.format(date);
	}
	
	public static String getTimeOfDayFormatted(long time) {
		Context appContext = SecondScreenApplication.getInstance().getApplicationContext();
		return getTimeOfDayFormatted(appContext, time);
	}

	/**
	 * Get the current hour as a string
	 * 
	 * @param
	 * @return current hour as string
	 */
	public static String getCurrentHourString() {
		SimpleDateFormat df = new SimpleDateFormat("HH", Locale.getDefault());
		String hourNow = df.format(new Date());
		return hourNow;
	}

	/**
	 * Get the date as a name string
	 * 
	 * @return name of day of week
	 */
	public static String isoStringToDayOfWeek(String date) throws ParseException {
		long time = isoStringToLong(date);
		String output = isoStringToDayOfWeek(time);
		return output;
	}
	
	public static String isoStringToDayOfWeek(long time) throws ParseException {
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEEE", Locale.getDefault());

		String output = dfmOutput.format(time);
		return output;
	}

	/**
	 * Get the date as a name string and day/month digits
	 * 
	 * @return date in format "EEEE DD/MM"
	 */
	public static String isoStringToDayOfWeekAndDate(String date) throws ParseException {
		long time = isoStringToLong(date);
		String output = isoStringToDayOfWeekAndDate(time);
		return output;
	}
	
	public static String isoStringToDayOfWeekAndDate(long time) throws ParseException {
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEEE dd/MM", Locale.getDefault());

		String output = dfmOutput.format(time);
		return output;
	}
	
	//TODO verify that this uses correct time zone!
	/**
	 * Get the current hour as a long
	 * 
	 * @return current hour as a long
	 */
	public static long getCurrentHourLong(Context context) throws ParseException {
		long time = 0;
		SimpleDateFormat dfmInput;
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			dfmInput = new SimpleDateFormat("HH", Locale.getDefault());
		} else {
			dfmInput = new SimpleDateFormat("HH a", Locale.getDefault());
		}
		// TimeZone tz = TimeZone.getTimeZone("UTC");
		// dfmInput.setTimeZone(tz);
		String hourNow = dfmInput.format(new Date());
		time = dfmInput.parse(hourNow).getTime();
		return time;
	}

	//TODO verify that this uses correct time zone!
	/**
	 * Get the current time as a long
	 * 
	 * @return current hour and minute as a long
	 */
	public static long getCurrentHourMinuteLong(Context context) throws ParseException {
		long time = 0;
		SimpleDateFormat dfmInput;
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			dfmInput = new SimpleDateFormat("HH:mm ", Locale.getDefault());
		} else {
			dfmInput = new SimpleDateFormat("HH:mm a", Locale.getDefault());
		}
		// TimeZone tz = TimeZone.getTimeZone("UTC");
		// dfmInput.setTimeZone(tz);
		String hourNow = dfmInput.format(new Date());
		time = dfmInput.parse(hourNow).getTime();
		return time;
	}
	
	/**
	 * Get the difference in minutes between two submitted time values
	 * 
	 * @return number of minutes as integer
	 */
	public static int getDifferenceInMinutes(long timeSubmitted, long timeCurrent) throws ParseException {
		long difference = timeCurrent - timeSubmitted;

		int days = (int) (difference / (1000 * 60 * 60 * 24));
		int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
		return (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
	}

	/**
	 * Get the difference in minutes between current time and submitted
	 * 
	 * @return number of minutes as integer
	 */
	public static int getDifferenceInMinutes(long timeSubmitted) throws ParseException {
		Date currentDate = new Date();
		long timeCurrent = currentDate.getTime();

		long difference = timeCurrent - timeSubmitted;

		int days = (int) (difference / (1000 * 60 * 60 * 24));
		int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
		return (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
	}

	/**
	 * Get the absolute difference in time units between current time and submitted
	 * 
	 * @return time difference as long
	 */
	public static long getAbsoluteTimeDifference(long timeSubmitted) throws ParseException {
		Date currentDate = new Date();
		long timeCurrent = currentDate.getTime();

		long difference = (timeSubmitted - timeCurrent);
		
		return difference;
	}
}
