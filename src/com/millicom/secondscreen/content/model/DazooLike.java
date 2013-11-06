package com.millicom.secondscreen.content.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DazooLike implements Parcelable{
	private String likeType;
	private DazooLikeEntity entity;
	
	public DazooLike(){
	}
	
	public void setLikeType(String likeType){
		this.likeType = likeType;
	}
	
	public String getLikeType(){
		return this.likeType;
	}
	
	public void setEntity(DazooLikeEntity entity){
		this.entity = entity;
	}
	
	public DazooLikeEntity getEntity(){
		return this.entity;
	}
	
	@Override
	public int describeContents() { 
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(likeType);
		dest.writeParcelable(entity, flags);
	}
	
	public DazooLike(Parcel in){
		likeType = in.readString();
		entity = (DazooLikeEntity) in.readParcelable(DazooLikeEntity.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<DazooLike>	CREATOR	= new Parcelable.Creator<DazooLike>() {
		public DazooLike createFromParcel(Parcel in) {
			return new DazooLike(in);
		}

		public DazooLike[] newArray(int size) {
			return new DazooLike[size];
		}
	};
	
}
