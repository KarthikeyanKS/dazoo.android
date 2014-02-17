package com.mitv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable  {

	private String id;
	private String name;
	
	public Tag(){	
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
	
	public Tag(Parcel in){
		id = in.readString();
		name = in.readString();
	}
		
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Tag) {
			Tag other = (Tag) o;
			if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Tag>	CREATOR	= new Parcelable.Creator<Tag>() {
		public Tag createFromParcel(Parcel in) {
			return new Tag(in);
		}

		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};

}