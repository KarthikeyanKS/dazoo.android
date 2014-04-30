
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class StandingsJSON
{
	protected String standingId;
	protected int tiePoints;
	protected int winnerPoints;
	protected int loserPoints;
	protected List<StandingPositionJSON> currentStandings;
	protected List<String> eventIds;
	protected List<String> phaseIds;
	
	
	
	public StandingsJSON(){}


	
	public String getStandingId() {
		return standingId;
	}


	public int getTiePoints() {
		return tiePoints;
	}


	public int getWinnerPoints() {
		return winnerPoints;
	}


	public int getLoserPoints() {
		return loserPoints;
	}


	public List<StandingPositionJSON> getCurrentStandings() {
		return currentStandings;
	}


	public List<String> getEventIds() {
		return eventIds;
	}


	public List<String> getPhaseIds() {
		return phaseIds;
	}
}
