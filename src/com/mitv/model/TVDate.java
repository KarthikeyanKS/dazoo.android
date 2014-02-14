package com.mitv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TVDate implements Parcelable{
	
	String id;
	String name;
	String date;
	
	public TVDate(){		
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
	
	public TVDate(Parcel in){
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
		if (o instanceof TVDate) {
			TVDate other = (TVDate) o;
			if (getDate() != null && other.getDate() != null && getDate().equals(other.getDate()) &&
					getId() != null && other.getDate() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<TVDate>	CREATOR	= new Parcelable.Creator<TVDate>() {
		public TVDate createFromParcel(Parcel in) {
			return new TVDate(in);
		}

		public TVDate[] newArray(int size) {
			return new TVDate[size];
		}
	};

}
