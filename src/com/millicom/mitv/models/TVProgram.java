
package com.millicom.mitv.models;



import junit.framework.Assert;
import android.text.TextUtils;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVProgramJSON;



public class TVProgram
	extends TVProgramJSON implements GSONDataFieldValidation
{
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