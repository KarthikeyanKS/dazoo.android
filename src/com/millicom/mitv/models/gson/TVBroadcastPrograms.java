package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TVBroadcastPrograms implements Serializable, JsonDeserializer<TVBroadcastDetails> {

	private static final long serialVersionUID = 272717963623445349L;
	
	TVChannel channel;
	Long beginTimeMillis;
	Date beginTime;
	Date endTime;
	String broadcastType;
	String shareUrl;
	
	public TVBroadcastPrograms() {
	}

	@Override
	public TVBroadcastDetails deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public TVChannel getChannel() {
		return channel;
	}

	public void setChannel(TVChannel channel) {
		this.channel = channel;
	}

	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}

	public void setBeginTimeMillis(Long beginTimeMillis) {
		this.beginTimeMillis = beginTimeMillis;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getBroadcastType() {
		return broadcastType;
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

}
