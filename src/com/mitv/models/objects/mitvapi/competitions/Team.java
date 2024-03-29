
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.models.gson.mitvapi.competitions.TeamJSON;



public class Team 
	extends TeamJSON
{	
	public Team(){}
	
	
	
	public String getFlagImageURL()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(Constants.EVENT_FLAG_IMAGE_PATH);
		sb.append(Constants.FORWARD_SLASH);
		sb.append(getTeamId());
		sb.append(Constants.EVENT_STADIUM_IMAGE_SIZE_LARGE);
		sb.append(Constants.EVENT_STADIUM_IMAGE_EXTENSION);
		
		return sb.toString();
	}
	
	
	
	public String getTeamImageURL()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(Constants.TEAM_PAGE_TEAM_IMAGE_PATH);
		sb.append(Constants.FORWARD_SLASH);
		sb.append(getTeamId());
		sb.append(Constants.EVENT_STADIUM_IMAGE_SIZE_LARGE);
		sb.append(Constants.EVENT_STADIUM_IMAGE_EXTENSION);
		
		return sb.toString();
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + (int) getTeamId();
		
		return result;
	}

	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		Team other = (Team) obj;
		
		if (getTeamId() != other.getTeamId()) 
		{
			return false;
		}
		
		return true;
	}
	
	
	
	public String getShareUrl() 
	{
		StringBuilder sb = new StringBuilder();

		sb.append(Constants.HTTP_SCHEME_USED)
			.append(Constants.BACKEND_DEPLOYMENT_DOMAIN_URL)
			.append(Constants.URL_SHARE_SPORT_SPANISH)
			.append(Constants.URL_SHARE_SPORT_team_SPANISH)
			.append(this.getTeamId());
		
		String url = sb.toString();
		
		return url;
	}
}
