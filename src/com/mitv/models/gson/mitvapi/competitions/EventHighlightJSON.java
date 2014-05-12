
package com.mitv.models.gson.mitvapi.competitions;



public class EventHighlightJSON
{
	private String highlightId;
	private String action;
	private int actionCode;
	private long actionTime;
	private long teamId;
	private String team;
	private long personId;
	private String actionMinute;
	private String person;
	private long subPersonId;
	private String subPerson;
	private long subActionReasonId;
	private String subActionReason;
	
	
	
	public EventHighlightJSON(){}



	public String getHighlightId() {
		return highlightId;
	}



	public String getAction() {
		return action;
	}



	public int getActionCode() {
		return actionCode;
	}



	public long getActionTime() {
		return actionTime;
	}



	public long getTeamId() {
		return teamId;
	}



	public String getTeam() {
		return team;
	}



	public long getPersonId() {
		return personId;
	}



	public String getActionMinute() {
		return actionMinute;
	}



	public String getPerson() {
		return person;
	}



	public long getSubPersonId() {
		return subPersonId;
	}



	public String getSubPerson() {
		return subPerson;
	}



	public long getSubActionReasonId() {
		return subActionReasonId;
	}



	public String getSubActionReason() {
		return subActionReason;
	}
}
