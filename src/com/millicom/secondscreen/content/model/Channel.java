package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Channel implements Parcelable {

	private String id = "";
	private String channelId = "";
	private String name = "";
	private String logoSUrl = "";
	private String logoMUrl = "";
	private String logoLUrl = "";
	private String channelPageUrl = "";
	
	public Channel(){		
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setChannelId(String channelId){
		this.channelId = channelId;
	}
	
	public String getChannelId(){
		return this.channelId;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setLogoSUrl(String logoSUrl){
		this.logoSUrl = logoSUrl;
	}
	
	public String getLogoSUrl(){
		return this.logoSUrl;
	}
	
	public void setLogoMUrl(String logoMUrl){
		this.logoMUrl = logoMUrl;
	}
	
	public String getLogoMUrl(){
		return this.logoMUrl;
	}
	
	public void setLogoLUrl(String logoLUrl){
		this.logoLUrl = logoLUrl;
	}
	
	public String getLogoLUrl(){
		return this.logoLUrl;
	}
	
	public void setChannelPageUrl(String channelPageUrl){
		this.channelId = channelId;
	}
	
	public String getChannelPageUrl(){
		return this.channelId;
	}
	
	public Channel(Parcel in){
		channelId = in.readString();
		name = in.readString();
		logoSUrl = in.readString();
		logoMUrl = in.readString();
		logoLUrl = in.readString();
		channelPageUrl = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(channelId);
		dest.writeString(name);
		dest.writeString(logoSUrl);
		dest.writeString(logoMUrl);	
		dest.writeString(logoLUrl);
		dest.writeString(channelPageUrl);
		}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Channel) {
			Channel other = (Channel) o;
			if (getChannelId() != null && other.getChannelId() != null && getChannelId().equals(other.getChannelId())) {
				return true;
			}
		}
		return false;
	}
	
	public static final Parcelable.Creator<Channel>	CREATOR	= new Parcelable.Creator<Channel>() {
		public Channel createFromParcel(Parcel in) {
			return new Channel(in);
		}

		public Channel[] newArray(int size) {
			return new Channel[size];
		}
	};

	@Override
	public String toString() {
	    return "Id: " + channelId + "\n name: " + name + "\n logoSUrl: " + logoSUrl + "\n logoMUrl: " + logoMUrl + "\n logoLUrl: " + logoLUrl + "\n channelPageUrl" + channelPageUrl; 
	}
}
