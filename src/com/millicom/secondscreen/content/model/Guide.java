package com.millicom.secondscreen.content.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.utilities.DateUtilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Guide implements Parcelable{

	private String id;
	private String name;
	private String logoSHref;
	private String logoMHref;
	private String logoLHref;
	private ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
	
	/* Used for caching broadcast indexes */
	private HashMap<Long, Integer> broadcastIndexCache = new HashMap<Long, Integer>();
	
	public Guide(){
	}

	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setLogoSHref(String logoSHref){
		this.logoSHref = logoSHref;
	}
	
	public String getLogoSHref(){
		return this.logoSHref;
	}
	
	public void setLogoMHref(String logoMHref){
		this.logoMHref = logoMHref;
	}
	
	public String getLogoMHref(){
		return this.logoMHref;
	}
	
	public void setLogoLHref(String logoLHref){
		this.logoLHref = logoLHref;
	}
	
	public String getLogoLHref(){
		return this.logoLHref;
	}
	
	public void setBroadcasts(ArrayList<Broadcast> broadcasts){
		this.broadcasts = broadcasts;
	}
	
	public ArrayList<Broadcast> getBroadcasts(){
		return this.broadcasts;
	}
	
	public Guide(Parcel in){
		id = in.readString();
		name = in.readString();
		logoSHref = in.readString();
		logoMHref = in.readString();
		logoLHref = in.readString();
		in.readTypedList(broadcasts, Broadcast.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(logoSHref);
		dest.writeString(logoMHref);
		dest.writeString(logoLHref);
		dest.writeTypedList(broadcasts);	
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Guide) {
			Guide other = (Guide) o;
			if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Guide>	CREATOR	= new Parcelable.Creator<Guide>() {
		public Guide createFromParcel(Parcel in) {
			return new Guide(in);
		}

		public Guide[] newArray(int size) {
			return new Guide[size];
		}
	};
		
	public int getClosestBroadcastIndexFromTime(ArrayList<Broadcast> broadcastList, int hour, TvDate date) {
		int nearestIndex = 0;

		long timeNow = DateUtilities.timeAsLongFromTvDateAndHour(date, hour);
	
		nearestIndex = getBroadcastIndex(broadcastList, timeNow);
		
		return nearestIndex;
	}
	
	public int getClosestBroadcastIndex(ArrayList<Broadcast> broadcastList) {
		int nearestIndex = 0;
		
		// get the time now
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();
		
		nearestIndex = getBroadcastIndex(broadcastList, timeNow);

		return nearestIndex;
	}
	
	
	
	public int getBroadcastIndex(ArrayList<Broadcast> broadcastList, long timeNow) {
		int nearestIndex = 0;
		Integer nearestIndexObj = null;
		Long timeLongObject = Long.valueOf(timeNow);
		if(broadcastIndexCache.containsKey(timeLongObject)) {
			nearestIndexObj = broadcastIndexCache.get(timeLongObject);
			if(nearestIndexObj != null) {
				/* Cache hit! */
				nearestIndex = nearestIndexObj.intValue();
			}
		}
		
		if(nearestIndexObj == null) {
			/* Cache miss */
			nearestIndex = Broadcast.getClosestBroadcastIndexUsingTime(broadcastList, timeNow);
			
			broadcastIndexCache.put(timeLongObject, Integer.valueOf(nearestIndex));
		}
		
		return nearestIndex;
	}
}
