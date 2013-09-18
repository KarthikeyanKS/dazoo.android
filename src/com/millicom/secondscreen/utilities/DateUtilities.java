package com.millicom.secondscreen.utilities;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.millicom.secondscreen.Consts;

public class DateUtilities {
	
	/**
	 * Converts a TvDate date string YYYY-MM-DD to the user-friendly format DD/MM
	 */
	public static String tvDateStringToDatePickerString(String date) throws ParseException {
		long time = 0;
		if (date != null && !date.equals("")) {
			SimpleDateFormat dfmInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
	 * Converts a iso-string to long
	 * 
	 * @param date
	 *            string in "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" format
	 * @return long value of the date
	 * @throws ParseException
	 */
	public static long isoStringDatePartToLong(String date) throws ParseException {
		long time = 0;
		if (date != null && !date.equals("")) {
			String dateOnly = date.substring(0, 10);
			SimpleDateFormat dfmInput = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
			time = dfmInput.parse(dateOnly).getTime();
		}
		return time;
	}

}
