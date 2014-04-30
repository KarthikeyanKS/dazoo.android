
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.PhaseJSON;



public class Phase 
	extends PhaseJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Phase.class.getName();
	
	
	public Phase(){}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((phaseId == null) ? 0 : phaseId.hashCode());
		
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
		
		if (phaseId == null) 
		{
			if (other.phaseId != null) 
			{
				return false;
			}
		} 
		else if (!phaseId.equals(other.phaseId)) 
		{
			return false;
		}
		
		return true;
	}
}
