package com.mitv.storage;

import com.mitv.model.OldTVTag;
import com.mitv.model.OldTVDate;

import android.os.Parcel;
import android.os.Parcelable;

public class BroadcastKey implements Parcelable{

	OldTVDate date;
	OldTVTag tag;
	
	public BroadcastKey(){}

	// Date component
	
	public void setDate(OldTVDate date){
		this.date = date;
	}
	
	public OldTVDate getDate(){
		return this.date;
	}
	
	public String getDateName(){
		return this.date.getName();
	}
	
	public String getDateDate(){
		return this.date.getDate();
	}
	
	public String getDateId(){
		return this.date.getId();
	}
	
	// Tag component
	
	public void setTag(OldTVTag tag){
		this.tag = tag;
	}
	
	public OldTVTag getTag(){
		return this.tag;
	}
	
	public String getTagName(){
		return this.tag.getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BroadcastKey) {
			BroadcastKey other = (BroadcastKey) o;
			if (getDateDate() != null && other.getDateDate() != null && getTagName().equals(other.getTagName())) {
				return true;
			}
		}
		return false;
	}
	
	public BroadcastKey(Parcel in){
		date = (OldTVDate) in.readParcelable(OldTVDate.class.getClassLoader());
		tag = (OldTVTag) in.readParcelable(OldTVTag.class.getClassLoader());
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(date, flags);
		dest.writeParcelable(tag, flags);
	}

}
