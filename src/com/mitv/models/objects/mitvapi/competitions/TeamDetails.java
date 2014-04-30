
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.TeamDetailsJSON;



public class TeamDetails
	extends TeamDetailsJSON
{
	public TeamDetails(){}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
		
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
		
		TeamDetails other = (TeamDetails) obj;
		
		if (teamId == null) 
		{
			if (other.teamId != null) 
			{
				return false;
			}
		} 
		else if (!teamId.equals(other.teamId)) 
		{
			return false;
		}
		
		return true;
	}
}
