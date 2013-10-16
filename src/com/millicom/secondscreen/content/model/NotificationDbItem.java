package com.millicom.secondscreen.content.model;

public class NotificationDbItem {
	private int notificationId;
	private String broadcastUrl;
	private String channelId;
	private int beginTimeInMillis;
	
	public void setNotificationId(int id){
		this.notificationId = id;
	}

	public int getNotificationId(){
		return notificationId;
	}
	
	public void setBroadcstUrl(String broadcastUrl){
		this.broadcastUrl = broadcastUrl;
	}
	
	public String getBroadcastUrl(){
		return this.broadcastUrl;
	}
	
	public void setChannelId(String channelId){
		this.channelId = channelId;
	}
	
	public String getChannelId(){
		return this.channelId;
	}
	
	public void setTimeInMillis(int beginTimeInMillis){
		this.beginTimeInMillis = beginTimeInMillis;
	}
}
