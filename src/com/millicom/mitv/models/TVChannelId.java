package com.millicom.mitv.models;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TVChannelId implements Serializable, JsonDeserializer<TVChannelId>{

	private static final long serialVersionUID = 4700191329599444606L;
	private String tvChannelId;

	/* Important to have empty constructor for GSON deserialization!!! */
	public TVChannelId() {}
	
	@Override
	public TVChannelId deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		return null;
	}

	public String getTvChannelId() {
		return tvChannelId;
	}

	public void setTvChannelId(String tvChannelId) {
		this.tvChannelId = tvChannelId;
	}
	
}
