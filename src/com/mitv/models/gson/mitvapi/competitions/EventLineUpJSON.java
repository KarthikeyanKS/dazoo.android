
package com.mitv.models.gson.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class EventLineUpJSON
	extends BaseObjectJSON
{
	private static final String TAG = EventLineUpJSON.class.getName();
	
	
	private long teamId;
	private String team;
	private long personId;
	private String person;
	private String personShort;
	private String nation;
	private int functionType;
	private String functionShort;
 	private String function;
 	private int shirtNumber;
 	private boolean inStartingLineUp;
 	private String lineUpOutMinute;
 	private String lineUpInMinute;
	
	
	
	public EventLineUpJSON(){}


	
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



	public long getTeamId() 
	{
		return teamId;
	}



	public String getTeam() 
	{
		if(team == null)
		{
			team = "";
			
			Log.w(TAG, "team is null");
		}
		
		return team;
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



	public String getFunctionShort() 
	{
		if(functionShort == null)
		{
			functionShort = "";
			
			Log.w(TAG, "functionShort is null");
		}
		
		return functionShort;
	}

	
	
	public int getFunctionType() 
	{
		return functionType;
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



	public int getShirtNumber() 
	{
		return shirtNumber;
	}



	public boolean isInStartingLineUp() 
	{
		return inStartingLineUp;
	}



	public String getLineUpOutMinute()
	{
		if(lineUpOutMinute == null)
		{
			lineUpOutMinute = "";
			
			// No need to warn (only verbose), since the value can be null
			Log.v(TAG, "lineUpOutMinute is null");
		}
		
		return lineUpOutMinute;
	}



	public String getLineUpInMinute()
	{
		if(lineUpInMinute == null)
		{
			lineUpInMinute = "";
			
			// No need to warn (only verbose), since the value can be null
			Log.v(TAG, "lineUpInMinute is null");
		}
		
		return lineUpInMinute;
	}
}
