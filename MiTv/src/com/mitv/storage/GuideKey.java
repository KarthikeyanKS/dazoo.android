package com.mitv.storage;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.model.TvDate;

public class GuideKey implements Parcelable{
	
	String date;
	String channelId;
	
	public GuideKey(){
	}

	// Date component
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDate(){
		return this.date;
	}
	
	// Channel component
	
	public void setChannelId(String channelId){
		this.channelId = channelId;
	}
	
	public String getChannelId(){
		return this.channelId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GuideKey) {
			GuideKey other = (GuideKey) o;
			if (getDate() != null && other.getDate() != null 
					&& getDate().equals(other.getDate())
					&& getChannelId().equals(other.getChannelId())) {
				return true;
			}
		}
		return false;
	}
	
	public GuideKey(Parcel in){
		date = in.readString();
		channelId = in.readString();
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeString(channelId);
	}
}
