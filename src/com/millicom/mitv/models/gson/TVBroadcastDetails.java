package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.millicom.mitv.interfaces.DeserializeManuallyAnnotation;
import com.mitv.Consts;

public class TVBroadcastDetails implements JsonDeserializer<TVBroadcastDetails> {
	
	@DeserializeManuallyAnnotation
	private TVChannel channel;
	private TVProgram program;
	
	@Override
    public TVBroadcastDetails deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {	
		/* Custom deserialization of channel object */	
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement channelJsonElement = jsonObject.get(Consts.BROADCAST_CHANNEL);
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.addDeserializationExclusionStrategy(new DeserializeManuallyAnnotationExclusionStrategy());
		Gson gson = gsonBuilder.create();
		
		this.channel = gson.fromJson(channelJsonElement, TVChannel.class);
		

		TVBroadcastDetails tvBroadcastDetails = gson.fromJson(jsonElement, TVBroadcastDetails.class);
		
		this.program = tvBroadcastDetails.program;
		
		return tvBroadcastDetails;
	}
	
	public TVBroadcastDetails() {
		
	}

	public TVChannel getChannel() {
		return channel;
	}

	public void setChannel(TVChannel channel) {
		this.channel = channel;
	}

	
	public TVProgram getTvProgram() {
		return program;
	}

	public void setTvProgram(TVProgram program) {
		this.program = program;
	}
	
	

}
