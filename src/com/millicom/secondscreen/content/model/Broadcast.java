package com.millicom.secondscreen.content.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Broadcast implements Parcelable{

	private String broadcastId;
	private String beginTime;
	private String endTime;
	private Channel channel;
	private Program program;
	private String channelUrl;
	
	public Broadcast(){
	}
	
	public void setBroadcastId(String broadcastId){
		this.broadcastId = broadcastId;
	}
	
	public String getBroadcastId(){
		return this.broadcastId;
	}
	
	public void setBeginTime(String beginTime){
		this.beginTime = beginTime;
	}
	
	public String getBeginTime(){
		return this.beginTime;
	}
	
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	
	public void setChannel(Channel channel){
		this.channel = channel;
	}
	
	public Channel getChannel(){
		return this.channel;
	}
	
	public void setProgram(Program program){
		this.program = program;
	}
	
	public Program getProgram(){
		return this.program;
	}
	
	public void setChannelUrl(String channelUrl){
		this.channelUrl = channelUrl;
	}
	
	public String getChannelUrl(){
		return this.channelUrl;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Broadcast) {
			Broadcast other = (Broadcast) o;
			if (getBroadcastId() != null && other.getBroadcastId() != null && getBroadcastId().equals(other.getBroadcastId())) {
				return true;
			}
		}
		return false;
	}
	
	public Broadcast(Parcel in){
		broadcastId = in.readString();
		beginTime = in.readString();
		endTime = in.readString();
		channel = (Channel)in.readParcelable(Channel.class.getClassLoader());
		program = (Program)in.readParcelable(Program.class.getClassLoader());
		channelUrl = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(broadcastId);
		dest.writeString(beginTime);
		dest.writeString(endTime);
		dest.writeParcelable(channel, flags);
		dest.writeParcelable(program, flags);	
		dest.writeString(channelUrl);
	}

}
