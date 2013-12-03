package com.millicom.secondscreen.utilities;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.TvDate;

public class DateUtilities {

	private static final String	TAG	= "DateUtilities";
	
	public static final String tvDateToYearNumber(String tvDate){
		SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
		SimpleDateFormat dfmOutput = new SimpleDateFormat("yyyy",  Locale.getDefault());
		
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
	
	public static final String tvDateToMonthNumber(String tvDate){
		SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
		SimpleDateFormat dfmOutput = new SimpleDateFormat("MM",  Locale.getDefault());
		
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
	
	public static String timeStringUsingTvDateAndHour(TvDate date, int hour) {
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		
		String year = DateUtilities.tvDateToYearNumber(date.getDate());
		String month = DateUtilities.tvDateToMonthNumber(date.getDate());
		String day = DateUtilities.tvDateToDayNumber(date.getDate());
		
		// current "today" is set to the date, selected by the user
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.valueOf(year));
		calendar.set(Calendar.MONTH,Integer.valueOf(month) -1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
		calendar.set(Calendar.HOUR_OF_DAY, hour+1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		String timeNowStr = df.format(calendar.getTime());
		
		return timeNowStr;
	}
	
	public static final String tvDateToDayNumber(String tvDate){
		SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
		SimpleDateFormat dfmOutput = new SimpleDateFormat("dd",  Locale.getDefault());
		
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
	
	
	public static final String todayDateAsTvDate(){
		SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		String dateNow = dfmInput.format(new Date());
		SimpleDateFormat dfmOutput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
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
	
	public static final String isoDateStringToTvDateString(String date) throws ParseException{
		SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		SimpleDateFormat dfmOutput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
		long time = 0;
		if (date != null && !date.equals("")) {
			time = dfmInput.parse(date).getTime();
		}
		String output = dfmOutput.format(time);
		return output;
	}
	
	public static boolean isTimeInFuture(String beginTime) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		Date date = df.parse(beginTime);
		if (new Date().after(date)){
			return true;
		} 
		else return false;
	}

	public static Calendar getTimeFifteenMinBefore(String beginTime) throws ParseException {

		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());

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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("dd/MM", Locale.getDefault());
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("HH:mm", Locale.getDefault());
		String output = dfmOutput.format(time);
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("H", Locale.getDefault());
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEE MMMM d, y", Locale.getDefault());

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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEEE MMMM d", Locale.getDefault());

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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEE MMMM d, y HH:mm", Locale.getDefault());

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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEE dd/MM, y HH:mm", Locale.getDefault());

		String output = dfmOutput.format(time);
		return output;
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.TVDATE_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
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
		SimpleDateFormat formatter;
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
		} else {
			formatter = new SimpleDateFormat("HH:mm a", Locale.getDefault());
		}
		Date date = new Date(time);
		return formatter.format(date);
	}

	/**
	 * Get the current hour as a string
	 * 
	 * @param
	 * @return current hour as string
	 */
	public static String getCurrentHourString() {
		SimpleDateFormat df = new SimpleDateFormat("HH", Locale.getDefault());
		// TimeZone tz = TimeZone.getTimeZone("UTC");
		// df.setTimeZone(tz);
		String dateNow = df.format(new Date());
		return dateNow;
	}

	/**
	 * Get the date as a name string
	 * 
	 * @return name of day of week
	 */
	public static String isoStringToDayOfWeek(String date) throws ParseException {
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
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
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(date).getTime();
		}
		SimpleDateFormat dfmOutput = new SimpleDateFormat("EEEE dd/MM", Locale.getDefault());

		String output = dfmOutput.format(time);
		return output;
	}

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
	public static int getDifferenceInMinutes(String timeOne, String timeTwo) throws ParseException {
		long timeSubmitted = DateUtilities.isoStringToLong(timeOne);
		long timeCurrent = DateUtilities.isoStringToLong(timeTwo);

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
	public static int getDifferenceInMinutes(String submittedTime) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		// TimeZone tz = TimeZone.getTimeZone("UTC");
		// df.setTimeZone(tz);
		String dateNow = df.format(new Date());

		long timeSubmitted = DateUtilities.isoStringToLong(submittedTime);
		long timeCurrent = DateUtilities.isoStringToLong(dateNow);

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
	public static long getAbsoluteTimeDifference(String submittedTime) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		// TimeZone tz = TimeZone.getTimeZone("UTC");
		// df.setTimeZone(tz);
		String dateNow = df.format(new Date());

		long timeSubmitted = DateUtilities.isoStringToLong(submittedTime);
		long timeCurrent = DateUtilities.isoStringToLong(dateNow);

		return (timeCurrent - timeSubmitted);
	}
}
