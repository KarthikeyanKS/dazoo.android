package com.mitv.model;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

public class OldTVLike implements Parcelable{
	private String likeType;
	private OldMiTVLikeEntity entity;
	private String nextBroadcastChannelId; 
	private long nextBroadcastBegintimeMillis;
	
	public OldTVLike(){
	}
	
	public void setLikeType(String likeType){
		this.likeType = likeType;
	}
	
	public String getLikeType(){
		return this.likeType;
	}
	
	public void setEntity(OldMiTVLikeEntity entity){
		this.entity = entity;
	}
	
	public OldMiTVLikeEntity getEntity(){
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
	
	public OldTVLike(Parcel in){
		likeType = in.readString();
		entity = (OldMiTVLikeEntity) in.readParcelable(OldMiTVLikeEntity.class.getClassLoader());
		nextBroadcastChannelId = in.readString();
		nextBroadcastBegintimeMillis = in.readLong();
	}
	
	public static final Parcelable.Creator<OldTVLike>	CREATOR	= new Parcelable.Creator<OldTVLike>() {
		public OldTVLike createFromParcel(Parcel in) {
			return new OldTVLike(in);
		}

		public OldTVLike[] newArray(int size) {
			return new OldTVLike[size];
		}
	};
	
	public static class MiTVLikeComparatorByTitle implements Comparator<OldTVLike> {

		@Override
		public int compare(OldTVLike a, OldTVLike b) {
			return a.getEntity().getTitle().compareTo(b.getEntity().getTitle());
		}
	}
	
}
