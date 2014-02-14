package com.mitv.model;

import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class OldSportType implements Parcelable{
	
	private String name;
	private String sportTypeId;

	public OldSportType(){
	}
	
	public OldSportType(JSONObject jsonSportType) {
		this.setName(jsonSportType.optString(Consts.SPORTTYPE_NAME));
		this.setSportTypeId(jsonSportType.optString(Consts.SPORTTYPE_SPORTTYPEID));
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
	
	public OldSportType(Parcel in){
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
		if (o instanceof OldSportType) {
			OldSportType other = (OldSportType) o;
			if (getSportTypeId() != null && other.getSportTypeId() != null && getSportTypeId().equals(other.getSportTypeId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldSportType>	CREATOR	= new Parcelable.Creator<OldSportType>() {
		public OldSportType createFromParcel(Parcel in) {
			return new OldSportType(in);
		}

		public OldSportType[] newArray(int size) {
			return new OldSportType[size];
		}
	};
	
	
}