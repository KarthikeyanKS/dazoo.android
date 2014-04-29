
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.TeamJSON;



public class Team 
	extends TeamJSON
{
	public Team(){}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((teamID == null) ? 0 : teamID.hashCode());
		
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
		
		if (teamID == null) 
		{
			if (other.teamID != null) 
			{
				return false;
			}
		} 
		else if (!teamID.equals(other.teamID)) 
		{
			return false;
		}
		
		return true;
	}
}
