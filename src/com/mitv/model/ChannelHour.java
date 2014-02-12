package com.mitv.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import com.mitv.utilities.DateUtilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelHour implements Parcelable{

	String hour;
	ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
	TVChannel channel;
	
	public ChannelHour(){
	}
	
	public void setHour(String hour){
		this.hour = hour;
	}
	
	public String getHour(){
		return this.hour;
	}
	
	public void setBroadcasts(ArrayList<Broadcast> broadcasts){
		this.broadcasts = broadcasts;
	}
	
	public ArrayList<Broadcast> getBroadcasts(){
		return this.broadcasts;
	}
	
	public void setChannel(TVChannel channel){
		this.channel = channel;
	}
	
	public TVChannel getChannel(){
		return this.channel;
	}
	
	public ChannelHour(Parcel in){
		hour = in.readString();
		broadcasts = (ArrayList<Broadcast>)in.readArrayList(Broadcast.class.getClassLoader());
		channel = (TVChannel)in.readParcelable(TVChannel.class.getClassLoader());
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(hour);
		dest.writeTypedList(broadcasts);	
		dest.writeParcelable(channel, flags);
	}
	
	
	public static final Parcelable.Creator<ChannelHour>	CREATOR	= new Parcelable.Creator<ChannelHour>() {
		public ChannelHour createFromParcel(Parcel in) {
			return new ChannelHour(in);
		}

		public ChannelHour[] newArray(int size) {
			return new ChannelHour[size];
		}
	};
}
