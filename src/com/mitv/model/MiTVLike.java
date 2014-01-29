package com.mitv.model;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class MiTVLike implements Parcelable{
	private String likeType;
	private MiTVLikeEntity entity;
	private String nextBroadcastChannelId; 
	private long nextBroadcastBegintimeMillis;
	
	public MiTVLike(){
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
	
	public MiTVLike(Parcel in){
		likeType = in.readString();
		entity = (MiTVLikeEntity) in.readParcelable(MiTVLikeEntity.class.getClassLoader());
		nextBroadcastChannelId = in.readString();
		nextBroadcastBegintimeMillis = in.readLong();
	}
	
	public static final Parcelable.Creator<MiTVLike>	CREATOR	= new Parcelable.Creator<MiTVLike>() {
		public MiTVLike createFromParcel(Parcel in) {
			return new MiTVLike(in);
		}

		public MiTVLike[] newArray(int size) {
			return new MiTVLike[size];
		}
	};
	
	public static class MiTVLikeComparatorByTitle implements Comparator<MiTVLike> {

		@Override
		public int compare(MiTVLike a, MiTVLike b) {
			return a.getEntity().getTitle().compareTo(b.getEntity().getTitle());
		}
	}
	
}
