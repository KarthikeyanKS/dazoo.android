package com.mitv.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import com.mitv.utilities.DateUtilities;

import android.os.Parcel;
import android.os.Parcelable;

public class OldChannelHour implements Parcelable{

	String hour;
	ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();
	OldTVChannel channel;
	
	public OldChannelHour(){
	}
	
	public void setHour(String hour){
		this.hour = hour;
	}
	
	public String getHour(){
		return this.hour;
	}
	
	public void setBroadcasts(ArrayList<OldBroadcast> broadcasts){
		this.broadcasts = broadcasts;
	}
	
	public ArrayList<OldBroadcast> getBroadcasts(){
		return this.broadcasts;
	}
	
	public void setChannel(OldTVChannel channel){
		this.channel = channel;
	}
	
	public OldTVChannel getChannel(){
		return this.channel;
	}
	
	public OldChannelHour(Parcel in){
		hour = in.readString();
		broadcasts = (ArrayList<OldBroadcast>)in.readArrayList(OldBroadcast.class.getClassLoader());
		channel = (OldTVChannel)in.readParcelable(OldTVChannel.class.getClassLoader());
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
	
	
	public static final Parcelable.Creator<OldChannelHour>	CREATOR	= new Parcelable.Creator<OldChannelHour>() {
		public OldChannelHour createFromParcel(Parcel in) {
			return new OldChannelHour(in);
		}

		public OldChannelHour[] newArray(int size) {
			return new OldChannelHour[size];
		}
	};
}
