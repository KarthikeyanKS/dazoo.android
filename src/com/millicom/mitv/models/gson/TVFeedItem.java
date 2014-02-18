
package com.millicom.mitv.models.gson;



import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;



public class TVFeedItem
	implements JsonDeserializer<TVFeedItem>
{
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
		
//		switch (baseObject.getItemType())
//		{
//			
//		}
		
		return baseObject;
	}
	
	
	
//	public String getItemType()
//	{
//		return LikeTypeEnum.getLikeTypeEnumFromCode(likeType);
//	}
	
	
	
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