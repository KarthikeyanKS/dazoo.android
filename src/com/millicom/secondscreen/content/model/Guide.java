package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Guide implements Parcelable{
	
	private String href;
	private String id;
	private String name;
	private String logoSHref;
	private String logoMHref;
	private String logoLHref;
	private ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
	
	public Guide(){
	}

	public void setHref(String href){
		this.href = href;
	}
	
	public String getHref(){
		return this.href;
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
		href = in.readString();
		id = in.readString();
		name = in.readString();
		logoSHref = in.readString();
		logoMHref = in.readString();
		logoLHref = in.readString();
		broadcasts = (ArrayList<Broadcast>)in.readArrayList(Broadcast.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(href);
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

}
