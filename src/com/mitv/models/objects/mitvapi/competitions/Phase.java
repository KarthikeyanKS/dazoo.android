
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.PhaseJSON;



public class Phase 
	extends PhaseJSON
{
	public Phase(){}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((phaseID == null) ? 0 : phaseID.hashCode());
		
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
		
		Phase other = (Phase) obj;
		
		if (phaseID == null) 
		{
			if (other.phaseID != null) 
			{
				return false;
			}
		} 
		else if (!phaseID.equals(other.phaseID)) 
		{
			return false;
		}
		
		return true;
	}
}
