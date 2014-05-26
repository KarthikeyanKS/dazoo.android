

package com.mitv.models.gson.mitvapi.competitions;

import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class CompetitionJSON 
{
	protected long competitionId;
	protected String displayName;
	protected String displayCompetitionType;
	protected String startDate;
	protected String endDate;
	protected boolean visible;
	protected ImageSetSizeJSON banner;
	
	
	
	public CompetitionJSON()
	{
		// TODO - Remove hardcoded string
		displayCompetitionType = "Futebol";
	}



	public long getCompetitionId() 
	{
		return competitionId;
	}



	public String getDisplayName() {
		return displayName;
	}



	public String getStartDate() {
		return startDate;
	}



	public String getEndDate() {
		return endDate;
	}



	public boolean isVisible() {
		return visible;
	}



	public ImageSetSizeJSON getBanner() {
		return banner;
	}



	public String getDisplayCompetitionType() {
		return displayCompetitionType;
	}
}
