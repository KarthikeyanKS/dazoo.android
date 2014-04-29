
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class PhaseJSON 
{
	protected String phaseID;
	protected String stage;
	protected String name;
	protected String type;
	protected List<String> teamIDs;
	
	
	
	public PhaseJSON(){}



	public String getPhaseID() {
		return phaseID;
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
		return teamIDs;
	}
}
