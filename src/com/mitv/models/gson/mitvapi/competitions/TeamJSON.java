
package com.mitv.models.gson.mitvapi.competitions;



public class TeamJSON 
{
	protected long teamId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected TeamImageSetJSON images;
	
	
	
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



	public TeamImageSetJSON getImages() {
		return images;
	}
}
