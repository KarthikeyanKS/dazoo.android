
package com.mitv.models;



import java.util.ArrayList;

import android.text.TextUtils;
import android.util.Log;

import com.mitv.enums.ProgramTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVProgramJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class TVProgram
	extends TVProgramJSON 
	implements GSONDataFieldValidation
{
	private static final String TAG = TVProgram.class.getName();
	
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


	public TVProgram(NotificationSQLElement item)
	{
		this.title = item.getProgramTitle();
		this.synopsisShort = item.getSynopsisShort();
		this.synopsisLong = item.getSynopsisLong();
		
		this.programId = item.getProgramId();
		
		ProgramTypeEnum programType = ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(item.getProgramType());
		
		this.programType = programType;
		
		switch(programType)
		{
			case TV_EPISODE:
			{
				this.series = new TVSeries(item);
				this.season = new TVSeriesSeason(item);
				this.episodeNumber = item.getProgramEpisodeNumber();
				break;
			}
			
			case SPORT:
			{
				this.sportType = new TVSportType(item);
				this.genre = item.getProgramGenre();
				break;
			}
			
			case OTHER:
			{
				this.category = item.getProgramCategory();
				break;
			}
			
			case MOVIE:
			{
				this.year = item.getProgramYear();
				this.genre = item.getProgramGenre();
				break;
			}
			
			case UNKNOWN:
			default:
			{
				Log.w(TAG, "Unhandled program type.");
				break;
			}
		}
		
		this.year = item.getProgramYear();

		// TODO - Using empty image set representation as no data is available from the notification item.
		this.images = new ImageSetOrientation();
		
		// TODO - Using empty tags representation as no data is available from the notification item.
		this.tags = new ArrayList<String>();
		
		// TODO - Using empty TVCredit representation as no data is available from the notification item.
		this.credits = new ArrayList<TVCredit>();
	}

	@Override
	public boolean areDataFieldsValid() {
		
		boolean imagesOk = getImages().areDataFieldsValid();
		
		boolean allCreditEntriesOk = true;
		for(TVCredit tvCredit : getCredits()) {
			boolean creditEntryOk = tvCredit.areDataFieldsValid();
			if(!creditEntryOk) {
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
		switch (programType) {
			case MOVIE: {
				typeDependantFieldsOk = ((getYear() != null) && !TextUtils.isEmpty(getGenre()));
				break;
			}
			case TV_EPISODE: {
				boolean tvSeriesOk = getSeries().areDataFieldsValid();
				boolean tvSeasonOk = getSeason().areDataFieldsValid();
				boolean episodeNumberOk = (getEpisodeNumber() != null);
				typeDependantFieldsOk = tvSeriesOk && tvSeasonOk && episodeNumberOk;
				break;
			}
			case SPORT: {
				boolean sportTypeOk = getSportType().areDataFieldsValid();
				boolean tournamentOk = !TextUtils.isEmpty(getTournament());
				typeDependantFieldsOk = sportTypeOk && tournamentOk;
				break;
			}
			case OTHER: {
				typeDependantFieldsOk = !TextUtils.isEmpty(getCategory());
				break;
			}
		}
		
		boolean areDataFieldsValid = (sharedFieldsAreOK && typeDependantFieldsOk);
		return areDataFieldsValid;
	}
}