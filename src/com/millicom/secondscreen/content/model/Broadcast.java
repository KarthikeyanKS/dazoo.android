package com.millicom.secondscreen.content.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.utilities.DateUtilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Broadcast implements Parcelable {

	private static final String	TAG	= "Broadcast";

	private String				broadcastType;
	private String				beginTime;
	private String				endTime;
	private Channel				channel;
	private Program				program;
	private String				channelUrl;
	private long				beginTimeMillis;
	private String				shareUrl;
	
	public Broadcast() {
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getBroadcastType() {
		return this.broadcastType;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getBeginTime() {
		return this.beginTime;
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
			if (getBeginTimeMillis() != 0 && other.getBeginTimeMillis() != 0 && getBeginTimeMillis() == other.getBeginTimeMillis() && getChannel().getChannelId() != null
					&& other.getChannel().getChannelId() != null && (getChannel().getChannelId()).equals(other.getChannel().getChannelId())) {
				return true;
			}
		}
		return false;
	}

	public Broadcast(Parcel in) {
		beginTime = in.readString();
		endTime = in.readString();
		channel = (Channel) in.readParcelable(Channel.class.getClassLoader());
		program = (Program) in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
		beginTimeMillis = in.readLong();
		shareUrl = in.readString();
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
			long left = 0, right = 0;
			try {
				left = DateUtilities.isoStringToLong(lhs.getBeginTime());
				right = DateUtilities.isoStringToLong(rhs.getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

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

	public static final Parcelable.Creator<Broadcast>	CREATOR	= new Parcelable.Creator<Broadcast>() {
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

	public static int getClosestBroadcastIndex(ArrayList<Broadcast> broadcastList) {

		// get the time now
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		String timeNowStr = df.format(new Date());
		long timeNow = 0;
		try {
			timeNow = DateUtilities.isoStringToLong(timeNowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int nearestIndex = 0;
		long bestDistanceFoundYet = Long.MAX_VALUE;
		for (int i = 0; i < broadcastList.size(); i++) {
			long timeBroadcast = 0;
			try {
				timeBroadcast = DateUtilities.isoStringToLong(broadcastList.get(i).getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			long d = Math.abs(timeNow - timeBroadcast);
			//TODO: This makes programs on air not show when half of the time has passed. Should it really be that way?
			//if (d < bestDistanceFoundYet && timeBroadcast < timeNow) {
			if (d < bestDistanceFoundYet) {
				nearestIndex = i;
				bestDistanceFoundYet = d;
			}
		}

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
}
