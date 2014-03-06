
package com.mitv.models.gson.deserializers;



import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mitv.models.TVChannel;
import com.mitv.models.TVSearchResultEntity;



public class TVSearchResultEntityChannelDeserializer implements JsonDeserializer<TVSearchResultEntity> 
{	
	@Override
	public TVSearchResultEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		TVChannel channel = new Gson().fromJson(jsonElement, TVChannel.class);
		TVSearchResultEntity channelEntity = new TVSearchResultEntity(channel);
		return channelEntity;
	}
}
