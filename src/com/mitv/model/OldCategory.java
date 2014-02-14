package com.mitv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OldCategory implements Parcelable{

	private String categoryId;
	private String name;
	private String alias;
	
	public OldCategory(){
		
	}
	
	public void setCategoryId(String categoryId){
		this.categoryId = categoryId;
	}
	
	public String getCategoryId(){
		return this.categoryId;
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
	
	public OldCategory(Parcel in){
		categoryId = in.readString();
		name = in.readString();
		alias = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(categoryId);
		dest.writeString(name);
		dest.writeString(alias);		
	}

}
