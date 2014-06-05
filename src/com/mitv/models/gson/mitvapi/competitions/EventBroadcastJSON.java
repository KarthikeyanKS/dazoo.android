
package com.mitv.models.gson.mitvapi.competitions;



public class EventBroadcastJSON 
{
	protected String eventBroadcastId;
	protected String programId;
	protected String beginTime;
	protected long beginTimeMillis;
	protected String endTime;
	protected String channelId;

    
    
    public EventBroadcastJSON()
    {}



	public String getEventBroadcastId() {
		return eventBroadcastId;
	}



	public String getProgramId() {
		return programId;
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
}
