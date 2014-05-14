package com.mitv.models.gson.mitvapi.competitions;



public class EventBroadcastDetailsJSON 
{
	protected String channelId;
	protected String beginTime;
	protected long beginTimeMillis;
	protected String endTime;
	protected String broadcastId;
	
	/*
	 * TODO We might have to change this model. It might look like:
	 * 
	 * {
			beginTime : Date,
			beginTimeMillis : Long,
			endTime : Date,
			broadcastType : "LIVE" or "RECORDED" or "RERUN" or "DELAYED" or "OTHER",
			shareUrl : String
		}
	 */
	
	public EventBroadcastDetailsJSON(){}

	
	
	public String getBeginTime() {
		return beginTime;
	}



	public long getBeginTimeMillis() {
		return beginTimeMillis;
	}



	public String getEndTime() {
		return endTime;
	}



	public String getChannelId() {
		return channelId;
	}

	

	public String getBroadcastId() {
		return broadcastId;
	}
}
