package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.mitv.Consts;

public class TVProgram implements JsonDeserializer<TVProgram> {
	
	@Expose
	private ProgramTypeEnum programType;
	
	@Expose
	private String programId;
	
	@Expose
	private String title;
	
	@Expose
	private String synopsisShort;
	
	@Expose
	private String synopsisLong;
	
	@Expose
	private ImageSetOrientation images;
	
	@Expose
	private ArrayList<String> tags;
	
	@Expose
	private ArrayList<TVCredit> credits;
	
	/* This variable is used if programType == "OTHER" */
	@Expose (deserialize = false)
	private String category; 
	
	/* The following variables are being used if programType == "TV_EPISODE" */
	private TVSeries series;
	
	@Expose (deserialize = false)
	private TVSeriesSeason season;

	@Expose (deserialize = false)
	private Integer episodeNumber;
	
	/* The following variables are being used if programType == "MOVIE" */
	@Expose (deserialize = false)
	private Integer year;
	
	@Expose (deserialize = false)
	private String genre;
	
	/* The following variables are being used if programType == "SPORT" */
	@Expose (deserialize = false)
	private TVSportType sportType;

	@Expose (deserialize = false)
	private String tournament;

	@Override
	public TVProgram deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		
		TVProgram tvProgramWithStandardFieldsSet = gson.fromJson(jsonElement, TVProgram.class);

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
			case MOVIE: {
				
				/* Year */
				JsonElement jsonYearElement = jsonObject.get(Consts.PROGRAM_YEAR);
				this.year = jsonYearElement.getAsInt();
				
				/* Genre */
				JsonElement jsonGenreElement = jsonObject.get(Consts.PROGRAM_GENRE);
				this.genre = jsonGenreElement.getAsString();
				break;
			}
			case TV_EPISODE: {
				/* Series */
				JsonElement jsonSeriesElement = jsonObject.get(Consts.PROGRAM_SERIES);
				TVSeries tvSeries = gson.fromJson(jsonSeriesElement, TVSeries.class);
				this.series = tvSeries;
				
				/* Season */
				JsonElement jsonSeriesSeasonElement = jsonObject.get(Consts.PROGRAM_GENRE);
				TVSeriesSeason tvSeriesSeason = gson.fromJson(jsonSeriesSeasonElement, TVSeriesSeason.class);
				this.season = tvSeriesSeason;
				
				/* EpisodNumber */
				JsonElement jsonEpisodNumberElement = jsonObject.get(Consts.PROGRAM_EPISODE);
				this.episodeNumber = jsonEpisodNumberElement.getAsInt();
				break;
			}
			case SPORT: {
				JsonElement jsonSeriesElement = jsonObject.get(Consts.PROGRAM_SERIES);
				TVSeries tvSeries = gson.fromJson(jsonSeriesElement, TVSeries.class);
				this.series = tvSeries;
				
				/* SportType */
				JsonElement jsonSportTypeElement = jsonObject.get(Consts.PROGRAM_GENRE);
				TVSportType tvSportType = gson.fromJson(jsonSportTypeElement, TVSportType.class);
				this.sportType = tvSportType;
				
				/* Tournament */
				JsonElement jsonTournamentElement = jsonObject.get(Consts.PROGRAM_TOURNAMENT);
				this.tournament = jsonTournamentElement.getAsString();
				break;
			}
			case OTHER: {
				/* category */
				JsonElement jsonCategoryElement = jsonObject.get(Consts.PROGRAM_CATEGORY);
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

	public ProgramTypeEnum getProgramType() {
		return programType;
	}

	public String getProgramId() {
		return programId;
	}

	public String getTitle() {
		return title;
	}

	public String getSynopsisShort() {
		return synopsisShort;
	}

	public String getSynopsisLong() {
		return synopsisLong;
	}

	public ImageSetOrientation getImages() {
		return images;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public ArrayList<TVCredit> getCredits() {
		return credits;
	}

	public String getCategory() {
		return category;
	}

	public TVSeries getSeries() {
		return series;
	}

	public TVSeriesSeason getSeason() {
		return season;
	}

	public Integer getEpisodeNumber() {
		return episodeNumber;
	}

	public Integer getYear() {
		return year;
	}

	public String getGenre() {
		return genre;
	}

	public TVSportType getSportType() {
		return sportType;
	}

	public String getTournament() {
		return tournament;
	}
	
	
}
