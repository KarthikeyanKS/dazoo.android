package com.millicom.secondscreen.content.model;

import org.json.JSONObject;

import com.millicom.secondscreen.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class SportType implements Parcelable{
	
	private String name;
	private String sportTypeId;

	public SportType(){
	}
	
	public SportType(JSONObject jsonSportType) {
		this.setName(jsonSportType.optString(Consts.DAZOO_SPORTTYPE_NAME));
		this.setSportTypeId(jsonSportType.optString(Consts.DAZOO_SPORTTYPE_SPORTTYPEID));
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setSportTypeId(String sportTypeId){
		this.sportTypeId = sportTypeId;
	}
	
	public String getSportTypeId(){
		return this.sportTypeId;
	}
	
	public SportType(Parcel in){
		name = in.readString();
		sportTypeId = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(sportTypeId);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SportType) {
			SportType other = (SportType) o;
			if (getSportTypeId() != null && other.getSportTypeId() != null && getSportTypeId().equals(other.getSportTypeId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<SportType>	CREATOR	= new Parcelable.Creator<SportType>() {
		public SportType createFromParcel(Parcel in) {
			return new SportType(in);
		}

		public SportType[] newArray(int size) {
			return new SportType[size];
		}
	};
	
	
}