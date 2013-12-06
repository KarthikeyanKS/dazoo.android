package com.millicom.secondscreen.content.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TvDate implements Parcelable{
	
	String id;
	String name;
	String date;
	
	public TvDate(){		
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
	
	public TvDate(Parcel in){
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
		if (o instanceof TvDate) {
			TvDate other = (TvDate) o;
			if (getDate() != null && other.getDate() != null && getDate().equals(other.getDate()) &&
					getId() != null && other.getDate() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<TvDate>	CREATOR	= new Parcelable.Creator<TvDate>() {
		public TvDate createFromParcel(Parcel in) {
			return new TvDate(in);
		}

		public TvDate[] newArray(int size) {
			return new TvDate[size];
		}
	};

}
