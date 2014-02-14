package com.mitv.model;

import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class OldSeason implements Parcelable  {

	private String number;
	
	public OldSeason(){	
	}
	
	public OldSeason(JSONObject jsonSeason) {
		this.setNumber(jsonSeason.optString(Consts.SEASON_NUMBER));
	}
	
	public void setNumber(String number){
		this.number = number;
	}
	
	public String getNumber(){
		return number;
	}
		
	public OldSeason(Parcel in){
		number = in.readString();
	}
		
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(number);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OldSeason) {
			OldSeason other = (OldSeason) o;
			if (getNumber() != null && other.getNumber() != null && getNumber().equals(other.getNumber())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldSeason>	CREATOR	= new Parcelable.Creator<OldSeason>() {
		public OldSeason createFromParcel(Parcel in) {
			return new OldSeason(in);
		}

		public OldSeason[] newArray(int size) {
			return new OldSeason[size];
		}
	};
}
