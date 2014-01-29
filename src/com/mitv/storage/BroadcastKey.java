package com.mitv.storage;

import com.mitv.model.Tag;
import com.mitv.model.TvDate;

import android.os.Parcel;
import android.os.Parcelable;

public class BroadcastKey implements Parcelable{

	TvDate date;
	Tag tag;
	
	public BroadcastKey(){
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
	
	public String getDateId(){
		return this.date.getId();
	}
	
	// Tag component
	
	public void setTag(Tag tag){
		this.tag = tag;
	}
	
	public Tag getTag(){
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
		date = (TvDate) in.readParcelable(TvDate.class.getClassLoader());
		tag = (Tag) in.readParcelable(Tag.class.getClassLoader());
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
