
package com.mitv.models.gson.mitvapi;



import java.lang.reflect.Type;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.mitv.Constants;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.deserializers.TVSearchResultEntityChannelDeserializer;
import com.mitv.models.gson.deserializers.TVSearchResultEntityProgramDeserializer;
import com.mitv.models.gson.deserializers.TVSearchResultEntitySeriesDeserializer;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.objects.mitvapi.TVSearchResult;
import com.mitv.models.objects.mitvapi.TVSearchResultEntity;



public class TVSearchResultJSON 
	implements GSONDataFieldValidation, JsonDeserializer<TVSearchResult> 
{
	private static final String TAG = TVSearchResultJSON.class.getName();
	
	
	@Expose
	private String displayText;

	@Expose
	private Float searchScore;

	@Expose
	private String entityType;

	@Expose(deserialize = false)
	protected TVSearchResultEntity entity;

	
	
	public TVSearchResultJSON(){}

	
	
	public ContentTypeEnum getEntityType() 
	{
		return ContentTypeEnum.getContentTypeEnumFromStringRepresentation(entityType);
	}

	
	
	public String getDisplayText() 
	{
		if(displayText == null)
		{
			displayText = "";
			
			Log.w(TAG, "displayText is null");
		}
		
		return displayText;
	}

	
	
	public Float getSearchScore()
	{
		if(searchScore == null)
		{
			searchScore = Float.valueOf(0);
			
			Log.w(TAG, "searchScore is null");
		}
		
		return searchScore;
	}

	
	
	public TVSearchResultEntity getEntity() 
	{
		return entity;
	}
	
	
	
	@Override
	public TVSearchResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) 
			throws JsonParseException 
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		/* Set exposed fields */
		GsonBuilder gsonBuilderSelf = new GsonBuilder();
		
		gsonBuilderSelf.excludeFieldsWithoutExposeAnnotation();
		
		Gson gsonSelf = gsonBuilderSelf.create();

		TVSearchResult searchResult = gsonSelf.fromJson(jsonElement, TVSearchResult.class);
		
		ContentTypeEnum entityTypeEnum = searchResult.getEntityType();

		TVSearchResultEntity entityDependingOnType = null;
		
		Gson gsonDependingOnType = null;
		
		GsonBuilder gsonBuilderDependingOnType = new GsonBuilder();

		JsonElement jsonElementEntity = jsonObject.get(Constants.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY);
		
		switch (entityTypeEnum) 
		{
			case CHANNEL: 
			{
				gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntityChannelDeserializer());
				break;
			}
			
			case PROGRAM: 
			{
				gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntityProgramDeserializer());
				break;
			}
			
			case SERIES: 
			{
				gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntitySeriesDeserializer());
				break;
			}
		}

		gsonDependingOnType = gsonBuilderDependingOnType.create();
		
		entityDependingOnType = gsonDependingOnType.fromJson(jsonElementEntity, TVSearchResultEntity.class);
		
		searchResult.entity = entityDependingOnType;
		
		/* This is sort of an ugly fix to fix the fact that backend returns broadcasts without program data, if search result was of type program */
		if(entityTypeEnum == ContentTypeEnum.PROGRAM) 
		{
			for(TVBroadcastWithChannelInfo broadcast : entityDependingOnType.getBroadcasts()) 
			{
				broadcast.setProgram(entityDependingOnType.getProgram());
			}
		}

		return searchResult;
	}

	

	@Override
	public boolean areDataFieldsValid() {
		boolean areFieldsValid = (displayText != null && entityType != null && entityType.length() > 0 && entity != null);

		if (areFieldsValid)
		{
			TVSearchResultEntity searchResultEntity = getEntity();
			
			switch (getEntityType()) 
			{
				case CHANNEL:
				{
					TVChannel tvChannel = searchResultEntity.getChannel();
					
					areFieldsValid = (tvChannel.areDataFieldsValid());
					
					break;
				}
				
				case SERIES: 
				{
					String seriesId = searchResultEntity.getId();
					
					String seriesName = searchResultEntity.getName();
					
					areFieldsValid = (!TextUtils.isEmpty(seriesId) && !TextUtils.isEmpty(seriesName));
					
					break;
				}
				
				case PROGRAM:
				{
					TVProgram program = searchResultEntity.getProgram();
					
					areFieldsValid = (program.areDataFieldsValid());
					
					break;
				}
			}

		}

		return areFieldsValid;
	}
}
