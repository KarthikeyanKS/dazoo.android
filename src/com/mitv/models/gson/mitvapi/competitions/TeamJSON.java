
package com.mitv.models.gson.mitvapi.competitions;



public class TeamJSON 
{
	protected String teamId;
	protected String phaseId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected TeamImageSetJSON images;
	
	
	
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


	public String getNation() {
		return nation;
	}



	public String getNationCode() {
		return nationCode;
	}



	public TeamImageSetJSON getImages() {
		return images;
	}
}
