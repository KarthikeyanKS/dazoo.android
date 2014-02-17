package com.mitv.model;

import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class Series implements Parcelable{
	
	private String name;
	private String seriesId;

	public Series(){
	}
	
	public Series(JSONObject jsonObject) {
		this.setName(jsonObject.optString(Consts.SERIES_NAME));
		this.setSeriesId(jsonObject.optString(Consts.SERIES_SERIES_ID));
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setSeriesId(String seriesId){
		this.seriesId = seriesId;
	}
	
	public String getSeriesId(){
		return this.seriesId;
	}
	
	public Series(Parcel in){
		name = in.readString();
		seriesId = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(seriesId);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Series) {
			Series other = (Series) o;
			if (getName() != null && other.getName() != null && getName().equals(other.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Series>	CREATOR	= new Parcelable.Creator<Series>() {
		public Series createFromParcel(Parcel in) {
			return new Series(in);
		}

		public Series[] newArray(int size) {
			return new Series[size];
		}
	};
	
	
}
