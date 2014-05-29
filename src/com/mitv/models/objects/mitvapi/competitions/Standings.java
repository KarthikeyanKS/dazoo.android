
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.competitions.StandingsJSON;



public class Standings 
	extends StandingsJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = Standings.class.getName();
	
	
	public Standings(){}
	
	
	public int getGoalsForMinusGoalsAgainst()
	{
		return goalsFor - goalsAgainst;
	}
	
	
	
	public boolean isTheSamePhaseAs(Standings element)
	{
		return this.phase.equalsIgnoreCase(element.phase);
	}
}
