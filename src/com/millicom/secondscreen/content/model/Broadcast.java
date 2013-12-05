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
	private String beginTime;
	private String endTime;
	private long endTimeMillis;
	private Channel channel;
	private Program program;
	private String channelUrl;
	private long beginTimeMillis;
	private String shareUrl;
	private String beginTimeString;
	
	private long timeSinceBegin;
	private long timeToEnd;
	
	public Broadcast() {
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getBroadcastType() {
		return this.broadcastType;
	}
	
	public long getEndTimeMillis() {
		if(this.endTimeMillis == 0) {
			try {
				this.endTimeMillis = DateUtilities.isoStringToLong(beginTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.endTimeMillis;
	}
	
	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}
	
	

	public long getTimeSinceBegin() {
		return timeSinceBegin;
	}

	public void setTimeSinceBegin(long timeSinceBegin) {
		this.timeSinceBegin = timeSinceBegin;
	}

	public long getTimeToEnd() {
		return timeToEnd;
	}

	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getBeginTime() {
		return this.beginTime;
	}
	
	public String getBeginTimeString() {
		if(this.beginTimeString == null) {
			try {
				this.beginTimeString = DateUtilities.isoStringToTimeString(beginTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return this.beginTimeString;
	}
	
	public void setBeginTimeString(String beginTimeString) {
		this.beginTimeString = beginTimeString;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTime() {
		return this.endTime;
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

	public void setBeginTimeMillis(long beginTimeMillis) {
		this.beginTimeMillis = beginTimeMillis;
	}

	public long getBeginTimeMillis() {
		return this.beginTimeMillis;
	}
	
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
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
			if (getBeginTimeMillis() != 0 && other.getBeginTimeMillis() != 0 && getBeginTimeMillis() == other.getBeginTimeMillis()
					&& getChannel().getChannelId() != null && other.getChannel().getChannelId() != null
					&& (getChannel().getChannelId()).equals(other.getChannel().getChannelId())) {
				return true;
			}
		}
		return false;
	}

	public Broadcast(Parcel in) {
		beginTime = in.readString();
		endTime = in.readString();
		endTimeMillis = getEndTimeMillis();
		channel = (Channel) in.readParcelable(Channel.class.getClassLoader());
		program = (Program) in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
		beginTimeMillis = in.readLong();
		beginTimeString = getBeginTimeString();
		shareUrl = in.readString();
	}
	
	public Broadcast(JSONObject jsonBroadcast) {
		this.setBeginTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_BEGIN_TIME));
		this.setEndTime(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_END_TIME));
		this.setBeginTimeMillis(jsonBroadcast.optLong(Consts.DAZOO_BROADCAST_BEGIN_TIME_MILLIS));

		this.setShareUrl(jsonBroadcast.optString(Consts.DAZOO_BROADCAST_SHARE_URL));
		
		/* Lazy instantiation => fields get set using beginTimeMillis and endTime */
		this.getBeginTimeString();
		this.getEndTimeMillis();
		
		try {
			this.timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(getBeginTimeMillis());
			this.timeToEnd = DateUtilities.getAbsoluteTimeDifference(getEndTimeMillis());
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
		dest.writeString(beginTime);
		dest.writeString(endTime);
		dest.writeParcelable(channel, flags);
		dest.writeParcelable(program, flags);
		dest.writeString(channelUrl);
		dest.writeLong(beginTimeMillis);
		dest.writeString(shareUrl);
	}

	public static class BroadcastComparatorByTime implements Comparator<Broadcast> {

		@Override
		public int compare(Broadcast lhs, Broadcast rhs) {
			long left = lhs.getBeginTimeMillis();
			long right = rhs.getBeginTimeMillis();

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
		return "\n beginTime: " + beginTime + "\n endTime: " + endTime + "\n channel: " + channel + "\n program: " + program + "\n shareUrl" + shareUrl;
	}

	public static int getClosestBroadcastIndexUsingTime(ArrayList<Broadcast> broadcastList, long timeNow) {
		int nearestIndex = -1;

		long bestDistanceFoundYet = Long.MAX_VALUE;
		for (int i = 0; i < broadcastList.size(); i++) {
			Broadcast broadcast = broadcastList.get(i);
			long timeBroadcast = broadcast.getBeginTimeMillis();
			
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
			this.timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(getBeginTimeMillis());
			this.timeToEnd = DateUtilities.getAbsoluteTimeDifference(getEndTimeMillis());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getDurationInMinutes() {
		this.updateTimeToBeginAndTimeToEnd();

		int durationInMinutes = 0;
		durationInMinutes = (int) (timeSinceBegin - timeToEnd) / (1000 * 60);
	
		return durationInMinutes;
	}
}
