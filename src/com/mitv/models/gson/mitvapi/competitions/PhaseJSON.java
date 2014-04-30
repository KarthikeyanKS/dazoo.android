
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class PhaseJSON 
{
	protected String phaseId;
	protected String stage;
	protected String name;
	protected String type;
	protected List<String> teamIds;
	
	
	
	public PhaseJSON(){}



	public String getPhaseId() {
		return phaseId;
	}



	public String getStage() {
		return stage;
	}



	public String getName() {
		return name;
	}



	public String getType() {
		return type;
	}



	public List<String> getTeamIDs() {
		return teamIds;
	}
}
