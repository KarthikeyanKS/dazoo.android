
package com.mitv.models.gson.mitvapi.competitions;

import java.util.List;

import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class TeamDetailsJSON 
{
	protected String teamID;
	protected String displayName;
	protected String phaseID;
	protected String flagID;
	protected String description;
	protected ImageSetSizeJSON teamBanner;
	protected TeamSocialLinksJSON socialLinks;
	protected List<String> coachIDs;
	protected List<String> managerIDs;
	protected List<String> playerIDs;
	
	
	
	public TeamDetailsJSON(){}
}
