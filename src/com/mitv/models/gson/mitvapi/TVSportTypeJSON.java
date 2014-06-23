
package com.mitv.models.gson.mitvapi;



import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import android.util.Log;



public class TVSportTypeJSON
	extends BaseObjectJSON
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
