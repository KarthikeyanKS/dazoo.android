
package com.mitv.models.gson.mitvapi.competitions;

import java.util.List;

import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class TeamDetailsJSON 
{
	protected String teamId;
	protected String phaseId;
	protected String displayName;
	protected String nation;
	protected String nationCode;
	protected String description;
	protected ImageSetSizeJSON images;
	protected TeamSocialLinksJSON socialLinks;
	protected List<String> coachIds;
	protected List<String> managerIds;
	protected List<String> playerIds;
	
	
	
	public TeamDetailsJSON(){}
}
