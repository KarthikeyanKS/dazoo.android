
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;
import android.util.Log;

import com.mitv.enums.ProgramTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVProgramJSON;
import com.mitv.models.orm.TVProgramORM;



public class TVProgram
	extends TVProgramJSON 
	implements GSONDataFieldValidation
{
	private static final String TAG = TVProgram.class.getName();
	
	
	
	public TVProgram(){}
	
	
	
	public TVProgram(TVProgramORM ormData)
	{
		this.programType = ormData.getProgramType();
		this.programId = ormData.getProgramId();
		this.title = ormData.getTitle();
		this.synopsisShort = ormData.getSynopsisShort();
		this.synopsisLong = ormData.getSynopsisLong();
		this.images = ormData.getImages();
		this.tags = ormData.getTags();
		this.credits = ormData.getCredits();
		this.season = ormData.getSeason();
		this.episodeNumber = ormData.getEpisodeNumber();
		this.year = ormData.getYear();
		this.genre = ormData.getGenre();
		this.sportType = ormData.getSportType();
		this.tournament = ormData.getTournament();
	}
	
	
	
	public void setProgramId(String programId) 
	{
		this.programId = programId;
	}

	
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	
	
	public void setProgramType(ProgramTypeEnum programType)
	{
		this.programType = programType;
	}
	
	
	
	public void setSeason(TVSeriesSeason season)
	{
		this.season = season;
	}

	
	
	public void  setEpisodeNumber(Integer episodeNumber) 
	{
		this.episodeNumber = episodeNumber;
	}

	
	
	public void setYear(Integer year) 
	{
		this.year = year;
	}

	
	
	public String getYearAsString() 
	{
		String yearString;

		if (year != null) 
		{
			if(year != 0)
			{
				yearString = String.valueOf(year);
			}
			else
			{
				yearString = "";
			}
		}
		else
		{
			yearString = "";
		}

		return yearString;
	}

	
	
	public String getGenreAsString() 
	{
		String genreString = "";

		if(genre != null)
		{
			genreString = genre;
		}
		
		return genreString;
	}
	
	
	
	public boolean hasZeroValueForSeasonOrEpisodeNumber()
	{
		boolean hasZeroOrNullValueForSeason = (season == null || season.getNumber() == null || season.getNumber() == 0);
		
		boolean hasZeroOrNullValueForEpisodeNumber = (episodeNumber == null || episodeNumber == 0);
		
		boolean hasZeroValueForSeasonOrEpisodeNumber = hasZeroOrNullValueForSeason || hasZeroOrNullValueForEpisodeNumber;

		return hasZeroValueForSeasonOrEpisodeNumber;
	}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{	
		boolean imagesOk = getImages().areDataFieldsValid();
		
		boolean allCreditEntriesOk = true;
		
		for(TVCredit tvCredit : getCredits()) 
		{
			boolean creditEntryOk = tvCredit.areDataFieldsValid();
			
			if(!creditEntryOk)
			{
				allCreditEntriesOk = false;
				break;
			}
		}
		
		boolean sharedFieldsAreOK = (
				!TextUtils.isEmpty(getProgramId()) && (getTitle() != null) &&
				(getSynopsisShort() != null) && (getSynopsisLong() != null) &&
				(getTags() != null) && !getTags().isEmpty() && imagesOk && allCreditEntriesOk
				);
		
		/* Test depending on programType */
		ProgramTypeEnum programType = getProgramType();
		
		boolean typeDependantFieldsOk = false;
		
		switch (programType) 
		{
			case MOVIE: 
			{
				typeDependantFieldsOk = ((getYear() != null) && !TextUtils.isEmpty(getGenre()));
				break;
			}
			
			case TV_EPISODE:
			{
				boolean tvSeriesOk = getSeries().areDataFieldsValid();
				boolean tvSeasonOk = getSeason().areDataFieldsValid();
				boolean episodeNumberOk = (getEpisodeNumber() != null);
				typeDependantFieldsOk = tvSeriesOk && tvSeasonOk && episodeNumberOk;
				break;
			}
			
			case SPORT:
			{
				boolean sportTypeOk = getSportType().areDataFieldsValid();
				boolean tournamentOk = !TextUtils.isEmpty(getTournament());
				typeDependantFieldsOk = sportTypeOk && tournamentOk;
				break;
			}
			
			case OTHER:
			{
				typeDependantFieldsOk = !TextUtils.isEmpty(getCategory());
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled program type");
				break;
			}
		}
		
		boolean areDataFieldsValid = (sharedFieldsAreOK && typeDependantFieldsOk);
		return areDataFieldsValid;
	}
}