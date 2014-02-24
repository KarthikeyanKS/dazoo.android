package com.mitv.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;

public class OldSeries implements Parcelable{
	
	private String name;
	private String seriesId;

	public OldSeries(){
	}
	
	public OldSeries(JSONObject jsonObject) {
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
	
	public OldSeries(Parcel in){
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
		if (o instanceof OldSeries) {
			OldSeries other = (OldSeries) o;
			if (getName() != null && other.getName() != null && getName().equals(other.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldSeries>	CREATOR	= new Parcelable.Creator<OldSeries>() {
		public OldSeries createFromParcel(Parcel in) {
			return new OldSeries(in);
		}

		public OldSeries[] newArray(int size) {
			return new OldSeries[size];
		}
	};
	
	
}
