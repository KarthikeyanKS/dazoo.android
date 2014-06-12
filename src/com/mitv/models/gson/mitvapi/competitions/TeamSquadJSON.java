
package com.mitv.models.gson.mitvapi.competitions;



import android.util.Log;



public class TeamSquadJSON 
{
	private static final String TAG = TeamSquadJSON.class.getName();
	
	
	private long id;
	private long teamId;
	private long personId;
	private String person;
	private String personShort;
	private String nation;
	private long birthDay;
	private int functionType;
	private String function;
	private int shirtNumber;
	private int matches;
	private int matchesStart;
	private int goals;
	private int assists;
	private int yellowCards;
	private int redCards;
	
	
	
	public TeamSquadJSON(){}

	
	
	public long getId()
	{
		return id;
	}

	
	
	public long getTeamId()
	{
		return teamId;
	}

	
	
	public long getPersonId()
	{
		return personId;
	}

	
	
	public String getPerson() 
	{
		if(person == null)
		{
			person = "";
			
			Log.w(TAG, "person is null");
		}
		
		return person;
	}
	
	
	
	public String getPersonShort()
	{
		if(personShort == null)
		{
			personShort = "";
			
			Log.w(TAG, "personShort is null");
		}
		
		return personShort;
	}

	
	
	public String getNation()
	{
		if(nation == null)
		{
			nation = "";
			
			Log.w(TAG, "nation is null");
		}
		
		return nation;
	}

	
	
	public long getBirthDay() 
	{
		return birthDay;
	}

	
	
	public int getFunctionType()
	{
		return functionType;
	}

	
	
	public String getFunction() 
	{
		if(function == null)
		{
			function = "";
			
			Log.w(TAG, "function is null");
		}
		
		return function;
	}
	
	
	
	public int getShirtNumber()
	{
		return shirtNumber;
	}

	
	
	public int getMatches() 
	{
		return matches;
	}

	
	
	public int getMatchesStart() 
	{
		return matchesStart;
	}

	
	
	public int getGoals() 
	{
		return goals;
	}

	
	
	public int getAssists()
	{
		return assists;
	}

	
	
	public int getYellowCards() 
	{
		return yellowCards;
	}

	
	
	public int getRedCards() 
	{
		return redCards;
	}
}
