package com.mitv.model;

import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class Season implements Parcelable  {

	private String number;
	
	public Season(){	
	}
	
	public Season(JSONObject jsonSeason) {
		this.setNumber(jsonSeason.optString(Consts.DAZOO_SEASON_NUMBER));
	}
	
	public void setNumber(String number){
		this.number = number;
	}
	
	public String getNumber(){
		return number;
	}
		
	public Season(Parcel in){
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
		if (o instanceof Season) {
			Season other = (Season) o;
			if (getNumber() != null && other.getNumber() != null && getNumber().equals(other.getNumber())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Season>	CREATOR	= new Parcelable.Creator<Season>() {
		public Season createFromParcel(Parcel in) {
			return new Season(in);
		}

		public Season[] newArray(int size) {
			return new Season[size];
		}
	};
}
