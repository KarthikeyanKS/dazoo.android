package com.millicom.secondscreen.content.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.utilities.DateUtilities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Broadcast implements Parcelable {

	private static final String TAG = "Broadcast";

	private String broadcastType;

	private Channel channel;
	private Program program;
	private String channelUrl;
	private String shareUrl;

	private long beginTimeMillisGmt;
	private long endTimeMillisGmt;
	private long beginTimeMillisLocal;
	private long endTimeMillisLocal;
	
	private String beginTimeStringGmt;
	private String endTimeStringGmt;
	
	private String beginTimeStringLocal;
	private String endTimeStringLocal;
	
	private String beginTimeStringLocalHourAndMinute;
	private String beginTimeStringLocalDayMonth;
	
	private long timeSinceBegin;
	private long timeToEnd;
	
	private String dayOfWeekString;
	private String dayOfWeekWithTimeString;
	private String tvDateString; // yyyy-mm-dd
	
	public Broadcast() {
	}

	
	
	public String getBeginTimeStringLocal() {
		return beginTimeStringLocal;
	}



	public void setBeginTimeStringLocal(String beginTimeStringLocal) {
		this.beginTimeStringLocal = beginTimeStringLocal;
	}



	public String getEndTimeStringLocal() {
		return endTimeStringLocal;
	}



	public void setEndTimeStringLocal(String endTimeStringLocal) {
		this.endTimeStringLocal = endTimeStringLocal;
	}



	public String getTvDateString() {
		return tvDateString;
	}

	public void setTvDateString(String tvDateString) {
		this.tvDateString = tvDateString;
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getBroadcastType() {
		return this.broadcastType;
	}
	
	public long getEndTimeMillisGmt() {
		return this.endTimeMillisGmt;
	}
	
	public void setEndTimeMillisGmt(long endTimeMillisGmt) {
		this.endTimeMillisGmt = endTimeMillisGmt;
	}
	
	public long getBeginTimeMillisLocal() {
		return beginTimeMillisLocal;
	}

	public void setBeginTimeMillisLocal(long beginTimeMillisLocal) {
		this.beginTimeMillisLocal = beginTimeMillisLocal;
	}

	public long getEndTimeMillisLocal() {
		return endTimeMillisLocal;
	}

	public void setEndTimeMillisLocal(long endTimeMillisLocal) {
		this.endTimeMillisLocal = endTimeMillisLocal;
	}

	public String getBeginTimeStringLocalDayMonth() {
		return beginTimeStringLocalDayMonth;
	}

	public void setBeginTimeStringLocalDayMonth(String beginTimeStringLocalDayMonth) {
		this.beginTimeStringLocalDayMonth = beginTimeStringLocalDayMonth;
	}

	public String getBeginTimeStringGmt() {
		return this.beginTimeStringGmt;
	}

	public long getTimeSinceBegin() {
		return this.timeSinceBegin;
	}

	public void setTimeSinceBegin(long timeSinceBegin) {
		this.timeSinceBegin = timeSinceBegin;
	}

	public long getTimeToEnd() {
		return this.timeToEnd;
	}

	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}

	public void setBeginTimeStringGmt(String beginTimeStringGmt) {
		this.beginTimeStringGmt = beginTimeStringGmt;
	}
	
	public String getBeginTimeStringLocalHourAndMinute() {		
		return this.beginTimeStringLocalHourAndMinute;
	}
	
	public void setBeginTimeStringLocalHourAndMinute(String beginTimeStringLocalHourAndMinute) {
		this.beginTimeStringLocalHourAndMinute = beginTimeStringLocalHourAndMinute;
	}

	public void setEndTimeStringGmt(String beginTimeStringHourAndMinute) {
		this.endTimeStringGmt = beginTimeStringHourAndMinute;
	}

	public String getEndTimeStringGmt() {
		return this.endTimeStringGmt;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Program getProgram() {
		return this.program;
	}

	public void setChannelUrl(String channelUrl) {
		this.channelUrl = channelUrl;
	}

	public String getChannelUrl() {
		return this.channelUrl;
	}

	public void setBeginTimeMillisGmt(long beginTimeStringHourAndMinute) {
		this.beginTimeMillisGmt = beginTimeStringHourAndMinute;
	}

	public long getBeginTimeMillisGmt() {
		return this.beginTimeMillisGmt;
	}
	
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getDayOfWeekString() {
		return dayOfWeekString;
	}

	public void setDayOfWeekString(String dayOfWeekString) {
		this.dayOfWeekString = dayOfWeekString;
	}

	
	
	public String getDayOfWeekWithTimeString() {
		return dayOfWeekWithTimeString;
	}

	public void setDayOfWeekWithTimeString(String dayOfWeekWithTimeString) {
		this.dayOfWeekWithTimeString = dayOfWeekWithTimeString;
	}

	public String getShareUrl() {
		return this.shareUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Broadcast) {
			Broadcast other = (Broadcast) o;
			if (getBeginTimeMillisGmt() != 0 && other.getBeginTimeMillisGmt() != 0 && getBeginTimeMillisGmt() == other.getBeginTimeMillisGmt()
					&& getChannel().getChannelId() != null && other.getChannel().getChannelId() != null
					&& (getChannel().getChannelId()).equals(other.getChannel().getChannelId())) {
				return true;
			}
		}
		return false;
	}

	public Broadcast(Parcel in) {
		beginTimeStringGmt = in.readString();
		endTimeStringGmt = in.readString();
		channel = (Channel) in.readParcelable(Channel.class.getClassLoader());
		program = (Program) in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
		beginTimeMillisGmt = in.readLong();
		beginTimeStringLocalHourAndMinute = getBeginTimeStringLocalHourAndMinute();
		shareUrl = in.readString();
	}
	
	
	public Broadcast(JSONObject jsonBroadcast) {
		String beginTimeStringGmt = jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME);
		String endTimeStringGmt = jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME);
		long beginTimeMillisGMT = jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS);
		long beginTimeMillisLocal = DateUtilities.convertTimeStampToLocalTime(beginTimeMillisGMT);
		
		
		this.setBeginTimeStringGmt(beginTimeStringGmt);
		this.setEndTimeStringGmt(endTimeStringGmt);
		
		this.setBeginTimeMillisGmt(beginTimeMillisGMT);
		this.setBeginTimeMillisLocal(beginTimeMillisLocal);

		this.setShareUrl(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_SHARE_URL));
			try {
				String beginTimeStringLocalHourAndMinute = DateUtilities.getTimeOfDayFormatted(beginTimeMillisLocal);
				this.setBeginTimeStringLocalHourAndMinute(beginTimeStringLocalHourAndMinute);
				
				long endTimeMillisGmt = DateUtilities.isoStringToLong(endTimeStringGmt);
				long endTimeMillisLocal = DateUtilities.convertTimeStampToLocalTime(endTimeMillisGmt);
				
				this.setEndTimeMillisGmt(endTimeMillisGmt);
				this.setEndTimeMillisLocal(endTimeMillisLocal);
				
				String beginTimeStringLocalDayMonth = DateUtilities.tvDateStringToDatePickerString(beginTimeMillisLocal);
				this.setBeginTimeStringLocalDayMonth(beginTimeStringLocalDayMonth);
				
				String dayOfWeekString = DateUtilities.isoStringToDayOfWeek(beginTimeMillisLocal);
				dayOfWeekString = Character.toUpperCase(dayOfWeekString.charAt(0)) + dayOfWeekString.substring(1);
				this.setDayOfWeekString(dayOfWeekString);
				
				String dayOfWeekAndTimeString = new StringBuilder().append(dayOfWeekString).append(" - ").append(beginTimeStringLocalHourAndMinute).toString();
				this.setDayOfWeekWithTimeString(dayOfWeekAndTimeString);
				
				String tvDateString = DateUtilities.isoDateToTvDateString(beginTimeMillisLocal);
				this.setTvDateString(tvDateString);
				
				String beginTimeStringLocal = DateUtilities.timeToTimeString(beginTimeMillisLocal);
				this.setBeginTimeStringLocal(beginTimeStringLocal);
				
				String endTimeStringLocal = DateUtilities.timeToTimeString(endTimeMillisLocal);
				this.setEndTimeStringLocal(endTimeStringLocal);
				
				updateTimeToBeginAndTimeToEnd();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		JSONObject jsonChannel = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_CHANNEL);
		if (jsonChannel != null) {
			Channel channel = new Channel(jsonChannel);
			if(channel != null) {
				this.setChannel(channel);
			}
		}

		JSONObject jsonProgram = jsonBroadcast.optJSONObject(Consts.DAZOO_BROADCAST_PROGRAM);
		if (jsonProgram != null) {
			Program program = new Program(jsonProgram);
			if(program != null) {
				this.setProgram(program);
			}
		}

		this.setBroadcastType(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BROADCAST_TYPE));
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(beginTimeStringGmt);
		dest.writeString(endTimeStringGmt);
		dest.writeParcelable(channel, flags);
		dest.writeParcelable(program, flags);
		dest.writeString(channelUrl);
		dest.writeLong(beginTimeMillisGmt);
		dest.writeString(shareUrl);
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

	public static final Parcelable.Creator<Broadcast> CREATOR = new Parcelable.Creator<Broadcast>() {
		public Broadcast createFromParcel(Parcel in) {
			return new Broadcast(in);
		}

		public Broadcast[] newArray(int size) {
			return new Broadcast[size];
		}
	};

	@Override
	public String toString() {
		return "\n beginTime: " + beginTimeStringGmt + "\n endTime: " + endTimeStringGmt + "\n channel: " + channel + "\n program: " + program + "\n shareUrl" + shareUrl;
	}

	public static int getClosestBroadcastIndexUsingTime(ArrayList<Broadcast> broadcastList, long timeNow) {
		int nearestIndex = -1;

		long bestDistanceFoundYet = Long.MAX_VALUE;
		for (int i = 0; i < broadcastList.size(); i++) {
			Broadcast broadcast = broadcastList.get(i);
			long timeBroadcast = broadcast.getBeginTimeMillisLocal();	
			long d = Math.abs(timeNow - timeBroadcast);
			if (d < bestDistanceFoundYet && timeNow <= timeBroadcast) {
				// if (d < bestDistanceFoundYet) {
				nearestIndex = i;
				bestDistanceFoundYet = d;
			}
		}
		return nearestIndex;
	}

	public static int getClosestBroadcastIndex(ArrayList<Broadcast> broadcastList) {
		int nearestIndex = -1;

		// get the time now
//		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
//		String timeNowStr = df.format(new Date());
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();

		nearestIndex = getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

		return nearestIndex;
	}

	public static int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TvDate date) {
		int nearestIndex = 0;

		long timeNow = DateUtilities.timeAsLongFromTvDateAndHour(date, hour);

		nearestIndex = getClosestBroadcastIndexUsingTime(broadcastList, timeNow);

		return nearestIndex;
	}

	public static ArrayList<Broadcast> getBroadcastsStartingFromPosition(int index, ArrayList<Broadcast> broadcastList, int numberOfClosest) {
		ArrayList<Broadcast> nextBroadcasts = new ArrayList<Broadcast>();

		for (int j = index; j < index + numberOfClosest; j++) {
			if (j < broadcastList.size()) {
				nextBroadcasts.add(broadcastList.get(j));
			}
		}
		return nextBroadcasts;
	}
	
	public void updateTimeToBeginAndTimeToEnd() {
		try {
			long timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(beginTimeMillisLocal);
			long timeToEnd = DateUtilities.getAbsoluteTimeDifference(endTimeMillisLocal);
			
			this.setTimeSinceBegin(timeSinceBegin);
			this.setTimeToEnd(timeToEnd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getDurationInMinutes() {
		this.updateTimeToBeginAndTimeToEnd();

		int durationInMinutes = 0;
		durationInMinutes = (int) (timeSinceBegin - timeToEnd) / (1000 * 60);
	
		durationInMinutes = Math.abs(durationInMinutes);
		
		return durationInMinutes;
	}
}
