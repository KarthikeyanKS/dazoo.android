package com.mitv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgramType implements Parcelable  {

	private String id;
	private String name;
	private String alias;
	
	public ProgramType(){
		
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setAlias(String alias){
		this.alias = alias;
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public ProgramType(Parcel in){
		id = in.readString();
		name = in.readString();
		alias = in.readString();
	}
		
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(alias);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ProgramType) {
			ProgramType other = (ProgramType) o;
			if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<ProgramType>	CREATOR	= new Parcelable.Creator<ProgramType>() {
		public ProgramType createFromParcel(Parcel in) {
			return new ProgramType(in);
		}

		public ProgramType[] newArray(int size) {
			return new ProgramType[size];
		}
	};

}
