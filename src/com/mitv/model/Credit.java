package com.mitv.model;

import org.json.JSONObject;

import com.mitv.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class Credit implements Parcelable{
	
	private String name;
	private String type;
	
	public Credit() {}
	
	public Credit(JSONObject jsonObject) {
		this.setName(jsonObject.optString(Consts.CREDIT_NAME));
		this.setType(jsonObject.optString(Consts.CREDIT_TYPE));
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(type);
	}
	
	public Credit(Parcel in){
		name = in.readString();
		type = in.readString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Credit) {
			Credit other = (Credit) o;
			if (getName() != null && other.getName() != null && getName().equals(other.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Credit>	CREATOR	= new Parcelable.Creator<Credit>() {
		public Credit createFromParcel(Parcel in) {
			return new Credit(in);
		}

		public Credit[] newArray(int size) {
			return new Credit[size];
		}
	};
	
}
