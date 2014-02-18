
package com.millicom.mitv.models.gson;



import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.mitv.Consts;



public class TVFeedItem
	implements JsonDeserializer<TVFeedItem>
{
	private static final String	TAG	= "TVFeedItem";
	
	
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	
	@Expose
	private String itemType;
	
	@Expose
	private String title;
	
	/* This variable is used if itemType == "BROADCAST" or itemType == "RECOMMENDED_BROADCAST" or itemType == "POPULAR_BROADCAST" or itemType == "POPULAR_TWITTER" */
	@Expose (deserialize = false)
	private Broadcast broadcast;
	
	/* This variable is used if itemType == "POPULAR_BROADCASTS" */
	@Expose (deserialize = false)
	private List<Broadcast> broadcasts;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public TVFeedItem()
	{}



	@Override
	public TVFeedItem deserialize(
			JsonElement jsonElement, 
			Type type,
			JsonDeserializationContext jsonDeserializationContext)
					throws JsonParseException 
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		
		TVFeedItem baseObject = gson.fromJson(jsonElement, TVFeedItem.class);
		
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		switch (baseObject.getItemType())
		{
			case BROADCAST:
			case POPULAR_BROADCAST:
			case RECOMMENDED_BROADCAST:
			case POPULAR_TWITTER:
			{
				broadcasts = null;
				
				JsonElement jsonBroadcastElement = jsonObject.get(Consts.JSON_USER_FEED_ITEM_BROADCAST);
				
				try
				{
					broadcast = gson.fromJson(jsonBroadcastElement, Broadcast.class);
				}
				catch(JsonSyntaxException jsex)
				{
					Log.e(TAG, jsex.getMessage(), jsex);
					
					broadcast = null;
				}
			}
			break;
			
			case POPULAR_BROADCASTS:
			{
				broadcast = null;
				
				JsonElement jsonBroadcastsElement = jsonObject.get(Consts.JSON_USER_FEED_ITEM_BROADCASTS);
				
				try
				{
					broadcasts = Arrays.asList(gson.fromJson(jsonBroadcastsElement, Broadcast[].class));
				}
				catch(JsonSyntaxException jsex)
				{
					Log.e(TAG, jsex.getMessage(), jsex);
					
					broadcasts = null;
				}
			}
			break;
			
			case UNKNOWN:
			default:
			{
				broadcast = null;
				broadcasts = null;
			}
			break;
		}
		
		return baseObject;
	}
	
	
	
	public FeedItemTypeEnum getItemType()
	{
		return FeedItemTypeEnum.getFeedItemTypeEnumFromStringRepresentation(itemType);
	}
	
	
	
	public String getTitle() {
		return title;
	}



	public Broadcast getBroadcast() {
		return broadcast;
	}



	public List<Broadcast> getBroadcasts() {
		return broadcasts;
	}
}