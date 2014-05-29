
package com.mitv.models.gson.mitvapi.competitions;



public class TeamJSON 
{
	protected long teamId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected String description;
	protected String teamImageCopyright;
	
	
	
	public TeamJSON()
	{}


	
	public long getTeamId() {
		return teamId;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getNation() {
		return nation;
	}



	public String getNationCode() {
		return nationCode;
	}



	public String getDescription() {
		return description;
	}



	public String getTeamImageCopyright() {
		return teamImageCopyright;
	}
}
