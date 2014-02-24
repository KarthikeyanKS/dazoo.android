package com.mitv.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitv.Consts;

public class OldCredit implements Parcelable{
	
	private String name;
	private String type;
	
	public OldCredit() {}
	
	public OldCredit(JSONObject jsonObject) {
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
	
	public OldCredit(Parcel in){
		name = in.readString();
		type = in.readString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OldCredit) {
			OldCredit other = (OldCredit) o;
			if (getName() != null && other.getName() != null && getName().equals(other.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldCredit>	CREATOR	= new Parcelable.Creator<OldCredit>() {
		public OldCredit createFromParcel(Parcel in) {
			return new OldCredit(in);
		}

		public OldCredit[] newArray(int size) {
			return new OldCredit[size];
		}
	};
	
}
