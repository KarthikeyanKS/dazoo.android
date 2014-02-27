
package com.millicom.mitv.models;



import java.util.ArrayList;

import android.util.Log;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.gson.TVProgramJSON;
import com.mitv.model.NotificationDbItem;



public class TVProgram
	extends TVProgramJSON
{
	private static final String TAG = TVProgram.class.getName();
	
	
	
	public TVProgram()
	{}
	
	
	
	public TVProgram(NotificationDbItem item)
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

	
	
	public void setEpisodeNumber(Integer episodeNumber) 
	{
		this.episodeNumber = episodeNumber;
	}

	
	
	public void setYear(Integer year) 
	{
		this.year = year;
	}
}