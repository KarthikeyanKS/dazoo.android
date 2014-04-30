
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class EventJSON 
{
	protected String eventId;
	protected String team1Id;
	protected String team2Id;
	protected String phaseId;
	protected EventScoreJSON score;
	protected List<EventBroadcastDetailsJSON> broadcastDetails;
	protected String startDate;
	protected boolean isOngoing;
	
	
	
	public EventJSON(){}



	public String getEventId() {
		return eventId;
	}



	public String getTeam1Id() {
		return team1Id;
	}



	public String getTeam2Id() {
		return team2Id;
	}



	public String getPhaseId() {
		return phaseId;
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
