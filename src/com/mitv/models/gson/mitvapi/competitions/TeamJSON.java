
package com.mitv.models.gson.mitvapi.competitions;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class TeamJSON
	extends BaseObjectJSON
{
	private static final String TAG = TeamJSON.class.getName();
	
	
	private long teamId;
	private String displayName;
	private String nation;
	private String nationCode;
	private String description;
	private String teamImageCopyright;
	
	
	
	public TeamJSON(){}


	
	public long getTeamId() 
	{
		return teamId;
	}


	
	public String getDisplayName() 
	{
		if(displayName == null)
		{
			displayName = "";
			
			Log.w(TAG, "displayName is null");
		}
		
		return displayName;
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



	public String getNationCode() 
	{
		if(nationCode == null)
		{
			nationCode = "";
			
			Log.w(TAG, "nationCode is null");
		}
		
		return nationCode;
	}



	public String getDescription() 
	{
		if(description == null)
		{
			description = "";
			
			Log.w(TAG, "description is null");
		}
		
		return description;
	}



	public String getTeamImageCopyright() 
	{
		if(teamImageCopyright == null)
		{
			teamImageCopyright = "";
			
			Log.w(TAG, "teamImageCopyright is null");
		}
		
		return teamImageCopyright;
	}
}
