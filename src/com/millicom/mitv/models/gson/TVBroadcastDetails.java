package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TVBroadcastDetails implements JsonDeserializer<TVBroadcastDetails> {
	
	private TVChannel channel;
	//private TVProgram program;
	
	@Override
	public TVBroadcastDetails deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		
		return null;
	}
	
	public TVBroadcastDetails() {
		
	}

	public TVChannel getChannel() {
		return channel;
	}

	public void setChannel(TVChannel channel) {
		this.channel = channel;
	}

	/*
	public TVProgram getTvProgram() {
		return tvProgram;
	}

	public void setTvProgram(TVProgram tvProgram) {
		this.tvProgram = tvProgram;
	}
	*/
	

}
