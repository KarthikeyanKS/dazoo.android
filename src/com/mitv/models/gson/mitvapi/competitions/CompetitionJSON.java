

package com.mitv.models.gson.mitvapi.competitions;



public class CompetitionJSON 
{
	protected String competitionId;
	protected String url;
	protected String displayName;
	protected String startDate;
	protected String endDate;
	protected boolean visible;
	
	
	
	public CompetitionJSON()
	{}



	public String getCompetitionId() 
	{
		return competitionId;
	}



	public String getUrl() {
		return url;
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
}
