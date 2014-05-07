
package com.mitv.models.gson.mitvapi.competitions;



public class TeamJSON 
{
	protected long teamId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected TeamImageSetJSON images;
	
	
	
	public TeamJSON()
	{
		// TODO - Remove this
		if(images == null)
		{
			images = new TeamImageSetJSON();
		}
	}


	
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
