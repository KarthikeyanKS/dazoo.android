
package com.mitv.models.gson.mitvapi;



import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.mitv.Constants;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.UserLikeNextBroadcast;



public class UserLikeJSON 
	implements JsonDeserializer<UserLikeJSON>
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	
	@Expose
	protected String likeType;
	
	@Expose
	protected String title;
	
	@Expose (deserialize = false)
	protected Integer broadcastCount;
	
	/* This will be null if broadcastCount == 0 */
	@Expose (deserialize = false)
	protected UserLikeNextBroadcast nextBroadcast;
	
	/* This variable is used if likeType == "SERIES" */
	@Expose (deserialize = false)
	protected String seriesId;

	/* This variable is used if likeType == "SPORT_TYPE" */
	@Expose (deserialize = false)
	protected String sportTypeId;
	
	/* This variable is used if likeType == "PROGRAM" */
	@Expose (deserialize = false)
	protected String programType;
	
	@Expose (deserialize = false)
	protected String programId;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "MOVIE" */
	@Expose (deserialize = false)
	protected String genre;
	
	@Expose (deserialize = false)
	protected Integer year;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "OTHER" */
	@Expose (deserialize = false)
	protected String category;
	

	/* This variable is used if likeType == "COMPETITION" */
	protected long competitionID;
	
	
	/* This variable is used if likeType == "COMPETITION_TEAM" */
	protected long teamID;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLikeJSON()
	{}
	
	
	
	@Override
	public UserLikeJSON deserialize(
			JsonElement jsonElement, 
			Type type,
			JsonDeserializationContext jsonDeserializationContext) 
					throws JsonParseException 
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		
		UserLikeJSON baseObject = gson.fromJson(jsonElement, UserLikeJSON.class);
		
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		Integer broadcastCount = jsonObject.get(Constants.LIKE_NEXT_BROADCAST_COUNT).getAsInt();
		
		if(broadcastCount != null) {
			baseObject.broadcastCount = broadcastCount;
			if(baseObject.broadcastCount  > 0) {
				JsonElement nextBroadcastJsonElement = jsonObject.get(Constants.LIKE_NEXT_BROADCAST);
				UserLikeNextBroadcast userLikeNextBroadcast = new Gson().fromJson(nextBroadcastJsonElement, UserLikeNextBroadcast.class);
				baseObject.nextBroadcast = userLikeNextBroadcast;
			}
		}

		switch (baseObject.getLikeType())
		{
			case SERIES:
			{
				JsonElement jsonSeriesIdElement = jsonObject.get(Constants.JSON_USER_LIKE_SERIES_SERIES_ID);
				
				baseObject.seriesId = jsonSeriesIdElement.getAsString();
			}
			break;
			
			case SPORT_TYPE:
			{
				JsonElement jsonSportTypeElement = jsonObject.get(Constants.JSON_USER_LIKE_SPORT_TYPE_ID);
				
				baseObject.sportTypeId = jsonSportTypeElement.getAsString();
			}
			break;
			
			case COMPETITION:
			{
				break;
			}
			
			case COMPETITION_TEAM:
			{
				
				break;
			}
			
			default:
			case PROGRAM:
			{
				JsonElement jsonProgramIdElement = jsonObject.get(Constants.JSON_USER_LIKE_PROGRAM_ID);
				
				baseObject.programId = jsonProgramIdElement.getAsString();
				
				JsonElement jsonProgramTypeElement = jsonObject.get(Constants.JSON_USER_LIKE_PROGRAM_TYPE);
				
				baseObject.programType = jsonProgramTypeElement.getAsString();
				
				switch (this.getProgramType()) 
				{
					case MOVIE:
					{
						JsonElement jsonGenreElement = jsonObject.get(Constants.JSON_USER_LIKE_PROGRAM_MOVIE_GENRE);
						
						baseObject.genre = jsonGenreElement.getAsString();
						
						JsonElement jsonYearElement = jsonObject.get(Constants.JSON_USER_LIKE_PROGRAM_MOVIE_YEAR);
						
						baseObject.year = jsonYearElement.getAsInt();
					}
					break;
					
					default:
					case OTHER:
					{
						JsonElement jsonCategoryElement = jsonObject.get(Constants.JSON_USER_LIKE_PROGRAM_OTHER_CATEGORY);
						
						baseObject.category = jsonCategoryElement.getAsString();
					}
					break;
				}
			}
			break;
		}
		
		return baseObject;
	}
	
	
	
	public LikeTypeResponseEnum getLikeType() 
	{
		return LikeTypeResponseEnum.getTypeEnumFromStringRepresentation(likeType);
	}
	
	
	
	public ProgramTypeEnum getProgramType() 
	{
		return ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(programType);
	}

	
	
	public String getTitle() {
		return title;
	}
	
	public String getSeriesId() {
		return seriesId;
	}

	public String getProgramId() {
		return programId;
	}

	public String getGenre() {
		return genre;
	}

	public Integer getYear() {
		return year;
	}

	public String getCategory() {
		return category;
	}
	
	public String getSportTypeId() {
		return sportTypeId;
	}

	public Integer getBroadcastCount() {
		return broadcastCount;
	}

	public UserLikeNextBroadcast getNextBroadcast() {
		return nextBroadcast;
	}



	public long getCompetitionID() {
		return competitionID;
	}



	public long getTeamID() {
		return teamID;
	}	
}