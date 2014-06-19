
package com.mitv.models.gson.mitvapi;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVCredit;
import com.mitv.models.objects.mitvapi.TVSeries;
import com.mitv.models.objects.mitvapi.TVSeriesSeason;
import com.mitv.models.objects.mitvapi.TVSportType;



public class TVProgramJSON 
	implements JsonDeserializer<TVProgramJSON> 
{	
	private static final String TAG = TVProgramJSON.class.getName();
	
	
	@Expose
	protected ProgramTypeEnum programType;
	
	@Expose
	protected String programId;
	
	@Expose
	protected String title;
	
	@Expose
	protected String synopsisShort;
	
	@Expose
	protected String synopsisLong;
	
	@Expose
	protected ImageSetOrientation images;
	
	@Expose
	protected List<String> tags;
	
	@Expose
	protected List<TVCredit> credits;
	
	/* This variable is used if programType == "OTHER" */
	@Expose (deserialize = false)
	protected String category; 
	
	/* The following variables are being used if programType == "TV_EPISODE" */
	protected TVSeries series;
	
	@Expose (deserialize = false)
	protected TVSeriesSeason season;

	@Expose (deserialize = false)
	protected Integer episodeNumber;
	
	/* The following variables are being used if programType == "MOVIE" */
	@Expose (deserialize = false)
	protected Integer year;
	
	@Expose (deserialize = false)
	protected String genre;
	
	/* The following variables are being used if programType == "SPORT" */
	@Expose (deserialize = false)
	protected TVSportType sportType;

	@Expose (deserialize = false)
	protected String tournament;

	
	
	@Override
	public TVProgramJSON deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException 
	{	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		
		TVProgramJSON tvProgramWithStandardFieldsSet = gson.fromJson(jsonElement, TVProgramJSON.class);

		this.programType = tvProgramWithStandardFieldsSet.programType;
		this.programId = tvProgramWithStandardFieldsSet.programId;
		this.title = tvProgramWithStandardFieldsSet.title;
		this.synopsisShort = tvProgramWithStandardFieldsSet.synopsisShort;
		this.synopsisLong = tvProgramWithStandardFieldsSet.synopsisLong;
		this.images = tvProgramWithStandardFieldsSet.images;
		this.tags = tvProgramWithStandardFieldsSet.tags;
		this.credits = tvProgramWithStandardFieldsSet.credits;
		
		
		/* Custom parsing of variables that varies depending on Program Type */
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		
		switch (tvProgramWithStandardFieldsSet.getProgramType()) {
			case MOVIE: 
		{		
				/* Year */
				JsonElement jsonYearElement = jsonObject.get(Constants.PROGRAM_YEAR);
				this.year = jsonYearElement.getAsInt();
				
				/* Genre */
				JsonElement jsonGenreElement = jsonObject.get(Constants.PROGRAM_GENRE);
				this.genre = jsonGenreElement.getAsString();
				break;
			}
			case TV_EPISODE: 
			{
				/* Series */
				JsonElement jsonSeriesElement = jsonObject.get(Constants.PROGRAM_SERIES);
				TVSeries tvSeries = gson.fromJson(jsonSeriesElement, TVSeries.class);
				this.series = tvSeries;
				
				/* Season */
				JsonElement jsonSeriesSeasonElement = jsonObject.get(Constants.PROGRAM_GENRE);
				TVSeriesSeason tvSeriesSeason = gson.fromJson(jsonSeriesSeasonElement, TVSeriesSeason.class);
				this.season = tvSeriesSeason;
				
				/* EpisodNumber */
				JsonElement jsonEpisodNumberElement = jsonObject.get(Constants.PROGRAM_EPISODE);
				this.episodeNumber = jsonEpisodNumberElement.getAsInt();
				break;
			}
			case SPORT: {
				JsonElement jsonSeriesElement = jsonObject.get(Constants.PROGRAM_SERIES);
				TVSeries tvSeries = gson.fromJson(jsonSeriesElement, TVSeries.class);
				this.series = tvSeries;
				
				/* SportType */
				JsonElement jsonSportTypeElement = jsonObject.get(Constants.PROGRAM_GENRE);
				TVSportType tvSportType = gson.fromJson(jsonSportTypeElement, TVSportType.class);
				this.sportType = tvSportType;
				
				/* Tournament */
				JsonElement jsonTournamentElement = jsonObject.get(Constants.PROGRAM_TOURNAMENT);
				this.tournament = jsonTournamentElement.getAsString();
				break;
			}
			case OTHER: {
				/* category */
				JsonElement jsonCategoryElement = jsonObject.get(Constants.PROGRAM_CATEGORY);
				this.category = jsonCategoryElement.getAsString();
				break;
			}
			case UNKNOWN:
			default: {
				/* Do nothing */
			}
		}
		
		return null;
	}

	
	
	public ProgramTypeEnum getProgramType() 
	{
		if(programType == null)
		{
			programType = ProgramTypeEnum.UNKNOWN;
			
			Log.w(TAG, "programType is null");
		}
		
		return programType;
	}

	
	
	public String getProgramId() 
	{
		if(programId == null)
		{
			programId = "";
			
			Log.w(TAG, "programId is null");
		}
		
		return programId;
	}

	
	
	public String getTitle() 
	{
		if(title == null)
		{
			title = "";
			
			Log.w(TAG, "title is null");
		}
		
		return title;
	}

	
	
	public String getSynopsisShort() 
	{
		if(synopsisShort == null)
		{
			synopsisShort = "";
			
			Log.w(TAG, "synopsisShort is null");
		}
		
		return synopsisShort;
	}

	
	
	public String getSynopsisLong() 
	{
		if(synopsisLong == null)
		{
			synopsisLong = "";
			
			Log.w(TAG, "synopsisLong is null");
		}
		
		return synopsisLong;
	}

	
	
	public ImageSetOrientation getImages() 
	{
		if(images == null)
		{
			images = new ImageSetOrientation();
			
			Log.w(TAG, "images is null");
		}
		
		return images;
	}

	
	
	public List<String> getTags() 
	{
		if(tags == null)
		{
			tags = Collections.emptyList();
			
			Log.w(TAG, "tags is null");
		}
		
		return tags;
	}

	
	
	public List<TVCredit> getCredits() 
	{
		if(credits == null)
		{
			credits = Collections.emptyList();
			
			Log.w(TAG, "credits is null");
		}
		
		return credits;
	}

	
	
	public String getCategory()
	{
		if(category == null)
		{
			category = "";
			
			Log.w(TAG, "category is null");
		}
		
		return category;
	}

	
	
	public TVSeries getSeries()
	{
		if(series == null)
		{
			series = new TVSeries();
			
			Log.w(TAG, "series is null");
		}
		
		return series;
	}

	
	
	public TVSeriesSeason getSeason()
	{
		if(season == null)
		{
			season = new TVSeriesSeason();
			
			Log.w(TAG, "season is null");
		}
		
		return season;
	}

	
	
	public Integer getEpisodeNumber()
	{
		if(episodeNumber == null)
		{
			episodeNumber = Integer.valueOf(0);
			
			Log.w(TAG, "episodeNumber is null");
		}
		
		return episodeNumber;
	}

	
	
	public Integer getYear()
	{
		if(year == null)
		{
			year = Integer.valueOf(0);
			
			Log.w(TAG, "year is null");
		}
		
		return year;
	}

	
	
	public String getGenre()
	{
		if(genre == null)
		{
			genre = "";
			
			Log.w(TAG, "genre is null");
		}
		
		return genre;
	}

	
	
	public TVSportType getSportType()
	{
		if(sportType == null)
		{
			sportType = new TVSportType();
			
			Log.w(TAG, "sportType is null");
		}
		
		return sportType;
	}

	
	
	public String getTournament()
	{
		if(tournament == null)
		{
			tournament = "";
			
			Log.w(TAG, "tournament is null");
		}
		
		return tournament;
	}
}