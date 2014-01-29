package com.mitv.model;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class DazooLike implements Parcelable{
	private String likeType;
	private DazooLikeEntity entity;
	private String nextBroadcastChannelId; 
	private long nextBroadcastBegintimeMillis;
	
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
	
	public void setNextBroadcastChannelId(String channelId) {
		this.nextBroadcastChannelId = channelId;
	}
	
	public String getNextBroadcastChannelId(){
		return this.nextBroadcastChannelId;
	}
	
	public void setNextBroadcastBegintimeMillis(long begintimeMillis) {
		this.nextBroadcastBegintimeMillis = begintimeMillis;
	}
	
	public long getNextBroadcastBegintimeMillis(){
		return this.nextBroadcastBegintimeMillis;
	}
	
	
	@Override
	public int describeContents() { 
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(likeType);
		dest.writeParcelable(entity, flags);
		dest.writeString(nextBroadcastChannelId);
		dest.writeLong(nextBroadcastBegintimeMillis);
	}
	
	public DazooLike(Parcel in){
		likeType = in.readString();
		entity = (DazooLikeEntity) in.readParcelable(DazooLikeEntity.class.getClassLoader());
		nextBroadcastChannelId = in.readString();
		nextBroadcastBegintimeMillis = in.readLong();
	}
	
	public static final Parcelable.Creator<DazooLike>	CREATOR	= new Parcelable.Creator<DazooLike>() {
		public DazooLike createFromParcel(Parcel in) {
			return new DazooLike(in);
		}

		public DazooLike[] newArray(int size) {
			return new DazooLike[size];
		}
	};
	
	public static class DazooLikeComparatorByTitle implements Comparator<DazooLike> {

		@Override
		public int compare(DazooLike a, DazooLike b) {
			return a.getEntity().getTitle().compareTo(b.getEntity().getTitle());
		}
	}
	
}
