package com.mitv.models.gson.deserializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mitv.Constants;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.objects.mitvapi.TVSearchResultEntity;

public class TVSearchResultEntityProgramDeserializer implements JsonDeserializer<TVSearchResultEntity> {
	
	@Override
	public TVSearchResultEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		/* Get the program */
		TVProgram program = new Gson().fromJson(jsonElement, TVProgram.class);
		
		/* Get the broadcasts */
		JsonElement jsonElementBroadcasts = jsonObject.get(Constants.JSON_KEY_SEARCH_ENTITY_BROADCASTS);
		TVBroadcastWithChannelInfo[] broadcastArray = new Gson().fromJson(jsonElementBroadcasts, TVBroadcastWithChannelInfo[].class);
		ArrayList<TVBroadcastWithChannelInfo> broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(Arrays.asList(broadcastArray));
		
		TVSearchResultEntity programEntity = new TVSearchResultEntity(program, broadcasts);
		return programEntity;
	}
}
