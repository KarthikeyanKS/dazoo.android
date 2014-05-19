
package com.mitv.models.gson.mitvapi.competitions;



public class EventHighlightJSON
{
	protected long highlightId;
	protected long eventId;
	protected String action;
	protected int actionCode;
	protected long actionTime;
	protected long teamId;
	protected String team;
	protected long personId;
	protected String actionMinute;
	protected String person;
	private String personShort;
	protected long subPersonId;
	protected String subPerson;
	protected String subPersonShort;
	protected long subActionReasonId;
	protected String subActionReason;
	
	
	
	public EventHighlightJSON(){}



	public long getHighlightId() {
		return highlightId;
	}



	public String getAction() {
		return action;
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

	
	
	public String getPersonShort() {
		return personShort;
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



	public long getEventId() {
		return eventId;
	}



	public String getSubPersonShort() {
		return subPersonShort;
	}
}
