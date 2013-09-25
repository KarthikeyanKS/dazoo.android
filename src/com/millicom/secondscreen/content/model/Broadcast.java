package com.millicom.secondscreen.content.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.utilities.DateUtilities;

import android.os.Parcel;
import android.os.Parcelable;

public class Broadcast implements Parcelable{

	private String broadcastId;
	private String beginTime;
	private String endTime;
	private Channel channel;
	private Program program;
	private String channelUrl;
	
	public Broadcast(){
	}
	
	public void setBroadcastId(String broadcastId){
		this.broadcastId = broadcastId;
	}
	
	public String getBroadcastId(){
		return this.broadcastId;
	}
	
	public void setBeginTime(String beginTime){
		this.beginTime = beginTime;
	}
	
	public String getBeginTime(){
		return this.beginTime;
	}
	
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	
	public void setChannel(Channel channel){
		this.channel = channel;
	}
	
	public Channel getChannel(){
		return this.channel;
	}
	
	public void setProgram(Program program){
		this.program = program;
	}
	
	public Program getProgram(){
		return this.program;
	}
	
	public void setChannelUrl(String channelUrl){
		this.channelUrl = channelUrl;
	}
	
	public String getChannelUrl(){
		return this.channelUrl;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Broadcast) {
			Broadcast other = (Broadcast) o;
			if (getBroadcastId() != null && other.getBroadcastId() != null && getBroadcastId().equals(other.getBroadcastId())) {
				return true;
			}
		}
		return false;
	}
	
	public Broadcast(Parcel in){
		broadcastId = in.readString();
		beginTime = in.readString();
		endTime = in.readString();
		channel = (Channel)in.readParcelable(Channel.class.getClassLoader());
		program = (Program)in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(broadcastId);
		dest.writeString(beginTime);
		dest.writeString(endTime);
		dest.writeParcelable(channel, flags);
		dest.writeParcelable(program, flags);	
		dest.writeString(channelUrl);
	}

	public static class BroadcastComparatorByTime implements Comparator<Broadcast> {

		@Override
		public int compare(Broadcast lhs, Broadcast rhs) {
			long left = 0, right = 0;
			try{
				left = DateUtilities.isoStringToLong(lhs.getBeginTime());
				right = DateUtilities.isoStringToLong(rhs.getBeginTime());
			} catch (ParseException e){
				e.printStackTrace();
			}
			
			if(left > right){
				return 1;
			} else if (left > right){
				return -1;
			} else {
				String leftChannel = lhs.getChannel().getName();
				String rightChannel = rhs.getChannel().getName();
				return leftChannel.compareTo(rightChannel);
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
	    return "Id: " + broadcastId + "\n beginTime: " + beginTime + "\n endTime: " + endTime + "\n channel: " + channel + "\n program: " + program + "\n channelUrl" + channelUrl; 
	}
	
	public static ArrayList<Broadcast> getClosestBroadcasts(ArrayList<Broadcast> broadcastList, int numberOfClosest) {
		ArrayList<Broadcast> nextBroadcasts = new ArrayList<Broadcast>();

		// get the time now
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df.setTimeZone(tz);
		String timeNowStr = df.format(new Date());
		long timeNow = 0;
		try {
			timeNow = DateUtilities.isoStringToLong(timeNowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int nearestIndex = -1;
		long bestDistanceFoundYet = Long.MAX_VALUE;
		for (int i = 0; i < broadcastList.size(); i++) {
			long timeBroadcast = 0;
			try {
				timeBroadcast = DateUtilities.isoStringToLong(broadcastList.get(i).getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			long d = Math.abs(timeNow - timeBroadcast);
			if (d < bestDistanceFoundYet) {
				nearestIndex = i;
				bestDistanceFoundYet = d;
			}
		}

		// get the selection of upcoming broadcasts
		for (int j = nearestIndex; j < numberOfClosest; j++) {
			if (j < broadcastList.size()) {
				nextBroadcasts.add(broadcastList.get(j));
			}
		}
		
		return nextBroadcasts;
	}
}
