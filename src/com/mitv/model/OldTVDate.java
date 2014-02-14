package com.mitv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OldTVDate implements Parcelable{
	
	String id;
	String name;
	String date;
	
	public OldTVDate(){		
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public OldTVDate(Parcel in){
		id = in.readString();
		name = in.readString();
		date = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(date);	
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OldTVDate) {
			OldTVDate other = (OldTVDate) o;
			if (getDate() != null && other.getDate() != null && getDate().equals(other.getDate()) &&
					getId() != null && other.getDate() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldTVDate>	CREATOR	= new Parcelable.Creator<OldTVDate>() {
		public OldTVDate createFromParcel(Parcel in) {
			return new OldTVDate(in);
		}

		public OldTVDate[] newArray(int size) {
			return new OldTVDate[size];
		}
	};

}
