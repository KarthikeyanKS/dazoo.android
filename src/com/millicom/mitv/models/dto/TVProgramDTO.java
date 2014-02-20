
package com.millicom.mitv.models.dto;



import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.gson.TVProgram;
import com.millicom.mitv.models.gson.TVSeriesSeason;



public class TVProgramDTO
	extends TVProgram
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
}
