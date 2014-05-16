
package com.mitv.models.gson.mitvapi.competitions;



public class EventLineUpJSON 
{
	protected long lineUpId;
	protected long eventId;
	protected int actionSet; 		//Remove?
	protected int actionId; 		//Remove?
	protected String actionMinute;	//Remove?
	protected long personId;
	protected String person;
	protected String personShort;
	protected long teamId;
	protected String team;
	protected int functionCode;
	protected String function; 
 	protected String functionShort;
	protected boolean isCaptain; 
	protected String shirtNr;
	protected String position; 
	protected boolean inStartingLineUp;
	protected int lineUpInMinute; 
	protected String lineUpOutMinute;
	protected long subPersonId;
	protected String subPerson;
	
	
	
	public EventLineUpJSON(){}



	public long getLineUpId() {
		return lineUpId;
	}



	public long getEventId() {
		return eventId;
	}



	public int getActionSet() {
		return actionSet;
	}



	public int getActionId() {
		return actionId;
	}



	public String getActionMinute() {
		return actionMinute;
	}



	public long getPersonId() {
		return personId;
	}



	public String getPerson() {
		return person;
	}
	
	
	
	public String getPersonShort() {
		return personShort;
	}



	public long getTeamId() {
		return teamId;
	}



	public String getTeam() {
		return team;
	}



	public int getFunctionCode() {
		return functionCode;
	}



	public String getFunction() {
		return function;
	}



	public String getFunctionShort() {
		return functionShort;
	}



	public boolean isCaptain() {
		return isCaptain;
	}



	public String getShirtNr() {
		return shirtNr;
	}



	public String getPosition() {
		return position;
	}



	public boolean isInStartingLineUp() {
		return inStartingLineUp;
	}



	public int getLineUpInMinute() {
		return lineUpInMinute;
	}



	public String getLineUpOutMinute() {
		return lineUpOutMinute;
	}



	public long getSubPersonId() {
		return subPersonId;
	}



	public String getSubPerson() {
		return subPerson;
	}
}
