
package com.mitv.models.gson.mitvapi.competitions;

import android.util.Log;



public class StandingsJSON
{
	private static final String TAG = StandingsJSON.class.getName();
	
	
	private long competitionId;
	private long phaseId;
	private String phase;
	private String team;
	private long teamId;
	private String rank;
	private int matches;
	private int matchesWon;
	private int matchesLost;
	private int matchesDrawn;
	private int points;
	private int goalsFor;
	private int goalsAgainst;
	

	
	public StandingsJSON(){}



	public long getCompetitionId() 
	{
		return competitionId;
	}



	public long getPhaseId() 
	{
		return phaseId;
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



	public String getTeam()
	{
		if(team == null)
		{
			team = "";
			
			Log.w(TAG, "team is null");
		}
		
		return team;
	}



	public long getTeamId()
	{
		return teamId;
	}



	public String getRank()
	{
		if(rank == null)
		{
			rank = "";
			
			Log.w(TAG, "rank is null");
		}
		
		return rank;
	}



	public int getMatches() 
	{
		return matches;
	}



	public int getMatchesWon() 
	{
		return matchesWon;
	}



	public int getMatchesLost()
	{
		return matchesLost;
	}



	public int getMatchesDrawn() 
	{
		return matchesDrawn;
	}



	public int getPoints()
	{
		return points;
	}



	public int getGoalsFor()
	{
		return goalsFor;
	}



	public int getGoalsAgainst()
	{
		return goalsAgainst;
	}
}
