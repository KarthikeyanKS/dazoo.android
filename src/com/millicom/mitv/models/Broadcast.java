package com.millicom.mitv.models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.millicom.mitv.models.gson.BroadcastJSON;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.utilities.DateUtilities;

/**
 * All varaibles in this class needs to be either transient or use the
 * @Expose(deserialize=false) annotation, else the JSON parsing using GSON will
 * fail.
 * 
 * @author consultant_hdme
 * 
 */
public class Broadcast extends BroadcastJSON {
	
	private transient Calendar beginTimeCalendar;
	private transient Calendar endTimeCalendar;
	
	/**
	 * Lazy instantiated variable
	 * @return
	 */
	public Calendar getBeginTimeCalendar() {
		if(beginTimeCalendar == null) {
			beginTimeCalendar = DateUtilities.convertFromStringToCalendar(beginTime);
		}
		return beginTimeCalendar;
	}

	/**
	 * Lazy instantiated variable
	 * @return
	 */
	public Calendar getEndTimeCalendar() {
		if(endTimeCalendar == null) {
			endTimeCalendar = DateUtilities.convertFromStringToCalendar(endTime);
		}
		return endTimeCalendar;
	}

	// TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */

	public boolean isRunning() {
		// TODO implement or delete me
		return false;
	}

	public int getDurationInMinutes() {
		// TODO implement or delete me
		return 0;
	}

	public int minutesSinceStart() {
		// TODO implement or delete me
		return 0;
	}

	public int getBeginTimeMillisGmt() {
		// TODO implement or delete me
		return 0;
	}

	public TVChannel getChannel() {
		// TODO implement or delete me
		return null;
	}

	public String getTvDateString() {
		// TODO implement or delete me
		return null;
	}

	public String getDayOfWeekWithTimeString() {
		// TODO implement or delete me
		return null;
	}

	public boolean hasStarted() {
		// TODO implement or delete me
		return false;
	}

	public boolean hasNotEnded() {
		// TODO implement or delete me
		return false;
	}

	public String getBeginTimeStringGmt() {
		// TODO implement or delete me
		return null;
	}

	public String getBeginTimeStringLocalHourAndMinute() {
		// TODO implement or delete me
		return null;
	}

	public String getEndTimeStringLocal() {
		// TODO implement or delete me
		return null;
	}

	public static int getClosestBroadcastIndex(ArrayList<Broadcast> broadcasts) {
		// TODO implement or delete me
		return 0;
	}

	public static ArrayList<Broadcast> getBroadcastsStartingFromPosition(int indexOfNearestBroadcast, ArrayList<Broadcast> broadcasts, int howMany) {
		// TODO implement or delete me
		return null;
	}

	public String getBeginTimeStringLocalDayMonth() {
		// TODO implement or delete me
		return null;
	}

	public String getDayOfWeekString() {
		// TODO implement or delete me
		return null;
	}

	public static int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TVDate date) {
		// TODO implement or delete me
		return 0;
	}

	public void updateTimeToBeginAndTimeToEnd() {
		// TODO implement or delete me
	}

	public long getTimeToBegin() {
		// TODO implement or delete me
		return 0;
	}

	public static class BroadcastComparatorByTime implements Comparator<Broadcast> {

		@Override
		public int compare(Broadcast lhs, Broadcast rhs) {
			long left = lhs.getBeginTimeMillisGmt();
			long right = rhs.getBeginTimeMillisGmt();

			if (left > right) {
				return 1;
			} else if (left < right) {
				return -1;
			} else {
				String leftProgramName = lhs.getProgram().getTitle();
				String rightProgramName = rhs.getProgram().getTitle();
				return leftProgramName.compareTo(rightProgramName);
			}
		}
	}
}
