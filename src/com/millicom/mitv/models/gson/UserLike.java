
package com.millicom.mitv.models.gson;



import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.millicom.mitv.enums.LikeTypeEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.mitv.Consts;



public class UserLike 
	implements JsonDeserializer<UserLike>
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	
	@Expose
	private String likeType;
	
	@Expose
	private String title;
	
	/* This variable is used if likeType == "SERIES" */
	@Expose (deserialize = false)
	private String seriesId;

	/* This variable is used if likeType == "SPORT_TYPE" */
	@Expose (deserialize = false)
	private String sportTypeId;
	
	/* This variable is used if likeType == "PROGRAM" */
	@Expose (deserialize = false)
	private String programType;
	
	/* This variable is used if likeType == "PROGRAM" */
	@Expose (deserialize = false)
	private String programId;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "MOVIE" */
	@Expose (deserialize = false)
	private String genre;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "MOVIE" */
	@Expose (deserialize = false)
	private Integer year;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "OTHER" */
	@Expose (deserialize = false)
	private String category;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserLike()
	{}
	
	
	
	@Override
	public UserLike deserialize(
			JsonElement jsonElement, 
			Type type,
			JsonDeserializationContext jsonDeserializationContext) 
					throws JsonParseException 
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		
		UserLike baseObject = gson.fromJson(jsonElement, UserLike.class);
		
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		switch (baseObject.getLikeType())
		{
			case SERIES:
			{
				JsonElement jsonSeriesIdElement = jsonObject.get(Consts.JSON_USER_LIKE_SERIES_SERIES_ID);
				
				seriesId = jsonSeriesIdElement.getAsString();
			}
			break;
			
			case SPORT_TYPE:
			{
				JsonElement jsonSportTypeElement = jsonObject.get(Consts.JSON_USER_LIKE_SPORT_TYPE_ID);
				
				sportTypeId = jsonSportTypeElement.getAsString();
			}
			break;
			
			default:
			case PROGRAM:
			{
				JsonElement jsonProgramIdElement = jsonObject.get(Consts.JSON_USER_LIKE_PROGRAM_ID);
				
				programId = jsonProgramIdElement.getAsString();
				
				JsonElement jsonProgramTypeElement = jsonObject.get(Consts.JSON_USER_LIKE_PROGRAM_TYPE);
				
				programType = jsonProgramTypeElement.getAsString();
				
				switch (this.getProgramType()) 
				{
					case MOVIE:
					{
						JsonElement jsonGenreElement = jsonObject.get(Consts.JSON_USER_LIKE_PROGRAM_MOVIE_GENRE);
						
						genre = jsonGenreElement.getAsString();
						
						JsonElement jsonYearElement = jsonObject.get(Consts.JSON_USER_LIKE_PROGRAM_MOVIE_YEAR);
						
						year = jsonYearElement.getAsInt();
					}
					break;
					
					default:
					case OTHER:
					{
						JsonElement jsonCategoryElement = jsonObject.get(Consts.JSON_USER_LIKE_PROGRAM_OTHER_CATEGORY);
						
						category = jsonCategoryElement.getAsString();
					}
					break;
				}
			}
			break;
		}
		
		return baseObject;
	}
	
	
	
	public LikeTypeEnum getLikeType() 
	{
		return LikeTypeEnum.getLikeTypeEnumFromCode(likeType);
	}
	
	
	
	public ProgramTypeEnum getProgramType() 
	{
		return ProgramTypeEnum.getProgramTypeEnumFromCode(programType);
	}



	public String getTitle() 
	{
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
}