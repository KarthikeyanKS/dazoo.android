
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class EventJSON 
{
	protected String eventID;
	protected String team1ID;
	protected String team2ID;
	protected String phaseID;
	protected EventScoreJSON score;
	protected List<EventBroadcastDetailsJSON> broadcastDetails;
	protected String startDate;
	protected boolean isOngoing;
	
	
	
	public EventJSON(){}



	public String getEventID() {
		return eventID;
	}



	public String getTeam1ID() {
		return team1ID;
	}



	public String getTeam2ID() {
		return team2ID;
	}



	public String getPhaseID() {
		return phaseID;
	}



	public EventScoreJSON getScore() {
		return score;
	}



	public List<EventBroadcastDetailsJSON> getBroadcastDetails() {
		return broadcastDetails;
	}


	
	public boolean isOngoing() {
		return isOngoing;
	}
}
