
package com.mitv.models.gson.mitvapi.competitions;



public class StandingsJSON
{
	protected long competitionId;
	protected long phaseId;
	protected String phase;
	protected String team;
	protected long teamId;
	protected String rank;
	protected int matches;
	protected int matchesWon;
	protected int matchesLost;
	protected int matchesDrawn;
	protected int points;
	protected int goalsFor;
	protected int goalsAgainst;
	

	
	public StandingsJSON(){}



	public long getCompetitionId() {
		return competitionId;
	}



	public long getPhaseId() {
		return phaseId;
	}



	public String getPhase() {
		return phase;
	}



	public String getTeam() {
		return team;
	}



	public long getTeamId() {
		return teamId;
	}



	public String getRank() {
		return rank;
	}



	public int getMatches() {
		return matches;
	}



	public int getMatchesWon() {
		return matchesWon;
	}



	public int getMatchesLost() {
		return matchesLost;
	}



	public int getMatchesDrawn() {
		return matchesDrawn;
	}



	public int getPoints() {
		return points;
	}



	public int getGoalsFor() {
		return goalsFor;
	}



	public int getGoalsAgainst() {
		return goalsAgainst;
	}


	
}
