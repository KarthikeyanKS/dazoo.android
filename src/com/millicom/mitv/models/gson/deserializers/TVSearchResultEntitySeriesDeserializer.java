package com.millicom.mitv.models.gson.deserializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVSearchResultEntity;
import com.mitv.Consts;

public class TVSearchResultEntitySeriesDeserializer implements JsonDeserializer<TVSearchResultEntity> {
	
	@Override
	public TVSearchResultEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		/* Get the series info */
		String seriesId = jsonObject.get(Consts.JSON_KEY_SEARCH_ENTITY_SERIES_ID).getAsString();
		String seriesName = jsonObject.get(Consts.JSON_KEY_SEARCH_ENTITY_SERIES_NAME).getAsString();
		
		/* Get the broadcasts */
		JsonElement jsonElementBroadcasts = jsonObject.get(Consts.JSON_KEY_SEARCH_ENTITY_BROADCASTS);
		TVBroadcastWithChannelInfo[] broadcastArray = new Gson().fromJson(jsonElementBroadcasts, TVBroadcastWithChannelInfo[].class);
		ArrayList<TVBroadcastWithChannelInfo> broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(Arrays.asList(broadcastArray));
		
		TVSearchResultEntity seriesEntity = new TVSearchResultEntity(seriesId, seriesName, broadcasts);
		return seriesEntity;
	}
}