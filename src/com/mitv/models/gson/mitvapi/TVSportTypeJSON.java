
package com.mitv.models.gson.mitvapi;

import android.util.Log;



public class TVSportTypeJSON 
{
	private static final String	TAG	= TVSportTypeJSON.class.getName();
	
	
	private String sportTypeId;
	private String name;

	
	
	public String getSportTypeId() 
	{
		if(sportTypeId == null)
		{
			sportTypeId = "";
			
			Log.w(TAG, "sportTypeId is null");
		}
		
		return sportTypeId;
	}
	
	
	
	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}
}
