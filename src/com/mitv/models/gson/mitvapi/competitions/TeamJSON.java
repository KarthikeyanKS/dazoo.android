
package com.mitv.models.gson.mitvapi.competitions;

import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class TeamJSON 
{
	protected String teamId;
	protected String phaseId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected ImageSetSizeJSON images;
	
	
	
	public TeamJSON(){}


	
	public String getTeamId() {
		return teamId;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getPhaseId() {
		return phaseId;
	}
}
