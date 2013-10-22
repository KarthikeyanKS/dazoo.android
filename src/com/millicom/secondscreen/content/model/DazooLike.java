package com.millicom.secondscreen.content.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DazooLike implements Parcelable{
	private String entityId;
	private String entityType;
	private DazooLikeEntity entity;
	
	public DazooLike(){
	}

	public void setEntityId(String entityId){
		this.entityId = entityId;
	}
	
	public String getEntityId(){
		return this.entityId;
	}
	
	public void setEntityType(String entityType){
		this.entityType = entityType;
	}
	
	public String getEntityType(){
		return this.entityType;
	}
	
	public void setEntity(DazooLikeEntity entity){
		this.entity = entity;
	}
	
	@Override
	public int describeContents() { 
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(entityId);
		dest.writeString(entityType);
		dest.writeParcelable(entity, flags);
	}
	
	public DazooLike(Parcel in){
		entityId = in.readString();
		entityType = in.readString();
		entity = (DazooLikeEntity) in.readParcelable(DazooLikeEntity.class.getClassLoader());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DazooLike) {
			DazooLike other = (DazooLike) o;
			if (getEntityId() != null && other.getEntityId() != null && getEntityId().equals(other.getEntityId())) {
				return true;
			}
		}
		return false;
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
