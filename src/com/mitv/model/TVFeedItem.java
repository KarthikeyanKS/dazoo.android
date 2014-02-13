package com.mitv.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class TVFeedItem implements Parcelable{
	
	private static final String TAG = "FeedItem";
	
	private String itemType;
	//itemType = BROADCAST and RECOMMENDED_BROADCAST
	private String title;
	private Broadcast broadcast;
	// itemType = POPULAR_BROADCASTS
	private ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
	
	public TVFeedItem(){
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
	
	public void setBroadcast(Broadcast broadcast){
		this.broadcast = broadcast;
	}
	
	public Broadcast getBroadcast(){
		return this.broadcast;
	}
	
	public void setBroadcasts(ArrayList<Broadcast> broadcasts){
		this.broadcasts = broadcasts;
	}
	
	public ArrayList<Broadcast> getBroadcasts(){
		return this.broadcasts;
	}

	public TVFeedItem(Parcel in){
		itemType = in.readString();
		title = in.readString();
		broadcast = (Broadcast) in.readParcelable(Broadcast.class.getClassLoader());
		in.readTypedList(broadcasts, Broadcast.CREATOR);
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
		if (o instanceof TVFeedItem) {
			TVFeedItem other = (TVFeedItem) o;
			if (getItemType() != null && other.getItemType() != null && getItemType().equals(other.getItemType()) && getTitle() != null
					&& other.getTitle() != null && (getTitle()).equals(other.getTitle())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<TVFeedItem>	CREATOR	= new Parcelable.Creator<TVFeedItem>() {
		public TVFeedItem createFromParcel(Parcel in) {
			return new TVFeedItem(in);
		}

		public TVFeedItem[] newArray(int size) {
			return new TVFeedItem[size];
		}
	};

	@Override
	public String toString() {
		return "\n itemType: " + itemType + "\n title: " + title + "\n broadcast: " + broadcast + "\n broadcasts number: " + String.valueOf(broadcasts.size());
	}
	
}
