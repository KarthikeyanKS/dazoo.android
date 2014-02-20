package com.millicom.mitv.models.gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.mitv.Consts;
import com.mitv.utilities.DateUtilities;

public class Broadcast {
	private TVProgram program;
	private Long beginTimeMillis;
	private String beginTime;
	private String endTime;
	private BroadcastTypeEnum broadcastType;
	private String shareUrl;
	
	private transient Date beginTimeObject;
	private transient Date endTimeObject;
	
	public TVProgram getProgram() {
		return program;
	}
	
	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}
	
	public Date getBeginTime() {
		if(beginTimeObject == null) {
			SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
			try {
				beginTimeObject = dfmInput.parse(beginTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return beginTimeObject;
	}
	
	public Date getEndTime() {
		if(endTimeObject == null) {
			SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
			try {
				endTimeObject = dfmInput.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return endTimeObject;
	}
	
	public BroadcastTypeEnum getBroadcastType() {
		return broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	
	
	
	
	//TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */
	
	public boolean isRunning() {
		//TODO implement or delete me
		return false;
	}
	
	public int getDurationInMinutes() {
		//TODO implement or delete me
		return 0;
	}
	
	public int minutesSinceStart() {
		//TODO implement or delete me
		return 0;
	}
	
	public int getBeginTimeMillisGmt() {
		//TODO implement or delete me
				return 0;
	}
	
	public TVChannel getChannel() {
		//TODO implement or delete me
		return null;
	}
	
	public String getTvDateString() {
		//TODO implement or delete me
		return null;
	}
	
	public String getDayOfWeekWithTimeString() {
		//TODO implement or delete me
		return null;
	}
	
	public boolean hasStarted() {
		//TODO implement or delete me
		return false;
	}
	
	public boolean hasNotEnded() {
		//TODO implement or delete me
		return false;
	}
	
	public String getBeginTimeStringGmt() {
		//TODO implement or delete me
		return null;
	}
	
	public String getBeginTimeStringLocalHourAndMinute() {
		//TODO implement or delete me
		return null;
	}
	
	public String getEndTimeStringLocal() {
		//TODO implement or delete me
		return null;
	}
	
	public static int getClosestBroadcastIndex(ArrayList<Broadcast>	broadcasts) {
		//TODO implement or delete me
		return 0;
	}
	
	public static ArrayList<Broadcast> getBroadcastsStartingFromPosition(int indexOfNearestBroadcast, ArrayList<Broadcast> broadcasts, int howMany) {
		//TODO implement or delete me
		return null;
	}
	
	public String getBeginTimeStringLocalDayMonth() {
		//TODO implement or delete me
		return null;
	}
	
	public String getDayOfWeekString() {
		//TODO implement or delete me
		return null;
	}
	
	public static int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TVDate date) {
		//TODO implement or delete me
		return 0;
	}
	
	public void updateTimeToBeginAndTimeToEnd() {
		//TODO implement or delete me
	}
	
	public long getTimeToBegin() {
		//TODO implement or delete me
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
