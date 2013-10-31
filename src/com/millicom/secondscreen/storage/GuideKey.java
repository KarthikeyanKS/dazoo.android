package com.millicom.secondscreen.storage;

import android.os.Parcel;
import android.os.Parcelable;

import com.millicom.secondscreen.content.model.TvDate;

public class GuideKey implements Parcelable{
	
	TvDate date;
	String channelId;
	
	public GuideKey(){
	}

	// Date component
	
	public void setDate(TvDate date){
		this.date = date;
	}
	
	public TvDate getDate(){
		return this.date;
	}
	
	public String getDateName(){
		return this.date.getName();
	}
	
	public String getDateDate(){
		return this.date.getDate();
	}
	
	public String getDateAlias(){
		return this.date.getAlias();
	}
	
	public String getDateId(){
		return this.date.getId();
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
			if (getDateDate() != null && other.getDateDate() != null && getChannelId().equals(other.getChannelId())) {
				return true;
			}
		}
		return false;
	}
	
	public GuideKey(Parcel in){
		date = (TvDate) in.readParcelable(TvDate.class.getClassLoader());
		channelId = in.readString();
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(date, flags);
		dest.writeString(channelId);
	}
}
