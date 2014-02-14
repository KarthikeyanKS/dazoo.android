package com.mitv.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class OldTVFeedItem implements Parcelable{
	
	private static final String TAG = "FeedItem";
	
	private String itemType;
	//itemType = BROADCAST and RECOMMENDED_BROADCAST
	private String title;
	private OldBroadcast broadcast;
	// itemType = POPULAR_BROADCASTS
	private ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();
	
	public OldTVFeedItem(){
	}
	
	public void setItemType(String itemType){
		this.itemType = itemType;
	}

	public String getItemType(){
		return this.itemType;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setBroadcast(OldBroadcast broadcast){
		this.broadcast = broadcast;
	}
	
	public OldBroadcast getBroadcast(){
		return this.broadcast;
	}
	
	public void setBroadcasts(ArrayList<OldBroadcast> broadcasts){
		this.broadcasts = broadcasts;
	}
	
	public ArrayList<OldBroadcast> getBroadcasts(){
		return this.broadcasts;
	}

	public OldTVFeedItem(Parcel in){
		itemType = in.readString();
		title = in.readString();
		broadcast = (OldBroadcast) in.readParcelable(OldBroadcast.class.getClassLoader());
		in.readTypedList(broadcasts, OldBroadcast.CREATOR);
	}
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(itemType);
		dest.writeString(title);
		dest.writeParcelable(broadcast, flags);
		dest.writeTypedList(broadcasts);	
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OldTVFeedItem) {
			OldTVFeedItem other = (OldTVFeedItem) o;
			if (getItemType() != null && other.getItemType() != null && getItemType().equals(other.getItemType()) && getTitle() != null
					&& other.getTitle() != null && (getTitle()).equals(other.getTitle())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<OldTVFeedItem>	CREATOR	= new Parcelable.Creator<OldTVFeedItem>() {
		public OldTVFeedItem createFromParcel(Parcel in) {
			return new OldTVFeedItem(in);
		}

		public OldTVFeedItem[] newArray(int size) {
			return new OldTVFeedItem[size];
		}
	};

	@Override
	public String toString() {
		return "\n itemType: " + itemType + "\n title: " + title + "\n broadcast: " + broadcast + "\n broadcasts number: " + String.valueOf(broadcasts.size());
	}
	
}
