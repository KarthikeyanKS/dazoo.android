
package com.mitv.models.gson.mitvapi;

import android.util.Log;



public class TVCreditJSON
{	
	private static final String TAG = TVSeriesJSON.class.getName();
	
	
	private String name;
	private String type;

	
	
	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}
	
	
	
	public String getType() 
	{
		if(type == null)
		{
			type = "";
			
			Log.w(TAG, "type is null");
		}
		return type;
	}
}