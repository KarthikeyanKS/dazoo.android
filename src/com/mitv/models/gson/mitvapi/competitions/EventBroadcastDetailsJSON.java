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
	
	public EventBroadcastDetailsJSON() {}
	
	public EventBroadcastDetailsJSON(String channelid, String beginTime, long beginTimeMillis, String broadcastId, String endTime) {
//		this.channelId = "co_8a59310b-f558-48e3-8dcf-d59afc9e5ac3";
//		this.beginTime = "2014-06-13T04:00:00Z";
//		this.beginTimeMillis = 1402632000000l;
//		this.broadcastId = "co_98693b86-0dbc-45e9-af4b-9547e620fb08"; //programid
//		this.endTime = "2014-06-13T06:00:00Z";
		
		this.beginTime = beginTime;
		this.channelId = channelid;
		this.beginTimeMillis = beginTimeMillis;
		this.broadcastId = broadcastId;
		this.endTime = endTime;
	}

	
	
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
