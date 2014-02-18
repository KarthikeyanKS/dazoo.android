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

public class TVChannel implements JsonDeserializer<TVChannel> {

	@DeserializeManuallyAnnotation
	private TVChannelId channelId;
	private String name;
	private Logo logo;
	
	public TVChannelId getChannelId() {
		return channelId;
	}
	
	public String getName() {
		return name;
	}

	public Logo getLogo() {
		return logo;
	}
	
	@Override
    public TVChannel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {	
		
		/* Custom parsing of some variable fields */
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement channelIdJsonElement = jsonObject.get(Consts.CHANNEL_CHANNEL_ID);
		String channelIdString = channelIdJsonElement.getAsString();
		
		TVChannelId tvChannelId = new TVChannelId(channelIdString);
		
		this.channelId = tvChannelId;
		
		/* Automatic parsing of non transient variable fields, store in tmp object */
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.addDeserializationExclusionStrategy(new DeserializeManuallyAnnotationExclusionStrategy());
		Gson gson = gsonBuilder.create();
		
		TVChannel tvChannel = gson.fromJson(jsonElement, TVChannel.class);
		this.name = tvChannel.name;
		this.logo = tvChannel.logo;
		
		return this;
	}
	
	
}
