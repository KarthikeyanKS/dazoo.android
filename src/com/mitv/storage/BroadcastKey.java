package com.mitv.storage;

import com.mitv.model.TVTag;
import com.mitv.model.TVDate;

import android.os.Parcel;
import android.os.Parcelable;

public class BroadcastKey implements Parcelable{

	TVDate date;
	TVTag tag;
	
	public BroadcastKey(){
	}

	// Date component
	
	public void setDate(TVDate date){
		this.date = date;
	}
	
	public TVDate getDate(){
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
	
	public void setTag(TVTag tag){
		this.tag = tag;
	}
	
	public TVTag getTag(){
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
		date = (TVDate) in.readParcelable(TVDate.class.getClassLoader());
		tag = (TVTag) in.readParcelable(TVTag.class.getClassLoader());
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
