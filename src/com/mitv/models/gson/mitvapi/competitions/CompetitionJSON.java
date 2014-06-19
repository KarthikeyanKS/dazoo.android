

package com.mitv.models.gson.mitvapi.competitions;



import android.util.Log;



public class CompetitionJSON 
{
	private static final String TAG = CompetitionJSON.class.getName();
	
	
	private Long competitionId;
	private String displayName;
	private String competitionCategory;
	private String startDate;
	private String endDate;
	private boolean visible;
	
	
	
	public CompetitionJSON(){}



	public Long getCompetitionId() 
	{
		if(competitionId == null)
		{
			competitionId = Long.valueOf(0);
			
			Log.w(TAG, "competitionId is null");
		}
		
		return competitionId;
	}



	public String getDisplayName() 
	{
		if(displayName == null)
		{
			displayName = "";
			
			Log.w(TAG, "displayName is null");
		}
		
		return displayName;
	}



	public String getStartDate() 
	{
		if(startDate == null)
		{
			startDate = "";
			
			Log.w(TAG, "startDate is null");
		}
		
		return startDate;
	}



	public String getEndDate() 
	{
		if(endDate == null)
		{
			endDate = "";
			
			Log.w(TAG, "endDate is null");
		}
		
		return endDate;
	}



	public boolean isVisible() 
	{
		return visible;
	}



	protected String getCompetitionCategory() 
	{
		if(competitionCategory == null)
		{
			competitionCategory = "";
			
			Log.w(TAG, "competitionCategory is null");
		}
		
		return competitionCategory;
	}
}
