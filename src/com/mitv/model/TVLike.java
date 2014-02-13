package com.mitv.model;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class TVLike implements Parcelable{
	private String likeType;
	private MiTVLikeEntity entity;
	private String nextBroadcastChannelId; 
	private long nextBroadcastBegintimeMillis;
	
	public TVLike(){
	}
	
	public void setLikeType(String likeType){
		this.likeType = likeType;
	}
	
	public String getLikeType(){
		return this.likeType;
	}
	
	public void setEntity(MiTVLikeEntity entity){
		this.entity = entity;
	}
	
	public MiTVLikeEntity getEntity(){
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
	
	public TVLike(Parcel in){
		likeType = in.readString();
		entity = (MiTVLikeEntity) in.readParcelable(MiTVLikeEntity.class.getClassLoader());
		nextBroadcastChannelId = in.readString();
		nextBroadcastBegintimeMillis = in.readLong();
	}
	
	public static final Parcelable.Creator<TVLike>	CREATOR	= new Parcelable.Creator<TVLike>() {
		public TVLike createFromParcel(Parcel in) {
			return new TVLike(in);
		}

		public TVLike[] newArray(int size) {
			return new TVLike[size];
		}
	};
	
	public static class MiTVLikeComparatorByTitle implements Comparator<TVLike> {

		@Override
		public int compare(TVLike a, TVLike b) {
			return a.getEntity().getTitle().compareTo(b.getEntity().getTitle());
		}
	}
	
}
