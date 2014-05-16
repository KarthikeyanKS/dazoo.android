
package com.mitv.models.gson.mitvapi.competitions;



public class StandingPositionJSON
{
	private int position;
	private String teamID;
	private int totalPoints;
	private int lastEventChange;
	private String lastEventId;
	

	public StandingPositionJSON(){}
	
	
	
	public int getPosition() {
		return position;
	}


	public String getTeamID() {
		return teamID;
	}


	public int getTotalPoints() {
		return totalPoints;
	}


	public int getLastEventChange() {
		return lastEventChange;
	}


	public String getLastEventId() {
		return lastEventId;
	}
}
