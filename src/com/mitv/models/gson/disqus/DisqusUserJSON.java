
package com.mitv.models.gson.disqus;



public class DisqusUserJSON 
{
	protected String username;
	protected String about;
	protected String name;
	protected String url;
	protected boolean isAnonymous;
	protected float rep;
	protected String profileUrl;
    protected float reputation;
    protected String location;
    protected boolean isPrivate;
    protected boolean isPrimary;
    protected String joinedAt;
    protected String id;
    
    
    
    private DisqusUserJSON(){}



	public String getUsername() {
		return username;
	}



	public String getAbout() {
		return about;
	}



	public String getName() {
		return name;
	}



	public String getUrl() {
		return url;
	}



	public boolean isAnonymous() {
		return isAnonymous;
	}



	public float getRep() {
		return rep;
	}



	public String getProfileUrl() {
		return profileUrl;
	}



	public float getReputation() {
		return reputation;
	}



	public String getLocation() {
		return location;
	}



	public boolean isPrivate() {
		return isPrivate;
	}



	public boolean isPrimary() {
		return isPrimary;
	}



	public String getJoinedAt() {
		return joinedAt;
	}



	public String getId() {
		return id;
	}
}
