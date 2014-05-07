package com.mitv.models.gson.mitvapi.competitions;



public class EventBroadcastDetailsJSON 
{
	protected String channelId;
	protected String date;
	protected String broadcastId;
	
	
	
	public EventBroadcastDetailsJSON(){}

	
	
	// TODO - Remove this
	public EventBroadcastDetailsJSON(String channelId, String date, String broadcastId)
	{
		this.channelId = channelId;
		this.date = date;
		this.broadcastId = broadcastId;
	}


	public String getChannelId() {
		return channelId;
	}



	public String getDate() {
		return date;
	}



	public String getBroadcastId() {
		return broadcastId;
	}
}
