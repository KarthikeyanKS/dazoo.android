
package com.mitv.models.gson.mitvapi.competitions;



import android.util.Log;



public class PhaseJSON 
{
	private static final String TAG = PhaseJSON.class.getName();
	
	
	private long competitionId;
	private long phaseId;
	private String stage;
	private String phase;
	private boolean table; 
	private boolean current;
	private boolean currents;
	private String dateStart;
	private String dateEnd;
	
	
	
	public PhaseJSON(){}



	public long getPhaseId() 
	{
		return phaseId;
	}



	public String getStage() 
	{
		if(stage == null)
		{
			stage = "";
			
			Log.w(TAG, "stage is null");
		}
		
		return stage;
	}



	public long getCompetitionId() {
		return competitionId;
	}



	public String getPhase() 
	{
		if(phase == null)
		{
			phase = "";
			
			Log.w(TAG, "phase is null");
		}
		
		return phase;
	}



	public boolean isTable() 
	{
		return table;
	}



	public boolean isCurrent()
	{
		return current;
	}



	public boolean isCurrents()
	{
		return currents;
	}



	public String getStartDate() 
	{
		if(dateStart == null)
		{
			dateStart = "";
			
			Log.w(TAG, "dateStart is null");
		}
		
		return dateStart;
	}



	public String getEndDate() 
	{
		if(dateEnd == null)
		{
			dateEnd = "";
			
			Log.w(TAG, "dateEnd is null");
		}
		
		return dateEnd;
	}
}
